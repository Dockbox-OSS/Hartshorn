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

package org.dockbox.hartshorn.application.context;

import org.dockbox.hartshorn.application.Activator;
import org.dockbox.hartshorn.application.ActivatorHolder;
import org.dockbox.hartshorn.application.ApplicationPropertyHolder;
import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.StartupModifiers;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ApplicationManager;
import org.dockbox.hartshorn.application.environment.ClasspathResourceLocator;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObservable;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.application.lifecycle.ObservableApplicationManager;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.component.ContextualComponentPopulator;
import org.dockbox.hartshorn.component.Enableable;
import org.dockbox.hartshorn.component.HierarchicalApplicationComponentProvider;
import org.dockbox.hartshorn.component.HierarchicalComponentProvider;
import org.dockbox.hartshorn.component.StandardComponentProvider;
import org.dockbox.hartshorn.component.processing.AutomaticActivation;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessor;
import org.dockbox.hartshorn.component.processing.ServiceActivator;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.ContextDrivenProvider;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.inject.MetaProvider;
import org.dockbox.hartshorn.inject.ProviderContext;
import org.dockbox.hartshorn.inject.binding.ApplicationBinder;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.ComponentBinding;
import org.dockbox.hartshorn.inject.binding.InjectConfiguration;
import org.dockbox.hartshorn.logging.ApplicationLogger;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.proxy.ProxyLookup;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.CustomMultiTreeMap;
import org.dockbox.hartshorn.util.Exceptional;
import org.dockbox.hartshorn.util.MultiMap;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Named;

public class StandardDelegatingApplicationContext extends DefaultContext implements
        SelfActivatingApplicationContext,
        HierarchicalComponentProvider {

    public static Comparator<String> PREFIX_PRIORITY_COMPARATOR = Comparator.naturalOrder();
    private static final Pattern ARGUMENTS = Pattern.compile("--([a-zA-Z0-9\\.]+)=(.+)");

    protected final transient MultiMap<Integer, ComponentPreProcessor<?>> preProcessors = new CustomMultiTreeMap<>(ConcurrentHashMap::newKeySet);
    protected final transient Queue<String> prefixQueue = new PriorityQueue<>(PREFIX_PRIORITY_COMPARATOR);

    protected final transient Properties environmentValues;
    private final transient StandardComponentProvider componentProvider;

    private final Set<StartupModifiers> modifiers;
    private final Set<Annotation> activators = ConcurrentHashMap.newKeySet();

    private final ApplicationEnvironment environment;
    private final ClasspathResourceLocator resourceLocator;
    private final ComponentPopulator componentPopulator;
    private final ComponentLocator locator;
    private final MetaProvider metaProvider;
    private final Activator activator;

    public StandardDelegatingApplicationContext(final ApplicationEnvironment environment,
                                                final Function<ApplicationContext, ComponentLocator> componentLocator,
                                                final Function<ApplicationContext, ClasspathResourceLocator> resourceLocator,
                                                final Function<ApplicationContext, MetaProvider> metaProvider,
                                                final Function<ApplicationContext, ComponentProvider> componentProvider,
                                                final Function<ApplicationContext, ComponentPopulator> componentPopulator,
                                                final TypeContext<?> activationSource,
                                                final Set<String> args,
                                                final Set<StartupModifiers> modifiers) {

        this.environmentValues = this.parseProperties(args);

        this.componentProvider = new HierarchicalApplicationComponentProvider(this);
        this.componentPopulator = new ContextualComponentPopulator(this);

        this.componentProvider.bind(ApplicationContext.class).singleton(this);
        this.environment = environment;
        final Exceptional<Activator> activator = activationSource.annotation(Activator.class);
        if (activator.absent()) {
            throw new IllegalStateException("Activation source is not marked with @Activator");
        }
        this.activator = activator.get();
        this.environment().annotationsWith(activationSource, ServiceActivator.class).forEach(this::addActivator);

        this.log().debug("Located %d service activators".formatted(this.activators().size()));

        this.modifiers = modifiers;
        this.locator = componentLocator.apply(this);
        this.resourceLocator = resourceLocator.apply(this);
        this.metaProvider = metaProvider.apply(this);

        this.registerDefaultBindings();
    }

    protected Properties parseProperties(final Set<String> args) {
        final Properties properties = new Properties();
        for (final String arg : args) {
            final Matcher matcher = ARGUMENTS.matcher(arg);
            if (matcher.find()) properties.put(matcher.group(1), matcher.group(2));
        }
        return properties;
    }

    protected void registerDefaultBindings() {
        this.bind(ComponentProvider.class).singleton(this);
        this.bind(ApplicationContext.class).singleton(this);
        this.bind(ActivatorHolder.class).singleton(this);
        this.bind(ApplicationPropertyHolder.class).singleton(this);
        this.bind(ApplicationBinder.class).singleton(this);

        this.bind(ComponentPopulator.class).singleton(this.componentPopulator);
        this.bind(StandardComponentProvider.class).singleton(this.componentProvider);
        this.bind(ComponentProvider.class).singleton(this.componentProvider);

        this.bind(MetaProvider.class).singleton(this.meta());
        this.bind(ComponentLocator.class).singleton(this.locator());
        this.bind(ApplicationEnvironment.class).singleton(this.environment());
        this.bind(ClasspathResourceLocator.class).singleton(this.resourceLocator());

        this.bind(ProxyLookup.class).singleton(this.environment().manager());
        this.bind(ApplicationLogger.class).singleton(this.environment().manager());
        this.bind(ApplicationProxier.class).singleton(this.environment().manager());
        this.bind(ApplicationManager.class).singleton(this.environment().manager());
        this.bind(LifecycleObservable.class).singleton(this.environment().manager());

        this.bind(Logger.class).to(this::log);
    }

    @Override
    public Properties properties() {
        return this.environmentValues;
    }

    @Override
    public Exceptional<String> property(final String key) {
        return Exceptional.of(this.environmentValues.get(key)).map(String::valueOf);
    }

    @Override
    public void addActivator(final Annotation annotation) {
        if (this.activators.contains(annotation)) return;
        final TypeContext<? extends Annotation> annotationType = TypeContext.of(annotation.annotationType());
        final Exceptional<ServiceActivator> activator = annotationType.annotation(ServiceActivator.class);
        if (activator.present()) {
            this.activators.add(annotation);
            for (final String scan : activator.get().scanPackages()) {
                this.bind(scan);
            }
            this.environment().annotationsWith(annotationType, ServiceActivator.class).forEach(this::addActivator);
        }
    }

    @Override
    public void add(final ComponentProcessor<?> processor) {
        final Integer order = processor.order();
        final String name = TypeContext.of(processor).name();

        if (processor instanceof ComponentPostProcessor<?> postProcessor) {
            this.componentProvider.postProcessor(postProcessor);
            this.log().debug("Added " + name + " for component post-processing at phase " + order);
        }
        else if (processor instanceof ComponentPreProcessor<?> preProcessor) {
            this.preProcessors.put(preProcessor.order(), preProcessor);
            this.log().debug("Added " + name + " for component pre-processing at phase " + order);
        }
        else {
            this.log().warn("Unsupported component processor type [" + name + "]");
        }
    }

    @Override
    public Set<Annotation> activators() {
        return Set.copyOf(this.activators);
    }

    @Override
    public <A> A activator(final Class<A> activator) {
        return (A) this.activators.stream().filter(a -> a.annotationType().equals(activator)).findFirst().orElse(null);
    }

    @Override
    public void processPrefixQueue() {
        String next;
        while ((next = this.prefixQueue.poll()) != null) {
            this.processPrefix(next);
        }
    }

    protected void processPrefix(final String prefix) {
        this.locator().register(prefix);

        final Collection<TypeContext<?>> binders = this.environment().types(prefix, ComponentBinding.class, false);

        for (final TypeContext<?> binder : binders) {
            final ComponentBinding bindAnnotation = binder.annotation(ComponentBinding.class).get();
            this.handleBinder(binder, bindAnnotation);
        }
    }

    @Override
    public void process() {
        this.processPrefixQueue();
        final Collection<ComponentContainer> containers = this.locator().containers();
        this.log().debug("Located %d components from classpath".formatted(containers.size()));
        this.process(containers);
    }

    protected void process(final Collection<ComponentContainer> containers) {
        for (final ComponentPreProcessor<?> serviceProcessor : this.preProcessors.allValues()) {
            for (final ComponentContainer container : containers) {
                final TypeContext<?> service = container.type();
                final Key<?> key = Key.of(service);
                if (serviceProcessor.modifies(this, key)) {
                    this.log().debug("Processing component %s with registered processor %s".formatted(container.id(), TypeContext.of(serviceProcessor).name()));
                    serviceProcessor.process(this, key);
                }
            }
        }
    }

    @Override
    public ComponentLocator locator() {
        return this.locator;
    }

    @Override
    public MetaProvider meta() {
        return this.metaProvider;
    }

    @Override
    public boolean hasActivator(final Class<? extends Annotation> activator) {
        final Exceptional<ServiceActivator> annotation = TypeContext.of(activator).annotation(ServiceActivator.class);
        if (annotation.absent())
            throw new IllegalArgumentException("Requested activator " + activator.getSimpleName() + " is not decorated with @ServiceActivator");

        if (this.modifiers.contains(StartupModifiers.ACTIVATE_ALL)) return true;
        else {
            return this.activators.stream()
                    .map(Annotation::annotationType)
                    .toList()
                    .contains(activator);
        }
    }

    @Override
    public void bind(final InjectConfiguration configuration) {
        this.log().debug("Activating configuration binder " + TypeContext.of(configuration).name());
        configuration.binder(this).collect(this);
    }

    @Override
    public void bind(final String prefix) {
        for (final String scannedPrefix : this.environment().prefixContext().prefixes()) {
            if (prefix.startsWith(scannedPrefix)) return;
            if (scannedPrefix.startsWith(prefix)) {
                // If a previously scanned prefix is a prefix of the current prefix, it is more specific and should be ignored,
                // as this prefix will include the specific prefix.
                this.environment().prefixContext().prefixes().remove(scannedPrefix);
            }
        }
        this.environment().prefix(prefix);
        this.prefixQueue.add(prefix);
    }

    @Override
    public <T> void add(final ProviderContext<T> context) {
        final Key<T> key = context.key();
        final BindingHierarchy<T> hierarchy = this.hierarchy(key);
        final BindingFunction<T> function = this.bind(key);

        if (context.singleton()) {
            if (context.lazy()) {
                function.lazySingleton(context.provider());
            }
            else {
                function.singleton(context.provider().get());
            }
        }
        else {
            function.to(context.provider());
        }
    }

    @Override
    public <T> T invoke(final MethodContext<T, ?> method) {
        return this.invoke((MethodContext<? extends T, Object>) method, this.get(method.parent()));
    }

    @Override
    public <T, P> T invoke(final MethodContext<T, P> method, final P instance) {
        final List<TypeContext<?>> parameters = method.parameterTypes();

        final Object[] invokingParameters = new Object[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            final TypeContext<?> parameter = parameters.get(i);
            final Exceptional<Named> annotation = parameter.annotation(Named.class);
            if (annotation.present()) {
                invokingParameters[i] = this.get(parameter, annotation.get());
            }
            else {
                invokingParameters[i] = this.get(parameter);
            }
        }
        try {
            return (T) method.invoke(instance, invokingParameters);
        }
        catch (final Throwable e) {
            return null;
        }
    }

    @Override
    public void enable(final Object instance) throws ApplicationException {
        if (instance instanceof Enableable enableable && enableable.canEnable()) {
            enableable.enable();
        }
    }

    protected <T> void handleBinder(final TypeContext<T> implementer, final ComponentBinding annotation) {
        final TypeContext<T> target = TypeContext.of((Class<T>) annotation.value());

        if (implementer.boundConstructors().isEmpty()) {
            this.handleScanned(implementer, target, annotation);
        }
        else {
            final BindingHierarchy<T> hierarchy = this.hierarchy(Key.of(target));
            hierarchy.add(annotation.priority(), new ContextDrivenProvider<>(implementer));
        }
    }

    protected <C> void handleScanned(final TypeContext<? extends C> binder, final TypeContext<C> binds, final ComponentBinding bindAnnotation) {
        final Named meta = bindAnnotation.named();
        Key<C> key = Key.of(binds);
        if (!"".equals(meta.value())) {
            key = key.name(meta);
        }
        final BindingHierarchy<C> hierarchy = this.hierarchy(key);
        hierarchy.add(bindAnnotation.priority(), new ContextDrivenProvider<>(binder));
    }

    @Override
    public void lookupActivatables() {
        final Collection<TypeContext<? extends ComponentProcessor>> children = this.environment().children(ComponentProcessor.class);
        for (final TypeContext<? extends ComponentProcessor> processor : children) {
            if (processor.isAbstract()) continue;

            if (processor.annotation(AutomaticActivation.class).map(AutomaticActivation::value).or(false)) {
                final ComponentProcessor componentProcessor = this.get(processor);
                if (this.hasActivator(componentProcessor.activator()))
                    this.add(componentProcessor);
            }
        }
    }

    @Override
    public void handle(final Throwable throwable) {
        this.environment().manager().handle(throwable);
    }

    @Override
    public void handle(final String message, final Throwable throwable) {
        this.environment().manager().handle(message, throwable);
    }

    @Override
    public ExceptionHandler stacktraces(final boolean stacktraces) {
        return this.environment().manager().stacktraces(stacktraces);
    }

    @Override
    public void close() {
        this.log().info("Runtime shutting down, notifying observers");
        final ApplicationManager manager = this.environment().manager();
        if (manager instanceof ObservableApplicationManager observable) {
            for (final LifecycleObserver observer : observable.observers()) {
                this.log().debug("Notifying " + observer.getClass().getSimpleName() + " of shutdown");
                try {
                    observer.onExit(this);
                } catch (final Throwable e) {
                    this.log().error("Error notifying " + observer.getClass().getSimpleName() + " of shutdown", e);
                }
            }
        }
    }

    @Override
    public <T> T get(final Key<T> key) {
        return this.componentProvider.get(key);
    }

    @Override
    public <T> T get(final Key<T> key, final boolean enable) {
        return this.componentProvider.get(key, enable);
    }

    @Override
    public <C> BindingFunction<C> bind(final Key<C> key) {
        final BindingFunction<C> function = this.componentProvider.bind(key);
        return new DelegatingApplicationBindingFunction<>(this, function);
    }

    public ClasspathResourceLocator resourceLocator() {
        return this.resourceLocator;
    }

    protected Activator activator() {
        return this.activator;
    }

    @Override
    public ApplicationEnvironment environment() {
        return this.environment;
    }

    public Set<StartupModifiers> modifiers() {
        return this.modifiers;
    }

    @Override
    public <T> BindingHierarchy<T> hierarchy(final Key<T> key) {
        return this.componentProvider.hierarchy(key);
    }

}
