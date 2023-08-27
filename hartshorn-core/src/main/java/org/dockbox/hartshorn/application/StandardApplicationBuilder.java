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
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.Initializer;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.StreamableConfigurer;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.List;

public final class StandardApplicationBuilder implements ApplicationBuilder<ApplicationContext> {

    private enum FactoryState {
        WAITING,
        CREATING,
    }

    private final ApplicationBuildContext buildContext;
    private final ApplicationContextConstructor applicationContextConstructor;

    private FactoryState state = FactoryState.WAITING;

    private StandardApplicationBuilder(Configurer configurer) {
        if (configurer.mainClass == null) {
            throw new IllegalArgumentException("Main class must be provided or inferred using #inferMainClass()");
        }

        Class<?> mainClass = configurer.mainClass.initialize();
        this.buildContext = new ApplicationBuildContext(mainClass, configurer.arguments.initialize(mainClass));
        this.applicationContextConstructor = configurer.constructor.initialize(this.buildContext);
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

    public StandardApplicationBuilder loadDefaults() {
        return this;
    }

    public static StandardApplicationBuilder create(Customizer<Configurer> customizer) {
        Configurer configurer = new Configurer();
        customizer.configure(configurer);
        return new StandardApplicationBuilder(configurer);
    }

    public static class Configurer extends ApplicationConfigurer {

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
                StackTraceElement element = stackTrace[2];
                try {
                    return Class.forName(element.getClassName());
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
