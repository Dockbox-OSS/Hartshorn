/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.launchpad.environment;

import org.dockbox.hartshorn.context.SingleElementContext;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentKeyResolver;
import org.dockbox.hartshorn.inject.ExceptionHandler;
import org.dockbox.hartshorn.inject.ImmutableInjectorConfiguration;
import org.dockbox.hartshorn.inject.InjectorConfiguration;
import org.dockbox.hartshorn.inject.LoggingExceptionHandler;
import org.dockbox.hartshorn.inject.StandardAnnotationComponentKeyResolver;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.inject.component.ApplicationMainComponentContainer;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.inject.condition.ConditionMatcher;
import org.dockbox.hartshorn.inject.environment.DefaultProxyOrchestratorLoader;
import org.dockbox.hartshorn.inject.targets.ComponentInjectionPointsResolver;
import org.dockbox.hartshorn.inject.targets.MethodsAndFieldsInjectionPointResolver;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.DelegatingApplicationContext;
import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.dockbox.hartshorn.launchpad.HartshornApplicationConfigurer;
import org.dockbox.hartshorn.launchpad.SimpleApplicationContext;
import org.dockbox.hartshorn.launchpad.banner.Banner;
import org.dockbox.hartshorn.launchpad.banner.HartshornLogoBanner;
import org.dockbox.hartshorn.launchpad.banner.ResourcePathBanner;
import org.dockbox.hartshorn.launchpad.component.TypeReferenceLookupComponentRegistry;
import org.dockbox.hartshorn.launchpad.context.ModifiableApplicationContextCarrier;
import org.dockbox.hartshorn.launchpad.launch.ApplicationBootstrapContext;
import org.dockbox.hartshorn.launchpad.launch.StandardApplicationContextFactory;
import org.dockbox.hartshorn.launchpad.lifecycle.ObservableApplicationEnvironment;
import org.dockbox.hartshorn.launchpad.lifecycle.Observer;
import org.dockbox.hartshorn.launchpad.properties.EnvironmentProfilesPropertyRegistryFactory;
import org.dockbox.hartshorn.launchpad.properties.PropertyRegistryFactory;
import org.dockbox.hartshorn.launchpad.resources.ResourceLookup;
import org.dockbox.hartshorn.launchpad.resources.StrategyResourceLookup;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.proxy.ProxyOrchestrator;
import org.dockbox.hartshorn.spi.DiscoveryService;
import org.dockbox.hartshorn.spi.ServiceDiscoveryException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.IOUtilities;
import org.dockbox.hartshorn.util.collections.ConcurrentSetTreeMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.configure.ContextualInitializer;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.util.configure.Initializer;
import org.dockbox.hartshorn.util.introspect.BatchCapableIntrospector;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.IntrospectorLoader;
import org.dockbox.hartshorn.util.introspect.ProxyLookup;
import org.dockbox.hartshorn.util.introspect.SupplierAdapterProxyLookup;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;
import org.dockbox.hartshorn.util.introspect.annotations.VirtualHierarchyAnnotationLookup;
import org.dockbox.hartshorn.util.introspect.scan.TypeReferenceCollectorContext;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.SequencedSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Common implementation of {@link ApplicationEnvironment}, supporting the full range of standard
 * functionalities and configurations. This implementation is typically used as a base for standard
 * applications, and can be customized through the use of the {@link Configurer}.
 *
 * <p>This implementation primarily delegates to individual components that are configured before
 * the environment is
 * initialized. This allows for a high degree of customization and flexibility, while still
 * providing a consistent environment for the application to run in.
 *
 * <p>Typically, this implementation will automatically be selected when creating applications
 * through the standard
 * {@link StandardApplicationContextFactory}, which is also the default for
 * {@link HartshornApplication} and {@link HartshornApplicationConfigurer}.
 *
 * @see ConfigurableApplicationEnvironment.Configurer
 * @see StandardApplicationContextFactory.Configurer#environment(ApplicationEnvironment)
 * @see HartshornApplication
 * @see ObservableApplicationEnvironment
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public final class ConfigurableApplicationEnvironment
    implements ObservableApplicationEnvironment, ModifiableApplicationContextCarrier {

    private final MultiMap<Integer, Observer> observers = new ConcurrentSetTreeMultiMap<>();
    private final Set<Class<? extends Observer>> lazyObservers = ConcurrentHashMap.newKeySet();

    private final FileSystemProvider fileSystemProvider;
    private final ProxyOrchestrator proxyOrchestrator;
    private final ExceptionHandler exceptionHandler;
    private final AnnotationLookup annotationLookup;
    private final ClasspathResourceLocator classPathResourceLocator;
    private final ResourceLookup resourceLookup;
    private final ConditionMatcher conditionMatcher;

    private final ComponentInjectionPointsResolver injectionPointsResolver;
    private final ComponentKeyResolver componentKeyResolver;
    private final EnvironmentTypeResolver typeResolver;
    private final PropertyRegistry propertyRegistry;
    private final ComponentRegistry componentRegistry;
    private final InjectorConfiguration injectorConfiguration;
    private final boolean isBuildEnvironment;

    private ApplicationContext applicationContext;
    private Introspector introspector;

    private ConfigurableApplicationEnvironment(
        SingleElementContext<? extends ApplicationBootstrapContext> context,
        Configurer configurer
    ) {
        SingleElementContext<ApplicationEnvironment> environmentInitializerContext =
            context.transform(this);
        ApplicationBootstrapContext bootstrapContext = context.input();
        environmentInitializerContext.addContext(bootstrapContext);

        this.conditionMatcher = this.configure(
            environmentInitializerContext,
            configurer.conditionMatcher
        );
        this.exceptionHandler = this.configure(
            environmentInitializerContext,
            configurer.exceptionHandler
        );
        this.annotationLookup = this.configure(
            environmentInitializerContext,
            configurer.annotationLookup
        );
        this.proxyOrchestrator = this.configure(
            environmentInitializerContext.transform(this.introspector()),
            configurer.proxyOrchestrator
        );
        this.fileSystemProvider = this.configure(
            environmentInitializerContext,
            configurer.applicationFSProvider
        );
        this.classPathResourceLocator = this.configure(
            environmentInitializerContext,
            configurer.classpathResourceLocator
        );
        this.injectionPointsResolver = this.configure(
            environmentInitializerContext,
            configurer.injectionPointsResolver
        );
        this.componentKeyResolver = this.configure(
            environmentInitializerContext,
            configurer.componentKeyResolver
        );
        this.typeResolver = this.configure(
            environmentInitializerContext,
            configurer.typeResolver
        );
        this.componentRegistry = this.configure(
            environmentInitializerContext,
            configurer.componentRegistry
        );

        Class<?> mainClass = bootstrapContext.mainClass();
        TypeView<?> mainType = this.introspector().introspect(mainClass);
        this.componentRegistry()
            .addCustomContainer(new ApplicationMainComponentContainer<>(mainType));

        this.resourceLookup = this.configure(
            environmentInitializerContext,
            configurer.resourceLookup
        );
        this.propertyRegistry = this.initializePropertyRegistry(
            configurer,
            environmentInitializerContext
        );

        SingleElementContext<PropertyRegistry> argumentsInitializerContext = context.transform(
            this.propertyRegistry
        );

        this.injectorConfiguration = configurer.injectorConfiguration
                .initialize(argumentsInitializerContext);
        this.printStackTraces(this.injectorConfiguration.showStacktraces());

        if (this.introspector() instanceof BatchCapableIntrospector batchCapableIntrospector) {
            batchCapableIntrospector.enableBatchMode(this.configuration().isBatchMode());
        }

        Boolean isBuildEnvironment = configurer.isBuildEnvironment
            .initialize(environmentInitializerContext);

        if (isBuildEnvironment == null) {
            isBuildEnvironment = false;
        }
        this.isBuildEnvironment = isBuildEnvironment;

        if (!this.isBuildEnvironment
            && this.injectorConfiguration.bannerEnabled()
        ) {
            this.printBanner(mainClass);
        }

        ApplicationContext initializedContext =
            configurer.applicationContext.initialize(environmentInitializerContext);
        // This will handle two aspects:
        // 1. If the context was not attached through the implementation of
        //    ModifiableContextCarrier, it will be attached here.
        // 2. If the context was attached through the implementation of ModifiableContextCarrier,
        //    it will verify that the resulting context is the same as the attached context, or
        //    throw an exception to prevent leaving the application in an inconsistent state.
        if (initializedContext != null) {
            this.applicationContext(initializedContext);
        }
    }

    private PropertyRegistry initializePropertyRegistry(
        Configurer configurer,
        SingleElementContext<ApplicationEnvironment> environmentInitializerContext
    ) {
        PropertyRegistryFactory factory =
            configurer.propertyRegistryFactory.initialize(environmentInitializerContext);
        return factory.createRegistry();
    }

    private <I, T> T configure(
        SingleElementContext<I> context,
        ContextualInitializer<I, T> initializer
    ) {
        T instance = initializer.initialize(context);
        return this.configure(instance);
    }

    @Override
    public ComponentKeyResolver componentKeyResolver() {
        return this.componentKeyResolver;
    }

    @Override
    public ComponentInjectionPointsResolver injectionPointsResolver() {
        return this.injectionPointsResolver;
    }

    private <T> T configure(T instance) {
        if (instance instanceof ApplicationManaged managed) {
            managed.environment(this);
        }
        return instance;
    }

    @Override
    public ExceptionHandler exceptionHandler() {
        return this.exceptionHandler;
    }

    /**
     * Returns the annotation lookup used by this environment. The annotation lookup is typically
     * used to discover annotations on types and members.
     *
     * @return the annotation lookup
     */
    public AnnotationLookup annotationLookup() {
        return this.annotationLookup;
    }

    @Override
    public FileSystemProvider fileSystem() {
        return this.fileSystemProvider;
    }

    @Override
    public ClasspathResourceLocator classpath() {
        return this.classPathResourceLocator;
    }

    @Override
    public ProxyOrchestrator proxyOrchestrator() {
        return this.proxyOrchestrator;
    }

    @Override
    public InjectorConfiguration configuration() {
        return this.injectorConfiguration;
    }

    @Override
    public PropertyRegistry propertyRegistry() {
        return this.propertyRegistry;
    }

    @Override
    public Introspector introspector() {
        if (this.introspector == null) {
            // Lazy, as the proxy orchestrator may not yet be initialized
            ProxyLookup proxyLookup = new SupplierAdapterProxyLookup(() -> this.proxyOrchestrator);
            try {
                this.introspector = DiscoveryService.instance()
                    .discover(IntrospectorLoader.class)
                    .create(proxyLookup, this.annotationLookup());
            }
            catch (ServiceDiscoveryException e) {
                throw new ApplicationRuntimeException(e);
            }
        }
        return this.introspector;
    }

    @Override
    public EnvironmentTypeResolver typeResolver() {
        return this.typeResolver;
    }

    @Override
    public boolean isBuildEnvironment() {
        return this.isBuildEnvironment;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public ComponentRegistry componentRegistry() {
        return this.componentRegistry;
    }

    @Override
    public ConditionMatcher conditionMatcher() {
        return this.conditionMatcher;
    }

    @Override
    public ResourceLookup resourceLookup() {
        return this.resourceLookup;
    }

    @Override
    public void handle(Throwable throwable) {
        this.exceptionHandler.handle(throwable);
    }

    @Override
    public void handle(String message, Throwable throwable) {
        this.exceptionHandler.handle(message, throwable);
    }

    @Override
    public ExceptionHandler printStackTraces(boolean stacktraces) {
        return this.exceptionHandler.printStackTraces(stacktraces);
    }

    @Override
    public void register(Observer observer) {
        this.observers.put(observer.priority(), observer);
    }

    @Override
    public void register(Class<? extends Observer> observer) {
        this.lazyObservers.add(observer);
    }

    @Override
    public <T extends Observer> SequencedSet<T> observers(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        this.initializeLazyObservers(type);

        Set<T> typedObservers = new HashSet<>();
        this.observers.allValues().stream()
            .filter(type::isInstance)
            .map(type::cast)
            .forEach(typedObservers::add);

        // In case of observers provided by bindings, we cannot safely cache them inside the
        // environment (primarily due to prototype components), so we will look them up from the
        // application context.
        ComponentKey<ComponentCollection<T>> lookupKey = ComponentKey.collect(type)
            .mutable()
            .fuzzy()
            .build();
        typedObservers.addAll(this.applicationContext.get(lookupKey));

        return typedObservers.stream()
            .sorted(Comparator.comparingInt(Observer::priority))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void initializeLazyObservers(Class<?> type) {
        Set<Class<? extends Observer>> initialized = new HashSet<>();
        for (Class<? extends Observer> observerClass : this.lazyObservers) {
            if (!initialized.contains(observerClass) && type.isAssignableFrom(observerClass)) {
                Observer observer = this.applicationContext.get(observerClass);
                this.register(observer);
                initialized.add(observerClass);
            }
        }
    }

    private void printBanner(Class<?> mainClass) {
        Logger logger = LoggerFactory.getLogger(mainClass);
        this.createBanner().print(logger);
    }

    private Banner createBanner() {
        try {
            return this.classPathResourceLocator.resource("banner.txt")
                .flatMap(IOUtilities::openBufferedStream)
                .map(resource -> (Banner) new ResourcePathBanner(resource))
                .orElseGet(HartshornLogoBanner::new);
        }
        catch (IOException e) {
            return new HartshornLogoBanner();
        }
    }

    /**
     * Creates a new {@link ContextualInitializer} for the
     * {@link ConfigurableApplicationEnvironment} using the given {@link Customizer}.
     *
     * @param customizer the customizer to use, if left empty the default configuration will be
     * used
     *
     * @return a non-cached {@link ContextualInitializer} for the
     * {@link ConfigurableApplicationEnvironment}
     */
    // checkstyle:off LineLength
    public static ContextualInitializer<ApplicationBootstrapContext, ConfigurableApplicationEnvironment>
    // checkstyle:on LineLength
    create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);
            return new ConfigurableApplicationEnvironment(context, configurer);
        };
    }

    @Override
    public ModifiableApplicationContextCarrier applicationContext(ApplicationContext context) {
        if (this.applicationContext != null && this.applicationContext != context) {
            throw new IllegalStateException("Application context already set");
        }
        this.applicationContext = context;
        return this;
    }

    /**
     * Configurer for the {@link ConfigurableApplicationEnvironment}. Allows for the configuration
     * of individual components which are used by the environment, as well as several global
     * settings that influence the behavior of the environment.
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public static class Configurer {

        // checkstyle:off LineLength
        private ContextualInitializer<ApplicationEnvironment, EnvironmentTypeResolver> typeResolver = context -> {
            TypeReferenceCollectorContext collectorContext =
                context.firstContext(TypeReferenceCollectorContext.class)
                    .orElseGet(TypeReferenceCollectorContext::new);
            return new EnvironmentTypeCollectorTypeResolver(new EnvironmentTypeCollector(context.input(),
                collectorContext));
        };

        private ContextualInitializer<ApplicationEnvironment, ? extends ComponentRegistry> componentRegistry =
                ContextualInitializer.of(TypeReferenceLookupComponentRegistry::new);

        private ContextualInitializer<Introspector, ? extends ProxyOrchestrator> proxyOrchestrator =
            DefaultProxyOrchestratorLoader.create(Customizer.useDefaults());

        private ContextualInitializer<ApplicationEnvironment, ? extends PropertyRegistryFactory> propertyRegistryFactory =
            EnvironmentProfilesPropertyRegistryFactory.create(Customizer.useDefaults());

        private ContextualInitializer<ApplicationEnvironment, ? extends FileSystemProvider> applicationFSProvider =
            ContextualInitializer.of(PathFileSystemProvider::new);

        private ContextualInitializer<ApplicationEnvironment, ? extends ExceptionHandler> exceptionHandler =
            ContextualInitializer.of(LoggingExceptionHandler::new);

        private ContextualInitializer<ApplicationEnvironment, ? extends ClasspathResourceLocator> classpathResourceLocator =
            ContextualInitializer.of(ClassLoaderClasspathResourceLocator::new);

        private ContextualInitializer<ApplicationEnvironment, ? extends AnnotationLookup> annotationLookup =
            ContextualInitializer.of(VirtualHierarchyAnnotationLookup::new);

        private ContextualInitializer<ApplicationEnvironment, ? extends ApplicationContext> applicationContext =
            SimpleApplicationContext.create(Customizer.useDefaults());

        private ContextualInitializer<ApplicationEnvironment, Boolean> isBuildEnvironment =
            ContextualInitializer.of(_ -> BuildEnvironmentPredicate.isBuildEnvironment());

        private ContextualInitializer<ApplicationEnvironment, ComponentInjectionPointsResolver> injectionPointsResolver =
            ContextualInitializer.defer(() -> MethodsAndFieldsInjectionPointResolver.create(Customizer.useDefaults()));

        private ContextualInitializer<ApplicationEnvironment, ComponentKeyResolver> componentKeyResolver =
            ContextualInitializer.of(StandardAnnotationComponentKeyResolver::new);

        private ContextualInitializer<ApplicationEnvironment, ResourceLookup> resourceLookup =
            StrategyResourceLookup.create(Customizer.useDefaults());

        private ContextualInitializer<ApplicationEnvironment, ConditionMatcher> conditionMatcher =
            ContextualInitializer.of(environment -> {
                return new ConditionMatcher(environment::applicationContext);
            });

        private ContextualInitializer<PropertyRegistry, InjectorConfiguration> injectorConfiguration =
                ImmutableInjectorConfiguration.create(Customizer.useDefaults());
        // checkstyle:on LineLength

        /**
         * Configures the {@link ComponentRegistry} that is used by the
         * {@link DelegatingApplicationContext} to locate components.
         *
         * @param componentRegistry the {@link ComponentRegistry} to use
         *
         * @return the current instance
         */
        public Configurer componentRegistry(ComponentRegistry componentRegistry) {
            return this.componentRegistry(ContextualInitializer.of(componentRegistry));
        }

        /**
         * Configures the {@link ComponentRegistry} that is used by the
         * {@link DelegatingApplicationContext} to locate components.
         *
         * @param componentRegistry the {@link ComponentRegistry} to use
         *
         * @return the current instance
         */
        public Configurer componentRegistry(
            ContextualInitializer<ApplicationEnvironment, ? extends ComponentRegistry>
                componentRegistry
        ) {
            this.componentRegistry = componentRegistry;
            return this;
        }

        /**
         * Sets the {@link ProxyOrchestrator} to use. The {@link ProxyOrchestrator} is responsible
         * for creating proxies for application components. The default implementation is provided
         * by {@link DefaultProxyOrchestratorLoader}.
         *
         * @param proxyOrchestrator the {@link ProxyOrchestrator} to use
         *
         * @return the current {@link Configurer} instance
         *
         * @see ProxyOrchestrator
         */
        public Configurer applicationOrchestrator(ProxyOrchestrator proxyOrchestrator) {
            return this.applicationOrchestrator(ContextualInitializer.of(proxyOrchestrator));
        }

        /**
         * Sets the {@link ProxyOrchestrator} to use. The {@link ProxyOrchestrator} is responsible
         * for creating proxies for application components. The default implementation is provided
         * by {@link DefaultProxyOrchestratorLoader}.
         *
         * @param orchestrator the {@link ProxyOrchestrator} to use
         *
         * @return the current {@link Configurer} instance
         *
         * @see ProxyOrchestrator
         */
        public Configurer applicationOrchestrator(
            ContextualInitializer<Introspector, ? extends ProxyOrchestrator> orchestrator
        ) {
            this.proxyOrchestrator = orchestrator;
            return this;
        }

        /**
         * Sets the {@link FileSystemProvider} to use. The {@link FileSystemProvider} is responsible
         * for providing the application's file system. The default implementation is
         * {@link PathFileSystemProvider}.
         *
         * @param fileSystemProvider the {@link FileSystemProvider} to use
         *
         * @return the current {@link Configurer} instance
         *
         * @see FileSystemProvider
         */
        public Configurer applicationFSProvider(FileSystemProvider fileSystemProvider) {
            return this.applicationFSProvider(ContextualInitializer.of(fileSystemProvider));
        }

        /**
         * Sets the {@link FileSystemProvider} to use. The {@link FileSystemProvider} is responsible
         * for providing the application's file system. The default implementation is
         * {@link PathFileSystemProvider}.
         *
         * @param applicationFSProvider the {@link FileSystemProvider} to use
         *
         * @return the current {@link Configurer} instance
         *
         * @see FileSystemProvider
         */
        public Configurer applicationFSProvider(
            ContextualInitializer<ApplicationEnvironment, ? extends FileSystemProvider>
                applicationFSProvider
        ) {
            this.applicationFSProvider = applicationFSProvider;
            return this;
        }

        /**
         * Sets the {@link ExceptionHandler} to use. The {@link ExceptionHandler} is responsible for
         * handling exceptions that occur during the application's lifecycle. The default
         * implementation is {@link LoggingExceptionHandler}.
         *
         * @param exceptionHandler the {@link ExceptionHandler} to use
         *
         * @return the current {@link Configurer} instance
         *
         * @see ExceptionHandler
         */
        public Configurer exceptionHandler(ExceptionHandler exceptionHandler) {
            return this.exceptionHandler(ContextualInitializer.of(exceptionHandler));
        }

        /**
         * Sets the {@link ExceptionHandler} to use. The {@link ExceptionHandler} is responsible for
         * handling exceptions that occur during the application's lifecycle. The default
         * implementation is {@link LoggingExceptionHandler}.
         *
         * @param exceptionHandler the {@link ExceptionHandler} to use
         *
         * @return the current {@link Configurer} instance
         *
         * @see ExceptionHandler
         */
        public Configurer exceptionHandler(
            ContextualInitializer<ApplicationEnvironment, ? extends ExceptionHandler>
                exceptionHandler
        ) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        /**
         * Sets the {@link PropertyRegistryFactory} to use. The {@link PropertyRegistryFactory} is
         * responsible for creating the {@link PropertyRegistry} used by the environment. The
         * default implementation is {@link EnvironmentProfilesPropertyRegistryFactory}.
         *
         * @param propertyRegistryFactory the factory to use
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer propertyRegistryFactory(PropertyRegistryFactory propertyRegistryFactory) {
            return this.propertyRegistryFactory(ContextualInitializer.of(propertyRegistryFactory));
        }

        /**
         * Sets the {@link PropertyRegistryFactory} to use. The {@link PropertyRegistryFactory} is
         * responsible for creating the {@link PropertyRegistry} used by the environment. The
         * default implementation is {@link EnvironmentProfilesPropertyRegistryFactory}.
         *
         * @param propertyRegistryFactory the factory to use
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer propertyRegistryFactory(
            ContextualInitializer<ApplicationEnvironment, ? extends PropertyRegistryFactory>
                propertyRegistryFactory
        ) {
            this.propertyRegistryFactory = propertyRegistryFactory;
            return this;
        }

        /**
         * Sets the {@link ClasspathResourceLocator} to use. The {@link ClasspathResourceLocator} is
         * responsible for locating resources on the classpath. The default implementation is
         * {@link ClassLoaderClasspathResourceLocator}.
         *
         * @param classpathResourceLocator the {@link ClasspathResourceLocator} to use
         *
         * @return the current {@link Configurer} instance
         *
         * @see ClasspathResourceLocator
         */
        public Configurer classpathResourceLocator(
            ClasspathResourceLocator classpathResourceLocator
        ) {
            return this.classpathResourceLocator(
                ContextualInitializer.of(classpathResourceLocator)
            );
        }

        /**
         * Sets the {@link ClasspathResourceLocator} to use. The {@link ClasspathResourceLocator} is
         * responsible for locating resources on the classpath. The default implementation is
         * {@link ClassLoaderClasspathResourceLocator}.
         *
         * @param classpathResourceLocator the {@link ClasspathResourceLocator} to use
         *
         * @return the current {@link Configurer} instance
         *
         * @see ClasspathResourceLocator
         */
        public Configurer classpathResourceLocator(
            ContextualInitializer<ApplicationEnvironment, ? extends ClasspathResourceLocator>
                classpathResourceLocator
        ) {
            this.classpathResourceLocator = classpathResourceLocator;
            return this;
        }

        /**
         * Sets the {@link AnnotationLookup} to use. The {@link AnnotationLookup} is responsible for
         * looking up annotations on elements. The default implementation is
         * {@link VirtualHierarchyAnnotationLookup}.
         *
         * @param annotationLookup the {@link AnnotationLookup} to use
         *
         * @return the current {@link Configurer} instance
         *
         * @see AnnotationLookup
         */
        public Configurer annotationLookup(AnnotationLookup annotationLookup) {
            return this.annotationLookup(ContextualInitializer.of(annotationLookup));
        }

        /**
         * Sets the {@link AnnotationLookup} to use. The {@link AnnotationLookup} is responsible for
         * looking up annotations on elements. The default implementation is
         * {@link VirtualHierarchyAnnotationLookup}.
         *
         * @param annotationLookup the {@link AnnotationLookup} to use
         *
         * @return the current {@link Configurer} instance
         *
         * @see AnnotationLookup
         */
        public Configurer annotationLookup(
            ContextualInitializer<ApplicationEnvironment, ? extends AnnotationLookup>
                annotationLookup
        ) {
            this.annotationLookup = annotationLookup;
            return this;
        }

        /**
         * Sets the {@link ApplicationContext} to use. The {@link ApplicationContext} is responsible
         * for providing access to components and global application state. The default
         * implementation is {@link SimpleApplicationContext}.
         *
         * @param applicationContext the {@link ApplicationContext} to use
         *
         * @return the current {@link Configurer} instance
         *
         * @see ApplicationContext
         */
        public Configurer applicationContext(ApplicationContext applicationContext) {
            return this.applicationContext(ContextualInitializer.of(applicationContext));
        }

        /**
         * Sets the {@link ApplicationContext} to use. The {@link ApplicationContext} is responsible
         * for providing access to components and global application state. The default
         * implementation is {@link SimpleApplicationContext}.
         *
         * @param applicationContext the {@link ApplicationContext} to use
         *
         * @return the current {@link Configurer} instance
         *
         * @see ApplicationContext
         */
        public Configurer applicationContext(
            ContextualInitializer<ApplicationEnvironment, ? extends ApplicationContext>
                applicationContext
        ) {
            this.applicationContext = applicationContext;
            return this;
        }

        /**
         * Sets whether the application is running in a build environment. This is typically used to
         * disable certain features that are not required in a build environment. By default this
         * will follow the result of
         *
         * @param isBuildEnvironment whether the application is running in a build environment
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer isBuildEnvironment(
            ContextualInitializer<ApplicationEnvironment, Boolean> isBuildEnvironment
        ) {
            this.isBuildEnvironment = isBuildEnvironment;
            return this;
        }

        /**
         * Sets whether the application is running in a build environment. This is typically used to
         * disable certain features that are not required in a build environment. This is disabled
         * by default.
         *
         * @param isBuildEnvironment whether the application is running in a build environment
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer isBuildEnvironment(boolean isBuildEnvironment) {
            return this.isBuildEnvironment(ContextualInitializer.of(isBuildEnvironment));
        }

        /**
         * Sets the {@link ComponentInjectionPointsResolver} to use. The
         * {@link ComponentInjectionPointsResolver} is responsible for resolving injection points on
         * components.
         *
         * @param injectionPointsResolver the {@link ComponentInjectionPointsResolver} to use
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer injectionPointsResolver(
            ComponentInjectionPointsResolver injectionPointsResolver
        ) {
            return this.injectionPointsResolver(
                ContextualInitializer.of(() -> injectionPointsResolver)
            );
        }

        /**
         * Sets the {@link ComponentInjectionPointsResolver} to use. The
         * {@link ComponentInjectionPointsResolver} is responsible for resolving injection points on
         * components.
         *
         * @param injectionPointsResolver the {@link ComponentInjectionPointsResolver} to use
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer injectionPointsResolver(
            ContextualInitializer<ApplicationEnvironment, ComponentInjectionPointsResolver>
                injectionPointsResolver
        ) {
            this.injectionPointsResolver = injectionPointsResolver;
            return this;
        }

        /**
         * Sets the {@link ComponentKeyResolver} to use. The {@link ComponentKeyResolver} is
         * responsible for resolving component keys from types and declarations.
         *
         * @param componentKeyResolver the {@link ComponentKeyResolver} to use
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer componentKeyResolver(ComponentKeyResolver componentKeyResolver) {
            return this.componentKeyResolver(ContextualInitializer.of(componentKeyResolver));
        }

        /**
         * Sets the {@link ComponentKeyResolver} to use. The {@link ComponentKeyResolver} is
         * responsible for resolving component keys from types and declarations.
         *
         * @param componentKeyResolver the {@link ComponentKeyResolver} to use
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer componentKeyResolver(
            Initializer<ComponentKeyResolver> componentKeyResolver
        ) {
            return this.componentKeyResolver(ContextualInitializer.of(componentKeyResolver));
        }

        /**
         * Sets the {@link ComponentKeyResolver} to use. The {@link ComponentKeyResolver} is
         * responsible for resolving component keys from types and declarations.
         *
         * @param componentKeyResolver the {@link ComponentKeyResolver} to use
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer componentKeyResolver(
            ContextualInitializer<ApplicationEnvironment, ComponentKeyResolver> componentKeyResolver
        ) {
            this.componentKeyResolver = componentKeyResolver;
            return this;
        }

        /**
         * Sets the {@link EnvironmentTypeResolver} to use. The {@link EnvironmentTypeResolver} is
         * responsible for resolving annotated types within the application environment.
         *
         * @param typeResolver the {@link EnvironmentTypeResolver} to use
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer typeResolver(EnvironmentTypeResolver typeResolver) {
            return this.typeResolver(ContextualInitializer.of(typeResolver));
        }

        /**
         * Sets the {@link EnvironmentTypeResolver} to use. The {@link EnvironmentTypeResolver} is
         * responsible for resolving annotated types within the application environment.
         *
         * @param typeResolver the {@link EnvironmentTypeResolver} to use
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer typeResolver(
            ContextualInitializer<ApplicationEnvironment, EnvironmentTypeResolver> typeResolver
        ) {
            this.typeResolver = typeResolver;
            return this;
        }

        /**
         * Sets the {@link ResourceLookup} to use. The {@link ResourceLookup} is responsible for
         * locating resources within the application environment.
         *
         * @param resourceLookup the {@link ResourceLookup} to use
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer resourceLookup(ResourceLookup resourceLookup) {
            return this.resourceLookup(ContextualInitializer.of(resourceLookup));
        }

        /**
         * Sets the {@link ResourceLookup} to use. The {@link ResourceLookup} is responsible for
         * locating resources within the application environment.
         *
         * @param resourceLookup the {@link ResourceLookup} to use
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer resourceLookup(
            ContextualInitializer<ApplicationEnvironment, ResourceLookup> resourceLookup
        ) {
            this.resourceLookup = resourceLookup;
            return this;
        }

        /**
         * Sets the {@link ConditionMatcher} to use. The {@link ConditionMatcher} is responsible for
         * evaluating conditions within the application environment (e.g. on conditional bindings
         * and components).
         *
         * @param conditionMatcher the {@link ConditionMatcher} to use
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer conditionMatcher(ConditionMatcher conditionMatcher) {
            return this.conditionMatcher(ContextualInitializer.of(conditionMatcher));
        }

        /**
         * Sets the {@link ConditionMatcher} to use. The {@link ConditionMatcher} is responsible for
         * evaluating conditions within the application environment (e.g. on conditional bindings
         * and components).
         *
         * @param conditionMatcher the {@link ConditionMatcher} to use
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer conditionMatcher(
            ContextualInitializer<ApplicationEnvironment, ConditionMatcher> conditionMatcher
        ) {
            this.conditionMatcher = conditionMatcher;
            return this;
        }

        /**
         * Sets the {@link InjectorConfiguration} to use. The
         * {@link InjectorConfiguration} is responsible for configuring the behavior of the
         * component injector within the application environment.
         *
         * @param injectorConfiguration the {@link InjectorConfiguration} to use
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer injectorConfiguration(InjectorConfiguration injectorConfiguration) {
            return this.injectorConfiguration(ContextualInitializer.of(injectorConfiguration));
        }

        /**
         * Sets the {@link InjectorConfiguration} to use. The
         * {@link InjectorConfiguration} is responsible for configuring the behavior of the
         * component injector within the application environment.
         *
         * @param injectorConfiguration the {@link InjectorConfiguration} to use
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer injectorConfiguration(
            ContextualInitializer<PropertyRegistry, InjectorConfiguration> injectorConfiguration
        ) {
            this.injectorConfiguration = injectorConfiguration;
            return this;
        }
    }
}
