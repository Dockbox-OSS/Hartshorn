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
import org.dockbox.hartshorn.application.scan.PredefinedSetTypeReferenceCollector;
import org.dockbox.hartshorn.application.scan.TypeReferenceCollectorContext;
import org.dockbox.hartshorn.application.scan.classpath.ClassPathScannerTypeReferenceCollector;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.ComponentType;
import org.dockbox.hartshorn.component.processing.ComponentProcessor;
import org.dockbox.hartshorn.component.processing.ServiceActivator;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        this.logger.debug("Registering shutdown hook for application context");
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
        return context.applicationEnvironment();
    }

    protected ApplicationContext createNewContext(@NotNull final ApplicationBuilder<?, ?> builder) {
        InitializingContext initializingContext = new InitializingContext(null, null, builder);
        final ApplicationEnvironment environment = this.createEnvironment(initializingContext);

        initializingContext = new InitializingContext(environment, null, builder);
        return this.createContext(initializingContext);
    }

    protected ApplicationContext createContext(final InitializingContext context) {
        this.logger.debug("Creating new application context with environment {}", context.environment().getClass().getSimpleName());
        return new ClasspathApplicationContext(context);
    }

    protected void configure(final ApplicationContext applicationContext, final ApplicationBuilder<?, ?> builder) {
        final ApplicationEnvironment environment = applicationContext.environment();
        final InitializingContext initializingContext = new InitializingContext(environment, applicationContext, builder);
        final ApplicationConfigurator configurator = builder.applicationConfigurator(initializingContext);
        this.logger.debug("Configuring application context with configurator {}", configurator.getClass().getSimpleName());
        configurator.configure(environment);

        final TypeReferenceCollectorContext collectorContext = new TypeReferenceCollectorContext();
        this.enhanceTypeReferenceCollectorContext(builder, environment, collectorContext);

        final Set<Annotation> activators = this.serviceActivators(applicationContext, builder);

        final ServiceActivatorContext serviceActivatorContext = new ServiceActivatorContext(applicationContext, activators);
        applicationContext.add(serviceActivatorContext);

        final Set<ServiceActivator> serviceActivatorAnnotations = activators.stream()
                .map(environment::introspect)
                .flatMap(introspected -> introspected.annotations().all(ServiceActivator.class).stream())
                .collect(Collectors.toSet());


        this.logger.debug("Registering {} type reference collectors to application context", collectorContext.collectors().size());
        applicationContext.add(collectorContext);
        final Set<ComponentProcessor> componentProcessors = componentProcessors(applicationContext, builder, serviceActivatorAnnotations);

        this.logger.debug("Registering {} component processors to application context", componentProcessors.size());
        for (final ComponentProcessor componentProcessor : componentProcessors) {
            applicationContext.add(componentProcessor);
        }
    }

    protected Set<Annotation> serviceActivators(final ApplicationContext applicationContext, final ApplicationBuilder<?, ?> builder) {
        final Set<Annotation> activators = builder.serviceActivators();
        final Set<Annotation> serviceActivators = new HashSet<>(applicationContext.environment()
                .introspect(builder.mainClass())
                .annotations()
                .annotedWith(ServiceActivator.class));
        activators.addAll(serviceActivators);

        for (final Annotation activator : activators) {
            activators.addAll(this.serviceActivators(applicationContext.environment(), activator));
        }

        return activators;
    }

    protected Set<Annotation> serviceActivators(final ApplicationEnvironment environment, final Annotation annotation) {
        final TypeView<? extends Annotation> introspected = environment.introspect(annotation.annotationType());
        final Set<Annotation> annotations = introspected.annotations().annotedWith(ServiceActivator.class);

        final Set<Annotation> activators = new HashSet<>(annotations);
        for (final Annotation activatorAnnotation : annotations) {
            activators.addAll(this.serviceActivators(environment, activatorAnnotation));
        }
        return activators;
    }

    @NotNull
    private static Set<ComponentProcessor> componentProcessors(final ApplicationContext applicationContext, final ApplicationBuilder<?, ?> builder, final Set<ServiceActivator> serviceActivators) {

        final Set<Class<? extends ComponentProcessor>> processorTypes = serviceActivators.stream()
                .flatMap(serviceActivator -> Arrays.stream(serviceActivator.processors()))
                .collect(Collectors.toSet());

        final Set<ComponentProcessor> componentProcessors = new HashSet<>();
        componentProcessors.addAll(builder.componentPreProcessors());
        componentProcessors.addAll(builder.componentPostProcessors());

        processorTypes.stream()
                .map(applicationContext::get)
                .forEach(componentProcessors::add);

        return componentProcessors;
    }

    protected void enhanceTypeReferenceCollectorContext(final ApplicationBuilder<?, ?> builder, final ApplicationEnvironment environment, final TypeReferenceCollectorContext collectorContext) {
        collectorContext.register(new ClassPathScannerTypeReferenceCollector(environment, Hartshorn.PACKAGE_PREFIX));
        if (builder.includeBasePackages()) {
            collectorContext.register(new ClassPathScannerTypeReferenceCollector(environment, builder.mainClass().getPackageName()));
        }

        final Set<String> prefixes = new HashSet<>(builder.scanPackages());
        for (final ServiceActivator serviceActivator : environment.introspect(builder.mainClass()).annotations().all(ServiceActivator.class)) {
            prefixes.addAll(List.of(serviceActivator.scanPackages()));
        }

        for (final Annotation serviceActivator : builder.serviceActivators()) {
            final Option<ServiceActivator> activatorCandidate = environment.introspect(serviceActivator).annotations().get(ServiceActivator.class);
            if (activatorCandidate.absent()) throw new IllegalStateException("Service activator annotation " + serviceActivator + " is not annotated with @ServiceActivator");

            final ServiceActivator activator = activatorCandidate.get();
            prefixes.addAll(List.of(activator.scanPackages()));
        }

        prefixes.stream()
                .map(prefix -> new ClassPathScannerTypeReferenceCollector(environment, prefix))
                .forEach(collectorContext::register);

        if (!builder.standaloneComponents().isEmpty()) {
            collectorContext.register(PredefinedSetTypeReferenceCollector.of(builder.standaloneComponents()));
        }
    }

    protected void process(final ApplicationContext applicationContext, final ApplicationBuilder<?, ?> builder) {
        builder.componentPreProcessors().forEach(applicationContext::add);
        builder.componentPostProcessors().forEach(applicationContext::add);

        if (applicationContext instanceof ProcessableApplicationContext activatingApplicationContext) {
            activatingApplicationContext.loadContext();
        }
    }

    protected void finalize(final ApplicationContext applicationContext) {
        this.logger.debug("Finalizing application context before releasing to application");
        if (applicationContext.environment() instanceof ObservableApplicationEnvironment observable) {
            this.logger.debug("Notifying application environment observers of application context creation");
            for (final LifecycleObserver observer : observable.observers(LifecycleObserver.class))
                observer.onStarted(applicationContext);
        }

        for (final ComponentContainer container : applicationContext.get(ComponentLocator.class).containers(ComponentType.FUNCTIONAL)) {
            this.logger.debug("Instantiating non-lazy singleton {} in application context", container.id());
            if (container.singleton() && !container.lazy()) {
                applicationContext.get(container.type());
            }
        }

        this.registerHooks(applicationContext);
    }
}
