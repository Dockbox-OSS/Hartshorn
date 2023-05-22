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
import org.dockbox.hartshorn.application.environment.ApplicationArgumentParser;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ApplicationFSProvider;
import org.dockbox.hartshorn.application.environment.ApplicationFSProviderImpl;
import org.dockbox.hartshorn.application.environment.ClassLoaderClasspathResourceLocator;
import org.dockbox.hartshorn.application.environment.ClasspathResourceLocator;
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.StandardApplicationArgumentParser;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.component.ComponentPostConstructor;
import org.dockbox.hartshorn.component.ComponentPostConstructorImpl;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.component.ContextualComponentPopulator;
import org.dockbox.hartshorn.component.ScopeAwareComponentProvider;
import org.dockbox.hartshorn.component.TypeReferenceLookupComponentLocator;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.discovery.DiscoveryService;
import org.dockbox.hartshorn.introspect.IntrospectionViewContextAdapter;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.logging.ApplicationLogger;
import org.dockbox.hartshorn.logging.logback.LogbackApplicationLogger;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.proxy.ApplicationProxierLoader;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyWriter;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;
import org.dockbox.hartshorn.util.introspect.annotations.VirtualHierarchyAnnotationLookup;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DefaultApplicationBuilder<Self extends DefaultApplicationBuilder<Self, C>, C extends ApplicationContext> implements ApplicationBuilder<Self, C> {

    private static final class ComponentInitializer<T> {

        private final Initializer<T> defaultInitializer;
        private Initializer<T> initializer;
        private boolean initialized;

        private ComponentInitializer(final Initializer<T> defaultInitializer) {
            this.defaultInitializer = defaultInitializer.cached();
        }

        public boolean initialized() {
            return this.initialized;
        }

        public T initialize(final InitializingContext context) {
            this.initialized = true;
            return this.initializer().initialize(context);
        }

        public Initializer<T> initializer() {
            return this.initializer == null ? this.defaultInitializer : this.initializer;
        }

        public void initializer(final Initializer<T> initializer) {
            this.initializer = initializer.cached();
        }

        public static <T> ComponentInitializer<T> of(final Initializer<T> defaultInitializer) {
            return new ComponentInitializer<>(defaultInitializer);
        }
    }

    private Class<?> mainClass;
    private boolean includeBasePackages = true;
    private boolean enableBanner = true;
    private boolean enableBatchMode = false;

    private final Set<Annotation> serviceActivators = ConcurrentHashMap.newKeySet();
    private final Set<String> arguments = ConcurrentHashMap.newKeySet();
    private final Set<String> prefixes = ConcurrentHashMap.newKeySet();
    private final Set<ComponentPostProcessor> componentPostProcessors = ConcurrentHashMap.newKeySet();
    private final Set<ComponentPreProcessor> componentPreProcessors = ConcurrentHashMap.newKeySet();
    private final Set<Class<?>> standaloneComponents = ConcurrentHashMap.newKeySet();

    private final ComponentInitializer<ApplicationConfigurator> applicationConfigurator = ComponentInitializer.of(ctx -> new EnvironmentDrivenApplicationConfigurator());
    private final ComponentInitializer<ApplicationProxier> applicationProxier = ComponentInitializer.of(ctx -> {
        ApplicationProxierLoader loader = DiscoveryService.instance().discover(ApplicationProxierLoader.class);
        return loader.create(ctx.environment());
    });
    private final ComponentInitializer<ApplicationFSProvider> applicationFSProvider = ComponentInitializer.of(ctx -> new ApplicationFSProviderImpl());
    private final ComponentInitializer<ExceptionHandler> exceptionHandler = ComponentInitializer.of(ctx -> new LoggingExceptionHandler());
    private final ComponentInitializer<ApplicationArgumentParser> argumentParser = ComponentInitializer.of(ctx -> new StandardApplicationArgumentParser());
    private final ComponentInitializer<ApplicationLogger> applicationLogger = ComponentInitializer.of(ctx -> new LogbackApplicationLogger());
    private final ComponentInitializer<ApplicationEnvironment> applicationEnvironment = ComponentInitializer.of(ContextualApplicationEnvironment::new);
    private final ComponentInitializer<ComponentLocator> componentLocator = ComponentInitializer.of(ctx -> new TypeReferenceLookupComponentLocator(ctx.applicationContext()));
    private final ComponentInitializer<ComponentPostConstructor> componentPostConstructor = ComponentInitializer.of(ComponentPostConstructorImpl::new);
    private final ComponentInitializer<ClasspathResourceLocator> resourceLocator = ComponentInitializer.of(ctx -> new ClassLoaderClasspathResourceLocator(ctx.environment()));
    private final ComponentInitializer<ComponentProvider> componentProvider = ComponentInitializer.of(ScopeAwareComponentProvider::new);
    private final ComponentInitializer<ComponentPopulator> componentPopulator = ComponentInitializer.of(ctx -> new ContextualComponentPopulator(ctx.applicationContext()));
    private final ComponentInitializer<ViewContextAdapter> viewContextAdapter = ComponentInitializer.of(ctx -> new IntrospectionViewContextAdapter(ctx.applicationContext()));
    private final ComponentInitializer<ConditionMatcher> conditionMatcher = ComponentInitializer.of(ctx -> new ConditionMatcher(ctx.applicationContext()));
    private final ComponentInitializer<AnnotationLookup> annotationLookup = ComponentInitializer.of(ctx -> new VirtualHierarchyAnnotationLookup());

    @Override
    public Self mainClass(final Class<?> mainClass) {
        if (Modifier.isAbstract(mainClass.getModifiers()))
            throw new InvalidActivationSourceException("Bootstrap type cannot be abstract, got " + mainClass.getCanonicalName());

        this.mainClass = mainClass;
        return this.self();
    }

    @Override
    public Class<?> mainClass() {
        return this.mainClass;
    }

    @Override
    public Self argument(final String argument) {
        this.arguments.add(argument);
        return this.self();
    }

    @Override
    public Self arguments(final String... args) {
        this.arguments.addAll(Set.of(args));
        return this.self();
    }

    @Override
    public Set<String> arguments() {
        return this.arguments;
    }

    @Override
    public Self includeBasePackages(final boolean include) {
        this.includeBasePackages = include;
        return this.self();
    }

    @Override
    public boolean includeBasePackages() {
        return this.includeBasePackages;
    }

    @Override
    public Self enableBanner(final boolean enable) {
        this.enableBanner = enable;
        return this.self();
    }

    @Override
    public boolean enableBanner() {
        return this.enableBanner;
    }

    @Override
    public Self enableBatchMode(final boolean enable) {
        this.enableBatchMode = enable;
        return this.self();
    }

    @Override
    public boolean enableBatchMode() {
        return this.enableBatchMode;
    }

    @Override
    public Self serviceActivator(final Annotation annotation) {
        this.serviceActivators.add(annotation);
        return this.self();
    }

    @Override
    public Self serviceActivators(final Set<Annotation> annotations) {
        this.serviceActivators.addAll(annotations);
        return this.self();
    }

    @Override
    public Set<Annotation> serviceActivators() {
        return this.serviceActivators;
    }

    @Override
    public Self postProcessor(final ComponentPostProcessor postProcessor) {
        this.componentPostProcessors.add(postProcessor);
        return this.self();
    }

    @Override
    public Set<ComponentPostProcessor> componentPostProcessors() {
        return this.componentPostProcessors;
    }

    @Override
    public Self preProcessor(final ComponentPreProcessor preProcessor) {
        this.componentPreProcessors.add(preProcessor);
        return this.self();
    }

    @Override
    public Set<ComponentPreProcessor> componentPreProcessors() {
        return this.componentPreProcessors;
    }

    @Override
    public Self standaloneComponent(final Class<?> component) {
        this.standaloneComponents.add(component);
        return this.self();
    }

    @Override
    public Set<Class<?>> standaloneComponents() {
        return this.standaloneComponents;
    }

    @Override
    public Self scanPackage(final String packageName) {
        this.prefixes.add(packageName);
        return this.self();
    }

    @Override
    public Self scanPackages(final String... packages) {
        this.prefixes.addAll(Set.of(packages));
        return this.self();
    }

    @Override
    public Self scanPackages(final Set<String> packages) {
        this.prefixes.addAll(packages);
        return this.self();
    }

    @Override
    public Set<String> scanPackages() {
        return this.prefixes;
    }

    @Override
    public Self applicationConfigurator(final Initializer<ApplicationConfigurator> applicationConfigurator) {
        this.applicationConfigurator.initializer(applicationConfigurator);
        return this.self();
    }

    @Override
    public ApplicationConfigurator applicationConfigurator(final InitializingContext context) {
        return this.applicationConfigurator.initialize(context);
    }

    @Override
    public Self applicationProxier(final Initializer<ApplicationProxier> applicationProxier) {
        this.applicationProxier.initializer(applicationProxier);
        return this.self();
    }

    @Override
    public ApplicationProxier applicationProxier(final InitializingContext context) {
        return this.applicationProxier.initialize(context);
    }

    @Override
    public Self applicationLogger(final Initializer<ApplicationLogger> applicationLogger) {
        this.applicationLogger.initializer(applicationLogger);
        return this.self();
    }

    @Override
    public ApplicationLogger applicationLogger(final InitializingContext context) {
        return this.applicationLogger.initialize(context);
    }

    @Override
    public Self applicationFSProvider(final Initializer<ApplicationFSProvider> applicationFSProvider) {
        this.applicationFSProvider.initializer(applicationFSProvider);
        return this.self();
    }

    @Override
    public ApplicationFSProvider applicationFSProvider(final InitializingContext context) {
        return this.applicationFSProvider.initialize(context);
    }

    @Override
    public Self applicationEnvironment(final Initializer<ApplicationEnvironment> applicationEnvironment) {
        this.applicationEnvironment.initializer(applicationEnvironment);
        return this.self();
    }

    @Override
    public ApplicationEnvironment applicationEnvironment(final InitializingContext context) {
        return this.applicationEnvironment.initialize(context);
    }

    @Override
    public Self componentLocator(final Initializer<ComponentLocator> componentLocator) {
        this.componentLocator.initializer(componentLocator);
        return this.self();
    }

    @Override
    public ComponentLocator componentLocator(final InitializingContext context) {
        return this.componentLocator.initialize(context);
    }

    @Override
    public Self componentPostConstructor(final Initializer<ComponentPostConstructor> componentPostConstructor) {
        this.componentPostConstructor.initializer(componentPostConstructor);
        return this.self();
    }

    @Override
    public ComponentPostConstructor componentPostConstructor(final InitializingContext context) {
        return this.componentPostConstructor.initialize(context);
    }

    @Override
    public Self annotationLookup(final Initializer<AnnotationLookup> annotationLookup) {
        this.annotationLookup.initializer(annotationLookup);
        return this.self();
    }

    @Override
    public AnnotationLookup annotationLookup(final InitializingContext context) {
        return this.annotationLookup.initialize(context);
    }

    @Override
    public Self resourceLocator(final Initializer<ClasspathResourceLocator> resourceLocator) {
        this.resourceLocator.initializer(resourceLocator);
        return this.self();
    }

    @Override
    public ClasspathResourceLocator resourceLocator(final InitializingContext context) {
        return this.resourceLocator.initialize(context);
    }

    @Override
    public Self exceptionHandler(final Initializer<ExceptionHandler> exceptionHandler) {
        this.exceptionHandler.initializer(exceptionHandler);
        return this.self();
    }

    @Override
    public ExceptionHandler exceptionHandler(final InitializingContext context) {
        return this.exceptionHandler.initialize(context);
    }

    @Override
    public Self argumentParser(final Initializer<ApplicationArgumentParser> argumentParser) {
        this.argumentParser.initializer(argumentParser);
        return this.self();
    }

    @Override
    public ApplicationArgumentParser argumentParser(final InitializingContext context) {
        return this.argumentParser.initialize(context);
    }

    @Override
    public Self componentProvider(final Initializer<ComponentProvider> componentProvider) {
        this.componentProvider.initializer(componentProvider);
        return this.self();
    }

    @Override
    public ComponentProvider componentProvider(final InitializingContext context) {
        return this.componentProvider.initialize(context);
    }

    @Override
    public Self componentPopulator(final Initializer<ComponentPopulator> componentPopulator) {
        this.componentPopulator.initializer(componentPopulator);
        return this.self();
    }

    @Override
    public ComponentPopulator componentPopulator(final InitializingContext context) {
        return this.componentPopulator.initialize(context);
    }

    @Override
    public Self viewContextAdapter(Initializer<ViewContextAdapter> viewContextAdapter) {
        this.viewContextAdapter.initializer(viewContextAdapter);
        return this.self();
    }

    @Override
    public ViewContextAdapter viewContextAdapter(InitializingContext context) {
        return this.viewContextAdapter.initialize(context);
    }

    @Override
    public Self conditionMatcher(final Initializer<ConditionMatcher> conditionMatcher) {
        this.conditionMatcher.initializer(conditionMatcher);
        return this.self();
    }

    @Override
    public ConditionMatcher conditionMatcher(final InitializingContext context) {
        return this.conditionMatcher.initialize(context);
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {

        collector.property("prefixes").write(this.prefixes.toArray(String[]::new));
        collector.property("standaloneComponents").write(this.standaloneComponents.stream()
                .map(Class::getCanonicalName)
                .toArray(String[]::new));

        collector.property("mainClass").write(this.mainClass.getName());
        collector.property("includeBasePackages").write(this.includeBasePackages);
        collector.property("basePackages").write(this.includeBasePackages
                ? new String[] { this.mainClass.getPackageName() }
                : new String[0]);

        collector.property("enableBanner").write(this.enableBanner);
        collector.property("enableBatchMode").write(this.enableBatchMode);

        this.reportInitializer(this.applicationConfigurator, "applicationConfigurator", collector);
        this.reportInitializer(this.applicationProxier, "applicationProxier", collector);
        this.reportInitializer(this.applicationFSProvider, "applicationFSProvider", collector);
        this.reportInitializer(this.exceptionHandler, "exceptionHandler", collector);
        this.reportInitializer(this.argumentParser, "argumentParser", collector);
        this.reportInitializer(this.applicationLogger, "applicationLogger", collector);
        this.reportInitializer(this.applicationEnvironment, "applicationEnvironment", collector);
        this.reportInitializer(this.componentLocator, "componentLocator", collector);
        this.reportInitializer(this.componentPostConstructor, "componentPostConstructor", collector);
        this.reportInitializer(this.resourceLocator, "resourceLocator", collector);
        this.reportInitializer(this.componentProvider, "componentProvider", collector);
        this.reportInitializer(this.componentPopulator, "componentPopulator", collector);
        this.reportInitializer(this.conditionMatcher, "conditionMatcher", collector);
        this.reportInitializer(this.annotationLookup, "annotationLookup", collector);
    }

    private void reportInitializer(final ComponentInitializer<?> initializer, final String name, final DiagnosticsPropertyCollector collector) {
        final DiagnosticsPropertyWriter property = collector.property(name);
        if (initializer.initialized()) {
            property.write(initializer.initialize(null).getClass().getCanonicalName());
        }
        else {
            property.write("not initialized");
        }
    }

}
