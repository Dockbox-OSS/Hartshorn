/*
 * Copyright 2019-2024 the original author or authors.
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

import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.InvalidActivationSourceException;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.Initializer;
import org.dockbox.hartshorn.util.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.SingleElementContext;
import org.dockbox.hartshorn.util.StreamableConfigurer;

/**
 * A standard implementation of {@link ApplicationBuilder}. This implementation uses a {@link ApplicationContextFactory}
 * to create a new {@link ApplicationContext}. The factory is responsible for the creation-, initialization- and
 * configuration of the context. The builder will provide the required build context to the factory.
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
 * @see ApplicationContextFactory
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public final class StandardApplicationBuilder implements ApplicationBuilder<ApplicationContext> {

    /**
     * The state of the factory. This is used to prevent multiple threads from creating a new application context at the
     * same time, even though {@link #create()} is synchronized.
     *
     * @since 0.4.8
     *
     * @author Guus Lieben
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
    private final ApplicationContextFactory applicationContextFactory;

    private volatile FactoryState state = FactoryState.WAITING;

    private StandardApplicationBuilder(Configurer configurer) {
        if(configurer.mainClass == null) {
            throw new IllegalArgumentException("Main class must be provided or inferred using #inferMainClass()");
        }

        Class<?> mainClass = configurer.mainClass.initialize();
        if(!this.isValidActivator(mainClass)) {
            throw new InvalidActivationSourceException("Main class (%s) must be a valid activator".formatted(mainClass.getName()));
        }

        SingleElementContext<? extends Class<?>> initializerContext = new ApplicationInitializerContext<>(mainClass).initializeInitial();
        this.buildContext = new ApplicationBuildContext(mainClass, configurer.arguments.initialize(initializerContext));

        SingleElementContext<ApplicationBuildContext> buildInitializerContext = initializerContext.transform(this.buildContext);
        this.applicationContextFactory = configurer.applicationContextFactory.initialize(buildInitializerContext);
    }

    /**
     * Validates if the provided class is a valid activator. A valid activator is a class that is not abstract, not an
     * interface, not an array, not a primitive, not a local class, not a member class, and not in a reserved package.
     *
     * @param mainClass The class to validate.
     *
     * @return {@code true} if the provided class is a valid activator, {@code false} otherwise.
     *
     * @see #RESERVED_PACKAGES
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

        ApplicationStartupLogger logger = new ApplicationStartupLogger(this.buildContext);
        logger.logStartup();
        long applicationStartTimestamp = System.currentTimeMillis();
        ApplicationContext applicationContext = this.applicationContextFactory.createContext();
        long applicationStartedTimestamp = System.currentTimeMillis();

        final Duration startupTime = Duration.ofMillis(applicationStartedTimestamp - applicationStartTimestamp);
        logger.logStarted(startupTime);

        this.state = FactoryState.WAITING;

        return applicationContext;
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
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public static class Configurer {

        private ContextualInitializer<ApplicationBuildContext, ? extends ApplicationContextFactory> applicationContextFactory = StandardApplicationContextFactory.create(
                Customizer.useDefaults());
        private final LazyStreamableConfigurer<Class<?>, String> arguments = LazyStreamableConfigurer.empty();
        private Initializer<Class<?>> mainClass;

        /**
         * Sets the factory that is used to create the {@link ApplicationContext}. The provided factory is expected
         * to be capable of creating a new {@link ApplicationContext} instance, and to initialize it without additional context.
         *
         * <p>Note that the provided factory does not receive any context. If the factory requires context, use
         * {@link #applicationContextFactory(ContextualInitializer)} instead.
         *
         * @param factory The factory that is used to create the {@link ApplicationContext}.
         * @return This {@link Configurer} instance.
         */
        public Configurer applicationContextFactory(ApplicationContextFactory factory) {
            return this.applicationContextFactory(Initializer.of(factory));
        }

        /**
         * Sets the factory that is used to create the {@link ApplicationContext}. The provided factory is expected
         * to be capable of creating a new {@link ApplicationContext} instance, and to initialize it without additional context.
         *
         * <p>Note that the provided factory does not receive any context. If the factory requires context, use
         * {@link #applicationContextFactory(ContextualInitializer)} instead.
         *
         * <p>The provided factory will be initialized immediately when the builder is created.
         *
         * @param applicationContextFactory The factory that is used to create the {@link ApplicationContext}.
         * @return This {@link Configurer} instance.
         */
        public Configurer applicationContextFactory(Initializer<ApplicationContextFactory> applicationContextFactory) {
            return this.applicationContextFactory(ContextualInitializer.of(applicationContextFactory));
        }

        /**
         * Sets the factory that is used to create the {@link ApplicationContext}. The provided factory is expected
         * to be capable of creating a new {@link ApplicationContext} instance, and to initialize it with the provided {@link ApplicationBuildContext}.
         *
         * <p>The {@link ApplicationBuildContext} will contain basic information about the application that is being created. This
         * includes the main class, and the arguments that were provided to the application.
         *
         * @param applicationContextFactory The factory that is used to create the {@link ApplicationContext}.
         * @return This {@link Configurer} instance.
         *
         * @see ApplicationBuildContext
         */
        public Configurer applicationContextFactory(ContextualInitializer<ApplicationBuildContext, ? extends ApplicationContextFactory> applicationContextFactory) {
            this.applicationContextFactory = applicationContextFactory;
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
         * @param namesToSkip The names of classes that should be skipped when inferring the main class.
         *
         * @return This {@link Configurer} instance.
         */
        public Configurer inferMainClass(String... namesToSkip) {
            return this.mainClass(() -> {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                Set<String> skip = new HashSet<>(getClassNamesToSkipForInferring());
                skip.addAll(Arrays.asList(namesToSkip));

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

        @NonNull
        private static Set<String> getClassNamesToSkipForInferring() {
            return Set.of(
                StandardApplicationBuilder.class.getName(),
                Configurer.class.getName(),
                ApplicationBuilder.class.getName(),
                HartshornApplication.class.getName(),
                Customizer.class.getName(),
                Thread.class.getName(),
                HartshornApplication.ApplicationBootstrap.class.getName(),
                HartshornApplicationConfigurer.class.getName()
            );
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
