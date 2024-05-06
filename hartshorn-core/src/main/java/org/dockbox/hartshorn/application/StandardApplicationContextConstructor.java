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

import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.ProcessableApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.application.lifecycle.ObservableApplicationEnvironment;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentRegistry;
import org.dockbox.hartshorn.component.UseProxying;
import org.dockbox.hartshorn.component.processing.ComponentFinalizingPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessor;
import org.dockbox.hartshorn.component.processing.ServiceActivator;
import org.dockbox.hartshorn.inject.LifecycleType;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.SingleElementContext;
import org.dockbox.hartshorn.util.StreamableConfigurer;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.scan.PredefinedSetTypeReferenceCollector;
import org.dockbox.hartshorn.util.introspect.scan.TypeReferenceCollectorContext;
import org.dockbox.hartshorn.util.introspect.scan.classpath.ClassPathScannerTypeReferenceCollector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * TODO: #1060 Add documentation
 *
 * @since 0.4.11
 *
 * @author Guus Lieben
 */
public final class StandardApplicationContextConstructor implements ApplicationContextConstructor {

    private final SingleElementContext<? extends ApplicationBuildContext> initializerContext;
    private final ApplicationBuildContext buildContext;
    private final Configurer configurer;

    private StandardApplicationContextConstructor(SingleElementContext<? extends ApplicationBuildContext> initializerContext, Configurer configurer) {
        this.initializerContext = initializerContext;
        this.buildContext = initializerContext.input();
        this.configurer = configurer;
    }

    @Override
    public ApplicationContext createContext() {
        ApplicationBootstrapContext bootstrapContext = new ApplicationBootstrapContext(
                this.buildContext.mainClass(),
                this.buildContext.arguments(),
                this.buildContext.logger(),
                this.configurer.includeBasePackages.initialize(this.initializerContext)
        );

        SingleElementContext<ApplicationBootstrapContext> bootstrapInitializerContext = this.initializerContext.transform(bootstrapContext);

        ApplicationEnvironment environment = this.configurer.environment.initialize(bootstrapInitializerContext);
        ApplicationContext applicationContext = environment.applicationContext();

        this.configure(applicationContext, bootstrapInitializerContext);
        if (applicationContext instanceof ProcessableApplicationContext activatingApplicationContext) {
            activatingApplicationContext.loadContext();
        }
        this.finalizeContext(applicationContext);

        return applicationContext;
    }

    private void registerHooks(ApplicationContext applicationContext) {
        this.buildContext.logger().debug("Registering shutdown hook for application context");
        ApplicationContextShutdownHook shutdownHook = new ApplicationContextShutdownHook(this.buildContext.logger(), applicationContext);
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook, "ShutdownHook"));
    }

    private void configure(ApplicationContext applicationContext, SingleElementContext<ApplicationBootstrapContext> initializerContext) {
        ApplicationEnvironment environment = applicationContext.environment();

        TypeReferenceCollectorContext collectorContext = new TypeReferenceCollectorContext();

        Set<Annotation> activators = this.serviceActivators(applicationContext, initializerContext);
        this.enhanceTypeReferenceCollectorContext(initializerContext, environment, collectorContext, activators);

        ServiceActivatorContext serviceActivatorContext = new ServiceActivatorContext(applicationContext, activators);
        applicationContext.addContext(serviceActivatorContext);

        Set<ServiceActivator> serviceActivatorAnnotations = activators.stream()
                .map(environment.introspector()::introspect)
                .flatMap(introspected -> introspected.annotations().all(ServiceActivator.class).stream())
                .collect(Collectors.toSet());


        this.buildContext.logger().debug("Registering {} type reference collectors to application context", collectorContext.collectors().size());
        applicationContext.addContext(collectorContext);

        Set<Class<? extends ComponentProcessor>> processorTypes = serviceActivatorAnnotations.stream()
                .flatMap(serviceActivator -> Arrays.stream(serviceActivator.processors()))
                .collect(Collectors.toSet());
        // Create sets for ComponentPreProcessor and ComponentPostProcessor from processorTypes
        Set<Class<? extends ComponentPreProcessor>> preProcessorTypes = extractProcessors(processorTypes, ComponentPreProcessor.class);
        Set<Class<? extends ComponentPostProcessor>> postProcessorTypes = extractProcessors(processorTypes, ComponentPostProcessor.class);
        for (Class<? extends ComponentPostProcessor> postProcessorType : postProcessorTypes) {
            applicationContext.add(postProcessorType);
        }

        Set<ComponentProcessor> componentProcessors = this.componentProcessors(applicationContext, initializerContext, preProcessorTypes);

        this.buildContext.logger().debug("Registering {} component processors to application context", componentProcessors.size());
        for (ComponentProcessor componentProcessor : componentProcessors) {
            applicationContext.add(componentProcessor);
        }
    }

    private static <T extends ComponentProcessor> Set<Class<? extends T>> extractProcessors(Set<Class<? extends ComponentProcessor>> processorTypes, Class<T> processorClass) {
        return processorTypes.stream()
                .filter(processorClass::isAssignableFrom)
                .map(type -> (Class<? extends T>) type)
                .collect(Collectors.toSet());
    }

    private Set<Annotation> serviceActivators(ApplicationContext applicationContext, SingleElementContext<ApplicationBootstrapContext> initializerContext) {
        Set<Annotation> activators = new HashSet<>(this.configurer.activators.initialize(initializerContext));
        Set<Annotation> serviceActivators = new HashSet<>(applicationContext.environment()
                .introspector()
                .introspect(initializerContext.input().mainClass())
                .annotations()
                .annotedWith(ServiceActivator.class));
        activators.addAll(serviceActivators);

        Queue<Annotation> queue = new ArrayDeque<>(activators);
        while(!queue.isEmpty()) {
            Annotation activator = queue.poll();
            Set<Annotation> inheritedActivators = this.serviceActivators(applicationContext.environment(), activator);
            activators.addAll(inheritedActivators);
            queue.addAll(inheritedActivators);
        }

        return activators;
    }

    private Set<Annotation> serviceActivators(ApplicationEnvironment environment, Annotation annotation) {
        TypeView<? extends Annotation> introspected = environment.introspector().introspect(annotation.annotationType());
        Set<Annotation> annotations = introspected.annotations().annotedWith(ServiceActivator.class);

        Set<Annotation> activators = new HashSet<>(annotations);
        for (Annotation activatorAnnotation : annotations) {
            activators.addAll(this.serviceActivators(environment, activatorAnnotation));
        }
        return activators;
    }

    @NonNull
    private Set<ComponentProcessor> componentProcessors(ApplicationContext applicationContext, SingleElementContext<ApplicationBootstrapContext> initializerContext, Set<Class<? extends ComponentPreProcessor>> processorTypes) {
        Set<ComponentProcessor> componentProcessors = new HashSet<>();

        SingleElementContext<@Nullable ApplicationContext> context = initializerContext.transform(applicationContext);
        componentProcessors.addAll(this.configurer.componentPreProcessors.initialize(context));
        componentProcessors.addAll(this.configurer.componentPostProcessors.initialize(context));

        processorTypes.stream()
                .map(applicationContext::get)
                .forEach(componentProcessors::add);

        return componentProcessors;
    }

    private void enhanceTypeReferenceCollectorContext(SingleElementContext<ApplicationBootstrapContext> initializerContext, ApplicationEnvironment environment, TypeReferenceCollectorContext collectorContext,
                                                      Set<Annotation> activators) {
        collectorContext.register(new ClassPathScannerTypeReferenceCollector(Hartshorn.PACKAGE_PREFIX));
        ApplicationBootstrapContext bootstrapContext = initializerContext.input();
        if (bootstrapContext.includeBasePackages()) {
            collectorContext.register(new ClassPathScannerTypeReferenceCollector(bootstrapContext.mainClass().getPackageName()));
        }

        Set<String> prefixes = new HashSet<>(this.configurer.scanPackages.initialize(initializerContext));
        Introspector introspector = environment.introspector();
        for (ServiceActivator serviceActivator : introspector.introspect(bootstrapContext.mainClass()).annotations().all(ServiceActivator.class)) {
            prefixes.addAll(List.of(serviceActivator.scanPackages()));
        }

        for (Annotation serviceActivator : activators) {
            Option<ServiceActivator> activatorCandidate = introspector.introspect(serviceActivator).annotations().get(ServiceActivator.class);
            if (activatorCandidate.absent()) {
                throw new IllegalStateException("Service activator annotation " + serviceActivator + " is not annotated with @ServiceActivator");
            }

            ServiceActivator activator = activatorCandidate.get();
            prefixes.addAll(List.of(activator.scanPackages()));
        }

        prefixes.stream()
                .map(ClassPathScannerTypeReferenceCollector::new)
                .forEach(collectorContext::register);

        Set<Class<?>> standaloneComponents = Set.copyOf(this.configurer.standaloneComponents.initialize(initializerContext));
        if (!standaloneComponents.isEmpty()) {
            collectorContext.register(PredefinedSetTypeReferenceCollector.of(standaloneComponents));
        }
    }

    private void finalizeContext(ApplicationContext applicationContext) {
        this.buildContext.logger().debug("Finalizing application context before releasing to application");
        this.notifyObservers(applicationContext);
        this.activateManagedComponents(applicationContext);
        this.registerHooks(applicationContext);
    }

    private void notifyObservers(ApplicationContext applicationContext) {
        if (applicationContext.environment() instanceof ObservableApplicationEnvironment observable) {
            this.buildContext.logger().debug("Notifying application environment observers of application context creation");
            for (LifecycleObserver observer : observable.observers(LifecycleObserver.class)) {
                observer.onStarted(applicationContext);
            }
        }
    }

    private void activateManagedComponents(ApplicationContext applicationContext) {
        applicationContext.get(ComponentRegistry.class).containers().stream()
            .filter(container -> container.lifecycle() == LifecycleType.SINGLETON)
            .filter(Predicate.not(ComponentContainer::lazy))
            .forEach(container -> {
                this.buildContext.logger().debug("Activating non-lazy singleton {} in application context", container.id());
                applicationContext.get(container.type().type());
            });
    }

    public static ContextualInitializer<ApplicationBuildContext, StandardApplicationContextConstructor> create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);
            return new StandardApplicationContextConstructor(context, configurer);
        };
    }

    /**
     * TODO: #1060 Add documentation
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public static class Configurer {

        private final LazyStreamableConfigurer<ApplicationBootstrapContext, Annotation> activators = LazyStreamableConfigurer.of(
                TypeUtils.annotation(UseBootstrap.class),
                TypeUtils.annotation(UseProxying.class)
        );

        private final LazyStreamableConfigurer<ApplicationContext, ComponentPreProcessor> componentPreProcessors = LazyStreamableConfigurer.empty();
        private final LazyStreamableConfigurer<ApplicationContext, ComponentPostProcessor> componentPostProcessors = LazyStreamableConfigurer.of(collection -> {
            collection.add(ComponentFinalizingPostProcessor.create(Customizer.useDefaults()));
        });

        private final LazyStreamableConfigurer<ApplicationBootstrapContext, Class<?>> standaloneComponents = LazyStreamableConfigurer.empty();
        private final LazyStreamableConfigurer<ApplicationBootstrapContext, String> scanPackages = LazyStreamableConfigurer.empty();

        private ContextualInitializer<ApplicationBootstrapContext, ? extends ApplicationEnvironment> environment = ContextualApplicationEnvironment.create(Customizer.useDefaults());
        private ContextualInitializer<ApplicationBuildContext, Boolean> includeBasePackages = ContextualInitializer.of(true);

        public Configurer activators(Customizer<StreamableConfigurer<ApplicationBootstrapContext, Annotation>> customizer) {
            this.activators.customizer(customizer);
            return this;
        }

        public Configurer componentPreProcessors(Customizer<StreamableConfigurer<ApplicationContext, ComponentPreProcessor>> customizer) {
            this.componentPreProcessors.customizer(customizer);
            return this;
        }

        public Configurer componentPostProcessors(Customizer<StreamableConfigurer<ApplicationContext, ComponentPostProcessor>> customizer) {
            this.componentPostProcessors.customizer(customizer);
            return this;
        }

        public Configurer standaloneComponents(Customizer<StreamableConfigurer<ApplicationBootstrapContext, Class<?>>> customizer) {
            this.standaloneComponents.customizer(customizer);
            return this;
        }

        public Configurer scanPackages(Customizer<StreamableConfigurer<ApplicationBootstrapContext, String>> customizer) {
            this.scanPackages.customizer(customizer);
            return this;
        }

        public Configurer environment(ApplicationEnvironment environment) {
            return this.environment(ContextualInitializer.of(environment));
        }

        public Configurer environment(ContextualInitializer<ApplicationBootstrapContext, ? extends ApplicationEnvironment> environment) {
            this.environment = environment;
            return this;
        }

        public Configurer includeBasePackages(boolean includeBasePackages) {
            return this.includeBasePackages(ContextualInitializer.of(includeBasePackages));
        }

        public Configurer includeBasePackages(ContextualInitializer<ApplicationBuildContext, Boolean> includeBasePackages) {
            this.includeBasePackages = includeBasePackages;
            return this;
        }
    }
}
