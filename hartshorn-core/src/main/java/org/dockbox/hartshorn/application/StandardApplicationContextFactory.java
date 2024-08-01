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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.ProcessableApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.application.lifecycle.ObservableApplicationEnvironment;
import org.dockbox.hartshorn.component.UseProxying;
import org.dockbox.hartshorn.component.condition.Condition;
import org.dockbox.hartshorn.component.processing.ComponentFinalizingPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ServiceActivator;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.SimpleSingleElementContext;
import org.dockbox.hartshorn.util.SingleElementContext;
import org.dockbox.hartshorn.util.StreamableConfigurer;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.scan.PredefinedSetTypeReferenceCollector;
import org.dockbox.hartshorn.util.introspect.scan.TypeReferenceCollectorContext;
import org.dockbox.hartshorn.util.introspect.scan.classpath.ClassPathScannerTypeReferenceCollector;
import org.dockbox.hartshorn.util.option.Option;

/**
 * The standard implementation of an {@link ApplicationContextFactory}. This factory is responsible for creating an
 * application context based on the configuration that is provided by the application build context. The factory is
 * configured by a {@link Configurer} instance, which allows for customization of the application context creation
 * process, without needing to subclass this factory.
 *
 * @since 0.4.11
 *
 * @author Guus Lieben
 */
public class StandardApplicationContextFactory implements ApplicationContextFactory {

    private final SingleElementContext<? extends ApplicationBuildContext> initializerContext;
    private final ApplicationBuildContext buildContext;
    private final Configurer configurer;

    private ComponentProcessorRegistrar componentProcessorRegistrar;
    private ServiceActivatorCollector activatorCollector;

    private StandardApplicationContextFactory(SingleElementContext<? extends ApplicationBuildContext> initializerContext, Configurer configurer) {
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
        this.activatorCollector = new ServiceActivatorCollector(environment.introspector());
        this.componentProcessorRegistrar = new ComponentProcessorRegistrar(this.activatorCollector, this.buildContext);

        this.configure(applicationContext, bootstrapContext);
        if (applicationContext instanceof ProcessableApplicationContext activatingApplicationContext) {
            activatingApplicationContext.loadContext();
        }
        this.finalizeContext(applicationContext);

        return applicationContext;
    }

    /**
     * Configures the application context with all necessary components. This includes service activators, type reference
     * collectors, and component processors.
     *
     * @param applicationContext The application context to configure
     * @param bootstrapContext The bootstrap context that is used to create the application context
     */
    private void configure(ApplicationContext applicationContext, ApplicationBootstrapContext bootstrapContext) {
        ApplicationEnvironment environment = applicationContext.environment();

        Set<Annotation> activators = this.serviceActivators(bootstrapContext);
        ServiceActivatorContext serviceActivatorContext = new ServiceActivatorContext(applicationContext, activators);
        applicationContext.addContext(serviceActivatorContext);

        TypeReferenceCollectorContext collectorContext = new TypeReferenceCollectorContext();
        this.enhanceTypeReferenceCollectorContext(bootstrapContext, environment.introspector(), collectorContext, activators);
        applicationContext.addContext(collectorContext);

        this.registerComponentProcessors(applicationContext, activators);
    }

    /**
     * Registers all component processors that are present in the application configuration. This includes processors that are
     * configured by the application itself, and processors that are present on the main class of the application.
     *
     * @param applicationContext The application context to register the processors to
     * @param activators The set of service activators that are present in the application configuration
     */
    private void registerComponentProcessors(ApplicationContext applicationContext, Set<Annotation> activators) {
        SingleElementContext<@Nullable ApplicationContext> context = SimpleSingleElementContext.create(applicationContext);
        this.componentProcessorRegistrar.withAdditionalProcessors(this.configurer.componentPreProcessors.initialize(context));
        this.componentProcessorRegistrar.withAdditionalProcessors(this.configurer.componentPostProcessors.initialize(context));
        this.componentProcessorRegistrar.registerComponentProcessors(applicationContext, activators);
    }

    /**
     * Collects all service activators that are present in the application configuration. This includes activators that are
     * configured by the application itself, and activators that are present on the main class of the application.
     *
     * @param bootstrapContext The bootstrap context that is used to create the application context
     * @return The set of service activators that are present in the application configuration
     */
    private Set<Annotation> serviceActivators(ApplicationBootstrapContext bootstrapContext) {
        SingleElementContext<ApplicationBootstrapContext> bootstrap = this.initializerContext.transform(bootstrapContext);
        List<Annotation> configuredActivators = this.configurer.activators.initialize(bootstrap).stream()
            .flatMap(activator -> this.activatorCollector.collectServiceActivatorsRecursively(activator).stream())
            .toList();
        Set<Annotation> additionalActivators = this.activatorCollector.serviceActivators(bootstrapContext.mainClass());
        return CollectionUtilities.merge(configuredActivators, additionalActivators);
    }

    /**
     * Enhances the type reference collector context with any collectors that can be derived from the application bootstrap
     * context and the application environment. By default, this includes collectors for all packages that are scanned by the
     * application, and a collector for all standalone components that are present in the configuration.
     *
     * @param bootstrapContext The bootstrap context that is used to create the application context
     * @param introspector The introspector to use for introspection
     * @param collectorContext The collector context to enhance
     * @param activators The activators that are present on the main class
     */
    private void enhanceTypeReferenceCollectorContext(
        ApplicationBootstrapContext bootstrapContext,
        Introspector introspector,
        TypeReferenceCollectorContext collectorContext,
        Set<Annotation> activators
    ) {
        Set<String> prefixes = this.collectPrefixesForRegistering(bootstrapContext, introspector, activators);
        prefixes.stream()
                .map(ClassPathScannerTypeReferenceCollector::new)
                .forEach(collectorContext::register);

        Set<Class<?>> standaloneComponents = Set.copyOf(this.configurer.standaloneComponents.initialize(this.initializerContext.transform(bootstrapContext)));
        if (!standaloneComponents.isEmpty()) {
            collectorContext.register(PredefinedSetTypeReferenceCollector.of(standaloneComponents));
        }
    }

    /**
     * Collects the prefixes that should be used to register components in the application context. This collects prefixes
     * from the main class, and from any service activators that are present in the configuration.
     *
     * @param introspector The introspector to use for introspection
     * @param activators The activators that are present on the main class
     * @return The prefixes that should be used to register components in the application context
     */
    protected Set<String> collectPrefixesForRegistering(
        ApplicationBootstrapContext bootstrapContext,
        Introspector introspector,
        Set<Annotation> activators
    ) {
        Set<String> prefixes = new HashSet<>();
        prefixes.addAll(this.configurer.scanPackages.initialize(this.initializerContext.transform(bootstrapContext)));

        // Not optional, required for the application to function. Note that any configuration that is required for the
        // application can be overridden by the application itself.
        prefixes.add(Hartshorn.PACKAGE_PREFIX);

        // Optional, application may prefer to use alternative packages for scanning. This is configured by the application
        // bootstrap context.
        if (bootstrapContext.includeBasePackages()) {
            prefixes.add(bootstrapContext.mainClass().getPackageName());
        }

        for (Annotation serviceActivator : activators) {
            Option<ServiceActivator> activatorCandidate = introspector.introspect(serviceActivator).annotations().get(ServiceActivator.class);
            if (activatorCandidate.absent()) {
                throw new IllegalStateException("Service activator annotation " + serviceActivator + " is not annotated with @ServiceActivator");
            }

            ServiceActivator activator = activatorCandidate.get();
            prefixes.addAll(List.of(activator.scanPackages()));
        }

        return prefixes;
    }

    /**
     * Finalizes the application context before releasing it to the application. This method is called after the
     * application context itself is ready for use, but any components that are managed by the application context have not
     * yet been activated and/or notified of the application context creation.
     *
     * @param applicationContext The application context that has been created
     */
    private void finalizeContext(ApplicationContext applicationContext) {
        this.buildContext.logger().debug("Finalizing application context before releasing to application");
        this.notifyObservers(applicationContext);
    }

    /**
     * Notifies all observers of the application environment that the application context has been created. This method is
     * called after all components have been registered and processed, but before they are activated and shutdown hooks are
     * registered.
     *
     * @param applicationContext The application context that has been created
     */
    protected void notifyObservers(ApplicationContext applicationContext) {
        if (applicationContext.environment() instanceof ObservableApplicationEnvironment observable) {
            this.buildContext.logger().debug("Notifying application environment observers of application context creation");
            for (LifecycleObserver observer : observable.observers(LifecycleObserver.class)) {
                observer.onStarted(applicationContext);
            }
        }
    }

    /**
     * Creates a new {@link StandardApplicationContextFactory} instance, based on the provided customizer. The result will always
     * be a new instance of {@link StandardApplicationContextFactory}, unless the returned initializer is manually {@link
     * ContextualInitializer#cached() cached}.
     *
     * @param customizer The customizer that is used to configure the application context factory
     * @return A new initializer for {@link StandardApplicationContextFactory} instances
     */
    public static ContextualInitializer<ApplicationBuildContext, StandardApplicationContextFactory> create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);
            return new StandardApplicationContextFactory(context, configurer);
        };
    }

    /**
     * A configurator for the {@link StandardApplicationContextFactory}. This class is used to configure the application
     * context factory, without needing to subclass it.
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

        /**
         * Configures the service activators that are used to collect component processors. By default, this includes the
         * {@link UseBootstrap} and {@link UseProxying} annotations.
         *
         * @param customizer The customizer that is used to configure the service activators
         * @return The current configurator instance
         */
        public Configurer activators(Customizer<StreamableConfigurer<ApplicationBootstrapContext, Annotation>> customizer) {
            this.activators.customizer(customizer);
            return this;
        }

        /**
         * Configures the component pre-processors that are used to process components before they are activated. By default, this
         * contains no pre-processors.
         *
         * @param customizer The customizer that is used to configure the component pre-processors
         * @return The current configurator instance
         */
        public Configurer componentPreProcessors(Customizer<StreamableConfigurer<ApplicationContext, ComponentPreProcessor>> customizer) {
            this.componentPreProcessors.customizer(customizer);
            return this;
        }

        /**
         * Configures the component post-processors that are used to process components after they are instantiated by the container. By
         * default, this contains a {@link ComponentFinalizingPostProcessor} that is used to finalize components.
         *
         * @param customizer The customizer that is used to configure the component post-processors
         * @return The current configurator instance
         */
        public Configurer componentPostProcessors(Customizer<StreamableConfigurer<ApplicationContext, ComponentPostProcessor>> customizer) {
            this.componentPostProcessors.customizer(customizer);
            return this;
        }

        /**
         * Configures the standalone components that should be added to the application, but are not scanned by the application by
         * default. By default, this contains no standalone components.
         *
         * <p><b>Note:</b> Standalone components should typically only be needed in test environments, in other cases it is
         * recommended to use {@link Condition}s to conditionally register components.
         *
         * @param customizer The customizer that is used to configure the standalone components
         * @return The current configurator instance
         */
        public Configurer standaloneComponents(Customizer<StreamableConfigurer<ApplicationBootstrapContext, Class<?>>> customizer) {
            this.standaloneComponents.customizer(customizer);
            return this;
        }

        /**
         * Configures the packages that should be scanned by the application. By default, this contains no packages outside the
         * main class package and the default {@link Hartshorn#PACKAGE_PREFIX Hartshorn package prefix}.
         *
         * @param customizer The customizer that is used to configure the packages that should be scanned
         * @return The current configurator instance
         */
        public Configurer scanPackages(Customizer<StreamableConfigurer<ApplicationBootstrapContext, String>> customizer) {
            this.scanPackages.customizer(customizer);
            return this;
        }

        /**
         * Configures the environment that is used to manage the application context. By default, this uses the {@link
         * ContextualApplicationEnvironment} with default settings.
         *
         * @param environment The environment to use
         * @return The current configurator instance
         */
        public Configurer environment(ApplicationEnvironment environment) {
            return this.environment(ContextualInitializer.of(environment));
        }

        /**
         * Configures the environment that is used to manage the application context. By default, this uses the {@link
         * ContextualApplicationEnvironment} with default settings.
         *
         * @param environment The environment to use
         * @return The current configurator instance
         */
        public Configurer environment(ContextualInitializer<ApplicationBootstrapContext, ? extends ApplicationEnvironment> environment) {
            this.environment = environment;
            return this;
        }

        /**
         * Configures whether the base packages of the main class should be included in the scanning process. By default, this is
         * enabled.
         *
         * @param includeBasePackages Whether the base packages of the main class should be included in the scanning process
         * @return The current configurator instance
         */
        public Configurer includeBasePackages(boolean includeBasePackages) {
            return this.includeBasePackages(ContextualInitializer.of(includeBasePackages));
        }

        /**
         * Configures whether the base packages of the main class should be included in the scanning process. By default, this is
         * enabled.
         *
         * @param includeBasePackages Whether the base packages of the main class should be included in the scanning process
         * @return The current configurator instance
         */
        public Configurer includeBasePackages(ContextualInitializer<ApplicationBuildContext, Boolean> includeBasePackages) {
            this.includeBasePackages = includeBasePackages;
            return this;
        }
    }
}
