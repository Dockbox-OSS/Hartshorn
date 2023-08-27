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

package org.dockbox.hartshorn.application.environment;

import org.dockbox.hartshorn.application.ApplicationBootstrapContext;
import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.LoggingExceptionHandler;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.SimpleApplicationContext;
import org.dockbox.hartshorn.application.environment.banner.Banner;
import org.dockbox.hartshorn.application.environment.banner.HartshornBanner;
import org.dockbox.hartshorn.application.environment.banner.ResourcePathBanner;
import org.dockbox.hartshorn.application.lifecycle.ObservableApplicationEnvironment;
import org.dockbox.hartshorn.application.lifecycle.Observer;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.context.ModifiableContextCarrier;
import org.dockbox.hartshorn.discovery.DiscoveryService;
import org.dockbox.hartshorn.logging.ApplicationLogger;
import org.dockbox.hartshorn.logging.AutoSwitchingApplicationLogger;
import org.dockbox.hartshorn.logging.LogExclude;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.proxy.lookup.StateAwareProxyFactory;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.GenericType;
import org.dockbox.hartshorn.util.LazyInitializer;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.IntrospectionEnvironment;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.IntrospectorLoader;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;
import org.dockbox.hartshorn.util.introspect.annotations.DuplicateAnnotationCompositeException;
import org.dockbox.hartshorn.util.introspect.annotations.VirtualHierarchyAnnotationLookup;
import org.dockbox.hartshorn.util.introspect.scan.ClassReferenceLoadException;
import org.dockbox.hartshorn.util.introspect.scan.TypeCollectionException;
import org.dockbox.hartshorn.util.introspect.scan.TypeReferenceCollectorContext;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jakarta.inject.Singleton;

@LogExclude
public class ContextualApplicationEnvironment implements ObservableApplicationEnvironment, ModifiableContextCarrier {

    private final Set<Observer> observers = ConcurrentHashMap.newKeySet();
    private final Set<Class<? extends Observer>> lazyObservers = ConcurrentHashMap.newKeySet();

    private final ApplicationFSProvider applicationFSProvider;
    private final ApplicationLogger applicationLogger;
    private final ApplicationProxier applicationProxier;
    private final ExceptionHandler exceptionHandler;
    private final AnnotationLookup annotationLookup;
    private final ClasspathResourceLocator resourceLocator;

    private final boolean isCI;
    private final boolean isBatchMode;

    private final ApplicationArgumentParser argumentParser;
    private final Properties arguments;

    private ApplicationContext applicationContext;
    private Introspector introspector;

    private ContextualApplicationEnvironment(ApplicationBootstrapContext context, Configurer configurer) {
        this.exceptionHandler = this.configure(configurer.exceptionHandler);
        this.applicationProxier = this.configure(configurer.applicationProxier.initialize(this));
        this.annotationLookup = this.configure(configurer.annotationLookup);
        this.applicationLogger = this.configure(configurer.applicationLogger);
        this.applicationFSProvider = this.configure(configurer.applicationFSProvider);
        this.argumentParser = this.configure(configurer.applicationArgumentParser);
        this.resourceLocator = this.configure(configurer.classpathResourceLocator);

        this.arguments = this.argumentParser.parse(context.arguments());

        this.stacktraces(configurer.showStacktraces.initialize(this.arguments));
        this.isBatchMode = configurer.enableBatchMode.initialize(this.arguments);

        this.isCI = this.checkCI();
        this.checkForDebugging();

        if (!this.isCI && configurer.enableBanner.initialize(this.arguments)) {
            this.printBanner(context.mainClass());
        }

        ApplicationContext initializedContext = configurer.applicationContext.initialize(this);
        // This will handle two aspects:
        // 1. If the context was not initialized through the implementation of ModifiableContextCarrier, it
        //    will be set here to the initialized context.
        // 2. If the context was initialized through the implementation of ModifiableContextCarrier, it will
        //    verify that the context is the same as the initialized context, or throw an exception to prevent
        //    the context from being overwritten and leaving the application in an inconsistent state.
        if (initializedContext != null) {
            this.applicationContext(initializedContext);
        }
    }

    private <T> T configure(LazyInitializer<ApplicationEnvironment, T> initializer) {
        T instance = initializer.initialize(this);
        return this.configure(instance);
    }

    @Override
    public Properties rawArguments() {
        return this.arguments;
    }

    private <T> T configure(T instance) {
        if (instance instanceof ApplicationManaged managed) {
            managed.environment(this);
        }
        return instance;
    }

    protected boolean checkCI() {
        return System.getenv().containsKey("GITLAB_CI")
                || System.getenv().containsKey("JENKINS_HOME")
                || System.getenv().containsKey("TRAVIS")
                || System.getenv().containsKey("GITHUB_ACTIONS")
                || System.getenv().containsKey("APPVEYOR");
    }

    public ApplicationFSProvider applicationFSProvider() {
        return this.applicationFSProvider;
    }

    public ApplicationLogger applicationLogger() {
        return this.applicationLogger;
    }

    public ExceptionHandler exceptionHandler() {
        return this.exceptionHandler;
    }

    public AnnotationLookup annotationLookup() {
        return this.annotationLookup;
    }

    @Override
    public Introspector introspector() {
        if (this.introspector == null) {
            this.introspector = DiscoveryService.instance()
                    .discover(IntrospectorLoader.class)
                    .create(this.applicationProxier, this.annotationLookup());
        }
        return this.introspector;
    }

    @Override
    public boolean isCI() {
        return this.isCI;
    }

    @Override
    public boolean isBatchMode() {
        return this.isBatchMode;
    }

    @Override
    public <A extends Annotation> Collection<TypeView<?>> types(Class<A> annotation) {
        return this.types(type -> type.annotations().has(annotation));
    }

    @Override
    public <T> Collection<TypeView<? extends T>> children(Class<T> parent) {
        return this.types(type -> type.isChildOf(parent) && !type.is(parent));
    }

    private <T> Collection<TypeView<? extends T>> types(Predicate<TypeView<?>> predicate) {
        Option<TypeReferenceCollectorContext> collectorContext = this.applicationContext().first(TypeReferenceCollectorContext.class);
        if (collectorContext.absent()) {
            this.log().warn("TypeReferenceCollectorContext not available, falling back to no-op type lookup");
            return Collections.emptyList();
        }
        try {
            return collectorContext.get().collector().collect().stream()
                    .map(reference -> {
                        try {
                            return reference.getOrLoad();
                        }
                        catch (ClassReferenceLoadException e) {
                            this.handle(e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .map(this::introspect)
                    .filter(predicate)
                    .map(reference -> (TypeView<T>) reference)
                    .collect(Collectors.toSet());
        }
        catch (TypeCollectionException e) {
            this.handle(e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Annotation> annotationsWith(TypeView<?> type, Class<? extends Annotation> annotation) {
        Collection<Annotation> annotations = new ArrayList<>();
        for (Annotation typeAnnotation : type.annotations().all()) {
            if (this.introspect(typeAnnotation.annotationType()).annotations().has(annotation)) {
                annotations.add(typeAnnotation);
            }
        }
        return List.copyOf(annotations);
    }

    @Override
    public List<Annotation> annotationsWith(Class<?> type, Class<? extends Annotation> annotation) {
        return this.annotationsWith(this.introspect(type), annotation);
    }

    @Override
    public boolean singleton(Class<?> type) {
        TypeView<?> typeView = this.introspect(type);
        return this.singleton(typeView);
    }

    @Override
    public boolean singleton(TypeView<?> type) {
        ComponentLocator componentLocator = this.applicationContext().get(ComponentLocator.class);
        return Boolean.TRUE.equals(componentLocator.container(type.type())
                .map(ComponentContainer::singleton)
                .orElseGet(() -> type.annotations().has(Singleton.class)));
    }

    @Override
    public <T> TypeView<T> introspect(Class<T> type) {
        return this.introspector().introspect(type);
    }

    @Override
    public <T> TypeView<T> introspect(T instance) {
        return this.introspector().introspect(instance);
    }

    @Override
    public TypeView<?> introspect(Type type) {
        return this.introspector().introspect(type);
    }

    @Override
    public TypeView<?> introspect(ParameterizedType type) {
        return this.introspector().introspect(type);
    }

    @Override
    public <T> TypeView<T> introspect(GenericType<T> type) {
        return this.introspector().introspect(type);
    }

    @Override
    public TypeView<?> introspect(String type) {
        return this.introspector().introspect(type);
    }

    @Override
    public MethodView<?, ?> introspect(Method method) {
        return this.introspector().introspect(method);
    }

    @Override
    public <T> ConstructorView<T> introspect(Constructor<T> method) {
        return this.introspector().introspect(method);
    }

    @Override
    public FieldView<?, ?> introspect(Field field) {
        return this.introspector().introspect(field);
    }

    @Override
    public ParameterView<?> introspect(Parameter parameter) {
        return this.introspector().introspect(parameter);
    }

    @Override
    public ElementAnnotationsIntrospector introspect(AnnotatedElement annotatedElement) {
        return this.introspector().introspect(annotatedElement);
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public IntrospectionEnvironment environment() {
        return this.introspector().environment();
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
    public ExceptionHandler stacktraces(boolean stacktraces) {
        return this.exceptionHandler.stacktraces(stacktraces);
    }

    @Override
    public Path applicationPath() {
        return this.applicationFSProvider.applicationPath();
    }

    @Override
    public void register(Observer observer) {
        this.observers.add(observer);
    }

    @Override
    public void register(Class<? extends Observer> observer) {
        this.lazyObservers.add(observer);
    }

    @Override
    public Logger log() {
        return this.applicationLogger.log();
    }

    @Override
    public void setDebugActive(boolean active) {
        this.applicationLogger.setDebugActive(active);
    }

    private void checkForDebugging() {
        // TODO: Better property? This does not align with current property definition standard
        boolean debug = Boolean.TRUE.equals(Option.of(this.arguments.get("hartshorn:debug"))
                .cast(String.class)
                .map(Boolean::valueOf)
                .orElse(false));

        this.setDebugActive(debug);
    }

    @Override
    public <T> Option<Class<T>> real(T instance) {
        return this.applicationProxier.real(instance);
    }

    @Override
    public <T> Option<ProxyManager<T>> manager(T instance) {
        return this.applicationProxier.manager(instance);
    }

    @Override
    public <D, T extends D> Option<D> delegate(Class<D> type, T instance) {
        return this.applicationProxier.delegate(type, instance);
    }

    @Override
    public <T> StateAwareProxyFactory<T> factory(Class<T> type) {
        return this.applicationProxier.factory(type);
    }

    @Override
    public <T> Option<Class<T>> unproxy(T instance) {
        return this.applicationProxier.unproxy(instance);
    }

    @Override
    public boolean isProxy(Object instance) {
        return this.applicationProxier.isProxy(instance);
    }

    @Override
    public boolean isProxy(Class<?> candidate) {
        return this.applicationProxier.isProxy(candidate);
    }

    @Override
    public <T extends Observer> Set<T> observers(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }

        Set<T> observers = new HashSet<>();
        this.observers.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .forEach(observers::add);

        this.lazyObservers.stream()
                .filter(type::isAssignableFrom)
                .map(this.applicationContext::get)
                .map(type::cast)
                .forEach(observers::add);

        return observers;
    }

    private void printBanner(Class<?> mainClass) {
        Logger logger = LoggerFactory.getLogger(mainClass);
        this.createBanner().print(logger);
    }

    private Banner createBanner() {
        return this.resourceLocator.resource("banner.txt")
                .option()
                .map(resource -> (Banner) new ResourcePathBanner(resource))
                .orElseGet(HartshornBanner::new);
    }

    @Override
    public <A extends Annotation> A find(AnnotatedElement element, Class<A> annotationType) throws DuplicateAnnotationCompositeException {
        return this.annotationLookup.find(element, annotationType);
    }

    @Override
    public <A extends Annotation> List<A> findAll(AnnotatedElement element, Class<A> annotationType) {
        return this.annotationLookup.findAll(element, annotationType);
    }

    @Override
    public Annotation unproxy(Annotation annotation) {
        return this.annotationLookup.unproxy(annotation);
    }

    @Override
    public LinkedHashSet<Class<? extends Annotation>> annotationHierarchy(Class<? extends Annotation> type) {
        return this.annotationLookup.annotationHierarchy(type);
    }

    public static LazyInitializer<ApplicationBootstrapContext, ContextualApplicationEnvironment> create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);
            return new ContextualApplicationEnvironment(context, configurer);
        };
    }

    @Override
    public ModifiableContextCarrier applicationContext(ApplicationContext context) {
        if (this.applicationContext != null && this.applicationContext != context) {
            throw new IllegalStateException("Application context already set");
        }
        this.applicationContext = context;
        return this;
    }

    public static class Configurer {

        private LazyInitializer<Properties, Boolean> enableBanner = properties -> Boolean.valueOf(properties.getProperty("hartshorn.banner.enabled", "true"));
        private LazyInitializer<Properties, Boolean> enableBatchMode = properties -> Boolean.valueOf(properties.getProperty("hartshorn.batch.enabled", "false"));
        private LazyInitializer<Properties, Boolean> showStacktraces = properties -> Boolean.valueOf(properties.getProperty("hartshorn.exceptions.stacktraces", "true"));

        private LazyInitializer<Introspector, ? extends ApplicationProxier> applicationProxier = DefaultApplicationProxierLoader.create(Customizer.useDefaults());
        private LazyInitializer<ApplicationEnvironment, ? extends ApplicationFSProvider> applicationFSProvider = LazyInitializer.of(ApplicationFSProviderImpl::new);
        private LazyInitializer<ApplicationEnvironment, ? extends ExceptionHandler> exceptionHandler = LazyInitializer.of(LoggingExceptionHandler::new);
        private LazyInitializer<ApplicationEnvironment, ? extends ApplicationArgumentParser> applicationArgumentParser = LazyInitializer.of(StandardApplicationArgumentParser::new);
        private LazyInitializer<ApplicationEnvironment, ? extends ApplicationLogger> applicationLogger = AutoSwitchingApplicationLogger.create(Customizer.useDefaults());
        private LazyInitializer<ApplicationEnvironment, ? extends ClasspathResourceLocator> classpathResourceLocator = ClassLoaderClasspathResourceLocator::new;
        private LazyInitializer<ApplicationEnvironment, ? extends AnnotationLookup> annotationLookup = LazyInitializer.of(VirtualHierarchyAnnotationLookup::new);
        private LazyInitializer<ApplicationEnvironment, ? extends ApplicationContext> applicationContext = SimpleApplicationContext.create(Customizer.useDefaults());

        public Configurer enableBanner(LazyInitializer<Properties, Boolean> enableBanner) {
            this.enableBanner = enableBanner;
            return this;
        }

        public Configurer enableBanner() {
            return this.enableBanner(LazyInitializer.of(true));
        }

        public Configurer disableBanner() {
            return this.enableBanner(LazyInitializer.of(false));
        }

        public Configurer enableBatchMode(LazyInitializer<Properties, Boolean> enableBatchMode) {
            this.enableBatchMode = enableBatchMode;
            return this;
        }

        public Configurer enableBatchMode() {
            return this.enableBatchMode(LazyInitializer.of(true));
        }

        public Configurer disableBatchMode() {
            return this.enableBatchMode(LazyInitializer.of(false));
        }

        public Configurer showStacktraces(LazyInitializer<Properties, Boolean> showStacktraces) {
            this.showStacktraces = showStacktraces;
            return this;
        }

        public Configurer showStacktraces() {
            return this.showStacktraces(LazyInitializer.of(true));
        }

        public Configurer hideStacktraces() {
            return this.showStacktraces(LazyInitializer.of(false));
        }

        public Configurer applicationProxier(ApplicationProxier applicationProxier) {
            return this.applicationProxier(LazyInitializer.of(applicationProxier));
        }
        
        public Configurer applicationProxier(LazyInitializer<Introspector, ? extends ApplicationProxier> applicationProxier) {
            this.applicationProxier = applicationProxier;
            return this;
        }
        
        public Configurer applicationFSProvider(ApplicationFSProvider applicationFSProvider) {
            return this.applicationFSProvider(LazyInitializer.of(applicationFSProvider));
        }
        
        public Configurer applicationFSProvider(LazyInitializer<ApplicationEnvironment, ? extends ApplicationFSProvider> applicationFSProvider) {
            this.applicationFSProvider = applicationFSProvider;
            return this;
        }
        
        public Configurer exceptionHandler(ExceptionHandler exceptionHandler) {
            return this.exceptionHandler(LazyInitializer.of(exceptionHandler));
        }
        
        public Configurer exceptionHandler(LazyInitializer<ApplicationEnvironment, ? extends ExceptionHandler> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }
        
        public Configurer applicationArgumentParser(ApplicationArgumentParser applicationArgumentParser) {
            return this.applicationArgumentParser(LazyInitializer.of(applicationArgumentParser));
        }
        
        public Configurer applicationArgumentParser(LazyInitializer<ApplicationEnvironment, ? extends ApplicationArgumentParser> applicationArgumentParser) {
            this.applicationArgumentParser = applicationArgumentParser;
            return this;
        }
        
        public Configurer applicationLogger(ApplicationLogger applicationLogger) {
            return this.applicationLogger(LazyInitializer.of(applicationLogger));
        }
        
        public Configurer applicationLogger(LazyInitializer<ApplicationEnvironment, ? extends ApplicationLogger> applicationLogger) {
            this.applicationLogger = applicationLogger;
            return this;
        }
        
        public Configurer classpathResourceLocator(ClasspathResourceLocator classpathResourceLocator) {
            return this.classpathResourceLocator(LazyInitializer.of(classpathResourceLocator));
        }
        
        public Configurer classpathResourceLocator(LazyInitializer<ApplicationEnvironment, ? extends ClasspathResourceLocator> classpathResourceLocator) {
            this.classpathResourceLocator = classpathResourceLocator;
            return this;
        }

        public Configurer annotationLookup(AnnotationLookup annotationLookup) {
            return this.annotationLookup(LazyInitializer.of(annotationLookup));
        }

        public Configurer annotationLookup(LazyInitializer<ApplicationEnvironment, ? extends AnnotationLookup> annotationLookup) {
            this.annotationLookup = annotationLookup;
            return this;
        }

        public Configurer applicationContext(ApplicationContext applicationContext) {
            return this.applicationContext(LazyInitializer.of(applicationContext));
        }

        public Configurer applicationContext(LazyInitializer<ApplicationEnvironment, ? extends ApplicationContext> applicationContext) {
            this.applicationContext = applicationContext;
            return this;
        }
    }
}
