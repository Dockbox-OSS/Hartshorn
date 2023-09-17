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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.InvalidActivationSourceException;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.Initializer;
import org.dockbox.hartshorn.util.InitializerContext;
import org.dockbox.hartshorn.util.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.StreamableConfigurer;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public final class StandardApplicationBuilder implements ApplicationBuilder<ApplicationContext> {

    private enum FactoryState {
        WAITING,
        CREATING,
    }

    private static final Set<String> RESERVED_PACKAGES = Set.of(
            "java.",
            "javax.",
            "sun.",
            "com.sun.",
            "jdk."
    );

    private final ApplicationBuildContext buildContext;
    private final ApplicationContextConstructor applicationContextConstructor;

    private FactoryState state = FactoryState.WAITING;

    private StandardApplicationBuilder(Configurer configurer) {
        if (configurer.mainClass == null) {
            throw new IllegalArgumentException("Main class must be provided or inferred using #inferMainClass()");
        }

        Class<?> mainClass = configurer.mainClass.initialize();
        if (!this.isValidActivator(mainClass)) {
            throw new InvalidActivationSourceException("Main class must be a valid activator");
        }

        InitializerContext<? extends Class<?>> initializerContext = new ApplicationInitializerContext<>(mainClass).initializeInitial();
        this.buildContext = new ApplicationBuildContext(mainClass, configurer.arguments.initialize(initializerContext));

        InitializerContext<ApplicationBuildContext> buildInitializerContext = initializerContext.transform(this.buildContext);
        this.applicationContextConstructor = configurer.constructor.initialize(buildInitializerContext);
    }

    private boolean isValidActivator(Class<?> mainClass) {
        boolean isConcrete = !(mainClass.isPrimitive() || Modifier.isAbstract(mainClass.getModifiers()) || mainClass.isInterface() || mainClass.isArray());
        if (!isConcrete) {
            return false;
        }

        if (mainClass.isLocalClass() || mainClass.isMemberClass()) {
            return false;
        }

        String packageName = mainClass.getPackageName();
        for (String reservedPackage : RESERVED_PACKAGES) {
            if (packageName.startsWith(reservedPackage)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ApplicationContext create() {
        if (this.state == FactoryState.CREATING) {
            throw new IllegalStateException("Application factory is already creating a new application context");
        }
        this.state = FactoryState.CREATING;

        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        this.buildContext.logger().info("Starting application " + this.buildContext.mainClass().getSimpleName() + " on " + this.host(runtimeMXBean) + " using Java " + runtimeMXBean.getVmVersion() + " with PID " + runtimeMXBean.getPid());

        long applicationStartTimestamp = System.currentTimeMillis();
        ApplicationContext applicationContext = this.applicationContextConstructor.createContext();
        long applicationStartedTimestamp = System.currentTimeMillis();

        double startupTime = ((double) (applicationStartedTimestamp - applicationStartTimestamp)) / 1000;
        double jvmUptime = ((double) runtimeMXBean.getUptime()) / 1000;

        this.buildContext.logger().info("Started " + Hartshorn.PROJECT_NAME + " in " + startupTime + " seconds (JVM running for " + jvmUptime + ")");

        this.state = FactoryState.WAITING;

        return applicationContext;
    }

    private String host(RuntimeMXBean runtimeMXBean) {
        // Alternative to InetAddress.getLocalHost().getHostName()
        return runtimeMXBean.getName().split("@")[1];
    }

    public static StandardApplicationBuilder create(Customizer<Configurer> customizer) {
        Configurer configurer = new Configurer();
        customizer.configure(configurer);
        return new StandardApplicationBuilder(configurer);
    }

    public static class Configurer {

        private ContextualInitializer<ApplicationBuildContext, ? extends ApplicationContextConstructor> constructor = StandardApplicationContextConstructor.create(Customizer.useDefaults());
        private final LazyStreamableConfigurer<Class<?>, String> arguments = LazyStreamableConfigurer.empty();
        private Initializer<Class<?>> mainClass;

        public Configurer constructor(ApplicationContextConstructor constructor) {
            return this.constructor(Initializer.of(constructor));
        }

        public Configurer constructor(Initializer<ApplicationContextConstructor> constructor) {
            return this.constructor(ContextualInitializer.of(constructor));
        }

        public Configurer constructor(ContextualInitializer<ApplicationBuildContext, ? extends ApplicationContextConstructor> constructor) {
            this.constructor = constructor;
            return this;
        }

        public Configurer mainClass(Class<?> mainClass) {
            return this.mainClass(Initializer.of(mainClass));
        }

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
                catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Could not deduce main class", e);
                }
            });
        }

        public Configurer mainClass(Initializer<Class<?>> mainClass) {
            this.mainClass = mainClass;
            return this;
        }

        public Configurer arguments(String... arguments) {
            return this.arguments(Arrays.asList(arguments));
        }

        public Configurer arguments(List<String> arguments) {
            return this.arguments(args -> {
                args.clear();
                args.addAll(arguments);
            });
        }

        public Configurer arguments(Customizer<StreamableConfigurer<Class<?>, String>> customizer) {
            this.arguments.customizer(customizer);
            return this;
        }
    }
}
