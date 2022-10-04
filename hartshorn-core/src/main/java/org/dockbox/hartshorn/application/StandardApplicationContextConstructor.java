/*
 * Copyright 2019-2022 the original author or authors.
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
import org.dockbox.hartshorn.application.context.ClasspathApplicationContext;
import org.dockbox.hartshorn.application.context.ProcessableApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.application.lifecycle.ObservableApplicationEnvironment;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.ComponentType;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public class StandardApplicationContextConstructor implements ApplicationContextConstructor {

    private final Logger logger;

    public StandardApplicationContextConstructor(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public ApplicationContext createContext(final ApplicationBuilder<?, ?> builder) {
        final ApplicationContext applicationContext = this.createNewContext(builder);

        this.configure(applicationContext, builder);
        this.process(applicationContext, builder);
        this.finalize(applicationContext);

        return applicationContext;
    }

    protected void registerHooks(final ApplicationContext applicationContext) {
        logger.debug("Registering shutdown hook for application context");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (!applicationContext.isClosed()) {
                    applicationContext.close();
                }
            } catch (final IOException e) {
                this.logger.error("Failed to close application context", e);
            }
        }, "ShutdownHook"));
    }

    protected ApplicationEnvironment createEnvironment(final InitializingContext context) {
        return context.builder().applicationEnvironment(context);
    }

    protected ApplicationContext createNewContext(final ApplicationBuilder<?, ?> builder) {
        InitializingContext initializingContext = new InitializingContext(null, null, builder);
        final ApplicationEnvironment environment = this.createEnvironment(initializingContext);

        initializingContext = new InitializingContext(environment, null, builder);
        return this.createContext(initializingContext);
    }

    protected ApplicationContext createContext(final InitializingContext context) {
        logger.debug("Creating new application context with environment {}", context.environment().getClass().getSimpleName());
        return new ClasspathApplicationContext(context);
    }

    protected void configure(final ApplicationContext applicationContext, final ApplicationBuilder<?, ?> builder) {
        final ApplicationEnvironment environment = applicationContext.environment();
        final InitializingContext initializingContext = new InitializingContext(environment, applicationContext, builder);
        final ApplicationConfigurator configurator = builder.applicationConfigurator(initializingContext);
        logger.debug("Configuring application context with configurator {}", configurator.getClass().getSimpleName());
        configurator.configure(environment);

        if (applicationContext instanceof ModifiableActivatorHolder modifiable) {
            logger.debug("Registering {} activators to modifiable application context", builder.serviceActivators().size());
            for (final Annotation serviceActivator : builder.serviceActivators())
                modifiable.addActivator(serviceActivator);
        }

        // Always load Hartshorn internals first, to ensure they are available for other activators
        configurator.bind(environment, Hartshorn.PACKAGE_PREFIX);

        final Set<String> scanPrefixes = new HashSet<>(builder.prefixes());
        if (builder.includeBasePackages())
            scanPrefixes.add(builder.mainClass().getPackageName());

        logger.debug("Registering {} prefixes to application context", scanPrefixes.size());
        for (final String prefix : scanPrefixes)
            configurator.bind(environment, prefix);
    }

    protected void process(final ApplicationContext applicationContext, final ApplicationBuilder<?, ?> builder) {
        builder.componentPreProcessors().forEach(applicationContext::add);
        builder.componentPostProcessors().forEach(applicationContext::add);

        if (applicationContext instanceof ProcessableApplicationContext activatingApplicationContext) {
            logger.debug("Processing application context with {} activators", activatingApplicationContext.activators().size());
            activatingApplicationContext.process();
        }
    }

    protected void finalize(final ApplicationContext applicationContext) {
        logger.debug("Finalizing application context before releasing to application");
        if (applicationContext.environment() instanceof ObservableApplicationEnvironment observable) {
            logger.debug("Notifying application environment observers of application context creation");
            for (final LifecycleObserver observer : observable.observers(LifecycleObserver.class))
                observer.onStarted(applicationContext);
        }

        for (final ComponentContainer container : applicationContext.get(ComponentLocator.class).containers(ComponentType.FUNCTIONAL)) {
            logger.debug("Instantiating non-lazy singleton {} in application context", container.id());
            if (container.singleton() && !container.lazy()) {
                applicationContext.get(container.type());
            }
        }

        this.registerHooks(applicationContext);
    }
}
