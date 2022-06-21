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
import org.dockbox.hartshorn.application.environment.ApplicationManager;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.application.lifecycle.ObservableApplicationManager;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.ComponentType;
import org.dockbox.hartshorn.context.ModifiableContextCarrier;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;

public class StandardApplicationContextConstructor implements ApplicationContextConstructor<ApplicationContext> {

    private final Logger logger;

    public StandardApplicationContextConstructor(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public ApplicationContext createContext(final ApplicationContextConfiguration configuration) {
        final ApplicationManager manager = this.createManager(configuration);
        final ApplicationContext applicationContext = this.createContext(manager, configuration);

        this.configure(manager, applicationContext, configuration);
        this.process(applicationContext, configuration);
        this.finalize(applicationContext, manager);

        return applicationContext;
    }

    protected void registerHooks(final ApplicationContext applicationContext) {
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

    protected Activator activatorAnnotation(final ApplicationContextConfiguration configuration) {
        return configuration.activator().annotation(Activator.class).get();
    }

    protected ApplicationManager createManager(final ApplicationContextConfiguration configuration) {
        final InitializingContext context = new InitializingContext(null, null, null, configuration);
        return configuration.manager(context);
    }

    protected ApplicationEnvironment createEnvironment(final InitializingContext context) {
        final ApplicationEnvironment environment = context.configuration().applicationEnvironment(context);
        context.configuration().prefixes().forEach(environment.prefixContext()::prefix);
        return environment;
    }

    protected ApplicationContext createContext(final ApplicationManager manager, final ApplicationContextConfiguration configuration) {
        InitializingContext initializingContext = new InitializingContext(null, null, manager, configuration);
        final ApplicationEnvironment environment = this.createEnvironment(initializingContext);

        initializingContext = new InitializingContext(environment, null, manager, configuration);
        return this.createContext(initializingContext);
    }

    protected ApplicationContext createContext(final InitializingContext context) {
        return new ClasspathApplicationContext(context);
    }

    protected void configure(final ApplicationManager manager, final ApplicationContext applicationContext, final ApplicationContextConfiguration configuration) {
        if (manager instanceof ModifiableContextCarrier modifiable) {
            modifiable.applicationContext(applicationContext);
        }

        final InitializingContext initializingContext = new InitializingContext(applicationContext.environment(), applicationContext, manager, configuration);
        final ApplicationConfigurator configurator = configuration.applicationConfigurator(initializingContext);
        configurator.configure(manager);

        if (applicationContext instanceof ModifiableActivatorHolder modifiable) {
            for (final Annotation serviceActivator : configuration.serviceActivators())
                modifiable.addActivator(serviceActivator);
        }

        // Always load Hartshorn internals first
        configurator.bind(manager, Hartshorn.PACKAGE_PREFIX);

        final Activator activator = this.activatorAnnotation(configuration);
        final Set<String> scanPackages = Set.of(activator.scanPackages());
        final Collection<String> scanPrefixes = CollectionUtilities.merge(configuration.prefixes(), scanPackages);

        if (activator.includeBasePackage())
            scanPrefixes.add(configuration.activator().type().getPackageName());

        for (final String prefix : scanPrefixes)
            configurator.bind(manager, prefix);
    }

    protected void process(final ApplicationContext applicationContext, final ApplicationContextConfiguration configuration) {
        configuration.componentPreProcessors().forEach(applicationContext::add);
        configuration.componentPostProcessors().forEach(applicationContext::add);

        if (applicationContext instanceof ProcessableApplicationContext activatingApplicationContext) {
            activatingApplicationContext.process();
        }
    }

    protected void finalize(final ApplicationContext applicationContext, final ApplicationManager manager) {
        if (manager instanceof ObservableApplicationManager observable) {
            for (final LifecycleObserver observer : observable.observers())
                observer.onStarted(applicationContext);
        }

        for (final ComponentContainer container : applicationContext.get(ComponentLocator.class).containers(ComponentType.FUNCTIONAL)) {
            if (container.singleton() && !container.lazy()) {
                applicationContext.get(container.type());
            }
        }

        this.registerHooks(applicationContext);
    }
}
