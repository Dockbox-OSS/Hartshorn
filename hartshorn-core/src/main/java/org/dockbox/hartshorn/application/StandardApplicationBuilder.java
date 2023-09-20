/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.application;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.InvalidActivationSourceException;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.Initializer;
import org.dockbox.hartshorn.util.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.SingleElementContext;
import org.dockbox.hartshorn.util.StreamableConfigurer;

/**
 * A standard implementation of {@link ApplicationBuilder}. This implementation uses a {@link ApplicationContextConstructor}
 * to create a new {@link ApplicationContext}. The constructor is responsible for the creation-, initialization- and
 * configuration of the context. The builder will provide the required build context to the constructor.
 *
 * <p>The creation of new applications is thread-safe through synchronization of the {@link #create()} method. That is,
 * multiple threads may call {@link #create()} concurrently, but only one thread will be able to create a new application
 * at a time. However, it should be noted that the builder is re-entrant. That is, a single instance of this class may be
 * used to create multiple applications with the same configuration.
 *
 * <p>It is possible to create multiple instances of this class, and use them concurrently. However, this is only recommended
 * if the instances are configured to create different types of applications. If multiple instances are used to create the
 * same type of application, it is recommended to re-use a single builder instance.
 *
 * @see ApplicationContextConstructor
 *
 * @author Guus Lieben
 * @since 0.4.8
 */
public final class StandardApplicationBuilder implements ApplicationBuilder<ApplicationContext> {

    /**
     * The state of the factory. This is used to prevent multiple threads from creating a new application context at the
     * same time, even though {@link #create()} is synchronized.
     */
    private enum FactoryState {
        /**
         * The factory is waiting for a new application context to be created. This is the default state.
         */
        WAITING,

        /**
         * The factory is creating a new application context. This state is used to prevent multiple threads from creating
         * a new application context at the same time.
         */
        CREATING,
    }

    /**
     * A set of packages that are reserved for the JVM and should not be used for application classes.
     * This is used to prevent the creation of applications that are named after a reserved package.
     */
    private static final Set<String> RESERVED_PACKAGES = Set.of(
            "java.",
            "javax.",
            "sun.",
            "com.sun.",
            "jdk."
    );

    private final ApplicationBuildContext buildContext;
    private final ApplicationContextConstructor applicationContextConstructor;

    private volatile FactoryState state = FactoryState.WAITING;

    private StandardApplicationBuilder(Configurer configurer) {
        if(configurer.mainClass == null) {
            throw new IllegalArgumentException("Main class must be provided or inferred using #inferMainClass()");
        }

        Class<?> mainClass = configurer.mainClass.initialize();
        if(!this.isValidActivator(mainClass)) {
            throw new InvalidActivationSourceException("Main class must be a valid activator");
        }

        SingleElementContext<? extends Class<?>> initializerContext = new ApplicationInitializerContext<>(mainClass).initializeInitial();
        this.buildContext = new ApplicationBuildContext(mainClass, configurer.arguments.initialize(initializerContext));

        SingleElementContext<ApplicationBuildContext> buildInitializerContext = initializerContext.transform(this.buildContext);
        this.applicationContextConstructor = configurer.constructor.initialize(buildInitializerContext);
    }

    /**
     * Validates if the provided class is a valid activator. A valid activator is a class that is not abstract, not an
     * interface, not an array, not a primitive, not a local class, not a member class, and not in a reserved package.
     *
     * @see #RESERVED_PACKAGES
     *
     * @param mainClass The class to validate.
     * @return {@code true} if the provided class is a valid activator, {@code false} otherwise.
     */
    private boolean isValidActivator(Class<?> mainClass) {
        boolean isConcrete = !(mainClass.isPrimitive() || Modifier.isAbstract(mainClass.getModifiers()) || mainClass.isInterface()
                || mainClass.isArray());
        if(!isConcrete) {
            return false;
        }

        if(mainClass.isLocalClass() || mainClass.isMemberClass()) {
            return false;
        }

        String packageName = mainClass.getPackageName();
        for(String reservedPackage : RESERVED_PACKAGES) {
            if(packageName.startsWith(reservedPackage)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public synchronized ApplicationContext create() {
        if(this.state == FactoryState.CREATING) {
            throw new IllegalStateException("Application factory is already creating a new application context");
        }
        this.state = FactoryState.CREATING;

        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        this.buildContext.logger()
                .info("Starting application " + this.buildContext.mainClass().getSimpleName() + " on " + this.host(runtimeMXBean)
                        + " using Java " + runtimeMXBean.getVmVersion() + " with PID " + runtimeMXBean.getPid());

        long applicationStartTimestamp = System.currentTimeMillis();
        ApplicationContext applicationContext = this.applicationContextConstructor.createContext();
        long applicationStartedTimestamp = System.currentTimeMillis();

        double startupTime = ((double) (applicationStartedTimestamp - applicationStartTimestamp)) / 1000;
        double jvmUptime = ((double) runtimeMXBean.getUptime()) / 1000;

        this.buildContext.logger()
                .info("Started " + Hartshorn.PROJECT_NAME + " in " + startupTime + " seconds (JVM running for " + jvmUptime + ")");

        this.state = FactoryState.WAITING;

        return applicationContext;
    }

    /**
     * Returns the hostname of the system on which the application is running. This is determined by the {@link RuntimeMXBean}
     * that is provided as an argument. The hostname is extracted from the {@link RuntimeMXBean#getName()}.
     *
     * <p>For example, if the {@link RuntimeMXBean#getName()} returns {@code root@hartshorn-ci}, the returned value will be
     * {@code hartshorn-ci}.
     *
     * @param runtimeMXBean The {@link RuntimeMXBean} that provides information about the JVM.
     * @return The hostname of the system on which the application is running.
     */
    private String host(RuntimeMXBean runtimeMXBean) {
        // Alternative to InetAddress.getLocalHost().getHostName()
        return runtimeMXBean.getName().split("@")[1];
    }

    /**
     * Creates a new {@link StandardApplicationBuilder} instance with the provided {@link Configurer}. The provided
     * {@link Configurer} is used to configure the builder before it is used to create a new application.
     *
     * @param customizer The {@link Configurer} that is used to configure the builder.
     * @return A new {@link StandardApplicationBuilder} instance.
     */
    public static StandardApplicationBuilder create(Customizer<Configurer> customizer) {
        Configurer configurer = new Configurer();
        customizer.configure(configurer);
        return new StandardApplicationBuilder(configurer);
    }

    /**
     * Configuration options for {@link StandardApplicationBuilder}. This class is used to configure the builder before
     * it is used to create a new application. The configuration may be customized by providing a {@link Customizer} to
     * the {@link #create(Customizer)} method.
     *
     * @see #create(Customizer)
     *
     * @author Guus Lieben
     * @since 0.5.0
     */
    public static class Configurer {

        private ContextualInitializer<ApplicationBuildContext, ? extends ApplicationContextConstructor> constructor = StandardApplicationContextConstructor.create(
                Customizer.useDefaults());
        private final LazyStreamableConfigurer<Class<?>, String> arguments = LazyStreamableConfigurer.empty();
        private Initializer<Class<?>> mainClass;

        /**
         * Sets the constructor that is used to create the {@link ApplicationContext}. The provided constructor is expected
         * to be capable of creating a new {@link ApplicationContext} instance, and to initialize it without additional context.
         *
         * <p>Note that the provided constructor does not receive any context. If the constructor requires context, use
         * {@link #constructor(ContextualInitializer)} instead.
         *
         * @param constructor The constructor that is used to create the {@link ApplicationContext}.
         * @return This {@link Configurer} instance.
         */
        public Configurer constructor(ApplicationContextConstructor constructor) {
            return this.constructor(Initializer.of(constructor));
        }

        /**
         * Sets the constructor that is used to create the {@link ApplicationContext}. The provided constructor is expected
         * to be capable of creating a new {@link ApplicationContext} instance, and to initialize it without additional context.
         *
         * <p>Note that the provided constructor does not receive any context. If the constructor requires context, use
         * {@link #constructor(ContextualInitializer)} instead.
         *
         * <p>The provided constructor will be initialized immediately when the builder is created.
         *
         * @param constructor The constructor that is used to create the {@link ApplicationContext}.
         * @return This {@link Configurer} instance.
         */
        public Configurer constructor(Initializer<ApplicationContextConstructor> constructor) {
            return this.constructor(ContextualInitializer.of(constructor));
        }

        /**
         * Sets the constructor that is used to create the {@link ApplicationContext}. The provided constructor is expected
         * to be capable of creating a new {@link ApplicationContext} instance, and to initialize it with the provided {@link ApplicationBuildContext}.
         *
         * <p>The {@link ApplicationBuildContext} will contain basic information about the application that is being created. This
         * includes the main class, and the arguments that were provided to the application.
         *
         * @param constructor The constructor that is used to create the {@link ApplicationContext}.
         * @return This {@link Configurer} instance.
         *
         * @see ApplicationBuildContext
         */
        public Configurer constructor(ContextualInitializer<ApplicationBuildContext, ? extends ApplicationContextConstructor> constructor) {
            this.constructor = constructor;
            return this;
        }

        /**
         * Sets the main class of the application that will be created. The main class is expected to be a valid activator.
         *
         * @param mainClass The main class of the application that will be created.
         * @return This {@link Configurer} instance.
         *
         * @see #isValidActivator(Class)
         */
        public Configurer mainClass(Class<?> mainClass) {
            return this.mainClass(Initializer.of(mainClass));
        }

        /**
         * Configures the initializer of the main class to be used to determine the main class of the application that will be created.
         *
         * <p>The initializer is called when the builder is created, and will use the stacktrace at that point in time to determine
         * the main class. This is useful when the main class is not known at compile time, but is determined at runtime.
         *
         * <p>When the initializer is called, the stacktrace is filtered to remove all elements that are part of the builder itself,
         * and all elements that are part of the JDK. The first element that is not part of the builder, and not part of the JDK is
         * used as the main class. If no such element is found, an {@link IllegalStateException} is thrown.
         *
         * @return This {@link Configurer} instance.
         */
        public Configurer inferMainClass() {
            return this.mainClass(() -> {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                Set<String> skip = Set.of(
                        StandardApplicationBuilder.class.getName(),
                        Configurer.class.getName(),
                        ApplicationBuilder.class.getName(),
                        HartshornApplication.class.getName(),
                        Customizer.class.getName(),
                        Thread.class.getName()
                );

                StackTraceElement target = Arrays.stream(stackTrace)
                        .filter(element -> !skip.contains(element.getClassName()))
                        .filter(element -> !element.getClassName().contains("lambda$"))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Could not deduce main class, no suitable stack trace element found"));

                try {
                    return Class.forName(target.getClassName());
                }
                catch(ClassNotFoundException e) {
                    throw new IllegalStateException("Could not deduce main class", e);
                }
            });
        }

        /**
         * Sets the initializer of the main class of the application that will be created. The initializer is expected to
         * return a valid activator. The initializer is called when the builder is created.
         *
         * @param mainClass The initializer of the main class of the application that will be created.
         * @return This {@link Configurer} instance.
         */
        public Configurer mainClass(Initializer<Class<?>> mainClass) {
            this.mainClass = mainClass;
            return this;
        }

        /**
         * Sets the arguments that are provided to the application that will be created. The provided arguments are expected
         * to be valid arguments for the main class of the application.
         *
         * @param arguments The arguments that are provided to the application that will be created.
         * @return This {@link Configurer} instance.
         */
        public Configurer arguments(String... arguments) {
            return this.arguments(Arrays.asList(arguments));
        }

        /**
         * Sets the arguments that are provided to the application that will be created. The provided arguments are expected
         * to be valid arguments for the main class of the application.
         *
         * @param arguments The arguments that are provided to the application that will be created.
         * @return This {@link Configurer} instance.
         */
        public Configurer arguments(List<String> arguments) {
            return this.arguments(args -> {
                args.clear();
                args.addAll(arguments);
            });
        }

        /**
         * Configures the arguments that are provided to the application that will be created. The provided arguments are expected
         * to be valid arguments for the main class of the application.
         *
         * @param customizer The {@link Customizer} that is used to configure the arguments.
         * @return This {@link Configurer} instance.
         */
        public Configurer arguments(Customizer<StreamableConfigurer<Class<?>, String>> customizer) {
            this.arguments.customizer(customizer);
            return this;
        }
    }
}
