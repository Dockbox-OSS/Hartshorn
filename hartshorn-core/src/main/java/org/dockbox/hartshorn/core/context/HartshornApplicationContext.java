/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.context;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.core.ComponentType;
import org.dockbox.hartshorn.core.CustomMultiMap;
import org.dockbox.hartshorn.core.Enableable;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.InjectConfiguration;
import org.dockbox.hartshorn.core.InjectionPoint;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.MetaProvider;
import org.dockbox.hartshorn.core.Modifiers;
import org.dockbox.hartshorn.core.MultiMap;
import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.annotations.activate.ServiceActivator;
import org.dockbox.hartshorn.core.annotations.inject.ComponentBinding;
import org.dockbox.hartshorn.core.annotations.inject.Context;
import org.dockbox.hartshorn.core.annotations.inject.Enable;
import org.dockbox.hartshorn.core.annotations.inject.Populate;
import org.dockbox.hartshorn.core.annotations.inject.Required;
import org.dockbox.hartshorn.core.binding.BindingHierarchy;
import org.dockbox.hartshorn.core.binding.ContextWrappedHierarchy;
import org.dockbox.hartshorn.core.binding.NativeBindingHierarchy;
import org.dockbox.hartshorn.core.binding.Provider;
import org.dockbox.hartshorn.core.binding.Providers;
import org.dockbox.hartshorn.core.boot.ApplicationLogger;
import org.dockbox.hartshorn.core.boot.ApplicationManager;
import org.dockbox.hartshorn.core.boot.ApplicationProxier;
import org.dockbox.hartshorn.core.boot.ClasspathResourceLocator;
import org.dockbox.hartshorn.core.boot.ExceptionHandler;
import org.dockbox.hartshorn.core.boot.LifecycleObservable;
import org.dockbox.hartshorn.core.boot.SelfActivatingApplicationContext;
import org.dockbox.hartshorn.core.context.element.FieldContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.inject.ProviderContext;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;
import org.dockbox.hartshorn.core.proxy.ProxyLookup;
import org.dockbox.hartshorn.core.services.ComponentContainer;
import org.dockbox.hartshorn.core.services.ComponentLocator;
import org.dockbox.hartshorn.core.services.ComponentPostProcessor;
import org.dockbox.hartshorn.core.services.ComponentPreProcessor;
import org.dockbox.hartshorn.core.services.ComponentProcessor;
import org.dockbox.hartshorn.core.services.ProcessingOrder;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.AccessLevel;
import lombok.Getter;

public class HartshornApplicationContext extends DefaultContext implements SelfActivatingApplicationContext {

    private static final Pattern ARGUMENTS = Pattern.compile("-H([a-zA-Z0-9\\.]+)=(.+)");

    protected final transient Set<InjectionPoint<?>> injectionPoints = HartshornUtils.emptyConcurrentSet();
    protected final transient MultiMap<ProcessingOrder, ComponentPostProcessor<?>> postProcessors = new CustomMultiMap<>(HartshornUtils::emptyConcurrentSet);
    protected final transient MultiMap<ProcessingOrder, ComponentPreProcessor<?>> preProcessors = new CustomMultiMap<>(HartshornUtils::emptyConcurrentSet);
    protected final transient Properties environmentValues = new Properties();
    protected final transient Queue<String> prefixQueue = new ConcurrentLinkedQueue<>();

    @Getter(AccessLevel.PROTECTED) private final Activator activator;
    @Getter private final ApplicationEnvironment environment;

    private final ComponentLocator locator;
    @Getter
    private final ClasspathResourceLocator resourceLocator;
    @Getter
    private final Set<Modifiers> modifiers;
    private final Set<Annotation> activators = HartshornUtils.emptyConcurrentSet();
    private final Map<Key<?>, Object> singletons = new ConcurrentHashMap<>();
    private final Map<Key<?>, BindingHierarchy<?>> hierarchies = new ConcurrentHashMap<>();
    private final MetaProvider metaProvider;

    public HartshornApplicationContext(final ApplicationEnvironment environment, final Function<ApplicationContext, ComponentLocator> componentLocator,
                                       final Function<ApplicationContext, ClasspathResourceLocator> resourceLocator,
                                       final Function<ApplicationContext, MetaProvider> metaProvider, final TypeContext<?> activationSource,
                                       final Set<String> args, final Set<Modifiers> modifiers) {
        this.singletons.put(Key.of(ApplicationContext.class), this);

        this.environment = environment;
        final Exceptional<Activator> activator = activationSource.annotation(Activator.class);
        if (activator.absent()) {
            throw new IllegalStateException("Activation source is not marked with @Activator");
        }
        this.activator = activator.get();
        this.environment().annotationsWith(activationSource, ServiceActivator.class).forEach(this::addActivator);

        this.log().debug("Located %d service activators".formatted(this.activators().size()));

        this.populateArguments(args);

        this.locator = componentLocator.apply(this);
        this.resourceLocator = resourceLocator.apply(this);
        this.metaProvider = metaProvider.apply(this);
        this.modifiers = modifiers;

        this.registerDefaultBindings();
    }

    protected void registerDefaultBindings() {
        this.bind(Key.of(ComponentProvider.class), this);
        this.bind(Key.of(ApplicationContext.class), this);
        this.bind(Key.of(ActivatorSource.class), this);
        this.bind(Key.of(ApplicationPropertyHolder.class), this);
        this.bind(Key.of(ApplicationBinder.class), this);

        this.bind(Key.of(MetaProvider.class), this.metaProvider);
        this.bind(Key.of(ComponentLocator.class), this.locator());
        this.bind(Key.of(ApplicationEnvironment.class), this.environment());

        this.bind(Key.of(ProxyLookup.class), this.environment().manager());
        this.bind(Key.of(ApplicationLogger.class), this.environment().manager());
        this.bind(Key.of(ApplicationProxier.class), this.environment().manager());
        this.bind(Key.of(ApplicationManager.class), this.environment().manager());
        this.bind(Key.of(LifecycleObservable.class), this.environment().manager());
    }

    @Override
    public void addActivator(final Annotation annotation) {
        if (this.activators.contains(annotation)) return;
        final TypeContext<? extends Annotation> annotationType = TypeContext.of(annotation.annotationType());
        final Exceptional<ServiceActivator> activator = annotationType.annotation(ServiceActivator.class);
        if (activator.present()) {
            this.activators.add(annotation);
            for (final String scan :activator.get().scanPackages()){
                this.bind(scan);
            }
            this.environment().annotationsWith(annotationType, ServiceActivator.class).forEach(this::addActivator);
        }
    }

    @Override
    public void add(final InjectionPoint<?> property) {
        if (null != property) this.injectionPoints.add(property);
    }

    public <T> T inject(final Key<T> key, T typeInstance) {
        for (final InjectionPoint<?> injectionPoint : this.injectionPoints) {
            if (injectionPoint.accepts(key.type())) {
                try {
                    typeInstance = ((InjectionPoint<T>) injectionPoint).apply(typeInstance, key.type());
                }
                catch (final ClassCastException e) {
                    this.log().warn("Attempted to apply injection point to incompatible type [" + key.type().qualifiedName() + "]");
                }
            }
        }
        return typeInstance;
    }

    public <T> T raw(final TypeContext<T> type) {
        return Providers.of(type).provide(this).rethrowUnchecked().orNull();
    }

    @Override
    public <T> T raw(final TypeContext<T> type, final boolean populate) {
        try {
            final Exceptional<T> instance = Providers.of(type).provide(this);
            if (instance.present()) {
                final T t = instance.get();
                if (populate) this.populate(t);
                return t;
            }
        }
        catch (final Exception e) {
            ExceptionHandler.unchecked(e);
        }
        return null;
    }

    @Override
    public void add(final ComponentProcessor<?> processor) {
        final ProcessingOrder order = processor.order();
        final String name = TypeContext.of(processor).name();

        if (processor instanceof ComponentPostProcessor<?> postProcessor) {
            this.postProcessors.put(order, postProcessor);
            this.log().debug("Added " + name + " for component post-processing at phase " + order);
        }
        else if (processor instanceof ComponentPreProcessor<?> preProcessor) {
            this.preProcessors.put(order, preProcessor);
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
        String scan;
        while ((scan = this.prefixQueue.poll()) != null) {
            this.locator().register(scan);

            final Collection<TypeContext<?>> binders = this.environment().types(scan, ComponentBinding.class, false);

            for (final TypeContext<?> binder : binders) {
                final ComponentBinding bindAnnotation = binder.annotation(ComponentBinding.class).get();
                this.handleBinder(binder, bindAnnotation);
            }
        }
    }

    @Override
    public void process() {
        this.processPrefixQueue();
        final Collection<ComponentContainer> containers = this.locator().containers(ComponentType.FUNCTIONAL);
        this.log().debug("Located %d functional components from classpath".formatted(containers.size()));
        for (final ProcessingOrder order : ProcessingOrder.VALUES) this.process(order, containers);
    }

    protected void process(final ProcessingOrder order, final Collection<ComponentContainer> containers) {
        for (final ComponentPreProcessor<?> serviceProcessor : this.preProcessors.get(order)) {
            for (final ComponentContainer container : containers) {
                final TypeContext<?> service = container.type();
                final Key<?> key = Key.of(service);
                if (serviceProcessor.modifies(this, key)) {
                    this.log().debug("Processing component %s with registered processor %s in phase %s".formatted(container.id(), TypeContext.of(serviceProcessor).name(), order));
                    serviceProcessor.process(this, key);
                }
            }
        }
    }

    @Override
    public <T> Exceptional<T> property(final String key) {
        return Exceptional.of(() -> (T) this.environmentValues.getOrDefault(key, System.getenv(key)));
    }

    @Override
    public <T> Exceptional<Collection<T>> properties(final String key) {
        // List values are stored as key[0], key[1], ...
        // We use regex to match this pattern, so we can restore the collection
        final String regex = key + "\\[[0-9]+]";
        final List<T> properties = this.environmentValues.entrySet().stream()
                .filter(e -> {
                    final String k = (String) e.getKey();
                    return k.matches(regex);
                })
                // Sort the collection using the key, as these are formatted to contain the index this means we
                // restore the original order of the collection.
                .sorted(Comparator.comparing(e -> (String) e.getKey()))
                .map(Entry::getValue)
                .map(v -> (T) v)
                .collect(Collectors.toList());

        if (properties.isEmpty()) return Exceptional.empty();
        return Exceptional.of(properties);
    }

    @Override
    public boolean hasProperty(final String key) {
        return this.property(key).present();
    }

    @Override
    public <T> void property(final String key, final T value) {
        this.environmentValues.put(key, value);
    }

    @Override
    public void properties(final Map<String, Object> tree) {
        for (final Entry<String, Object> entry : tree.entrySet())
            this.property(entry.getKey(), entry.getValue());
    }

    @Override
    public Properties properties() {
        return this.environmentValues;
    }

    private void populateArguments(final Set<String> args) {
        for (final String arg : args) {
            final Matcher matcher = ARGUMENTS.matcher(arg);
            if (matcher.find()) this.property(matcher.group(1), matcher.group(2));
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

    private <C> void inHierarchy(final Key<C> key, final Consumer<BindingHierarchy<C>> consumer) {
        final BindingHierarchy<C> hierarchy = (BindingHierarchy<C>) this.hierarchies.getOrDefault(key, new NativeBindingHierarchy<>(key, this));
        consumer.accept(hierarchy);
        this.hierarchies.put(key, hierarchy);
    }

    @Override
    public <T> T get(final Key<T> key) {
        return this.get(key, true);
    }

    @Override
    public <T> T get(final Key<T> key, final boolean enable) {
        if (this.singletons.containsKey(key)) return (T) this.singletons.get(key);
        this.locator().validate(key);

        T instance = this.create(key);

        // Modify the instance during phase 1. This allows discarding the existing instance and replacing it with a new instance.
        // See ServiceOrder#PHASE_1
        for (final ProcessingOrder order : ProcessingOrder.PHASE_1) {
            for (final ComponentPostProcessor<?> postProcessor : this.postProcessors.get(order)) {
                if (postProcessor.preconditions(this, key, instance))
                    instance = postProcessor.process(this, key, instance);
            }
        }

        final MetaProvider meta = this.meta();
        // Ensure the order of resolution is to first resolve the instance singleton state, and only after check the type state.
        // Typically, the implementation decided whether it should be a singleton, so this cuts time complexity in half.
        if (instance != null && (meta.singleton(key.type()) || meta.singleton(TypeContext.unproxy(this, instance))))
            this.singletons.put(key, instance);

        // Recreating field instances ensures all fields are created through bootstrapping, allowing injection
        // points to apply correctly
        this.populate(instance);

        // deprecated, will be removed in future versions
        instance = this.inject(key, instance);

        // Modify the instance during phase 2. This does not allow discarding the existing instance.
        // See ServiceOrder#PHASE_2
        for (final ProcessingOrder order : ProcessingOrder.PHASE_2) {
            for (final ComponentPostProcessor<?> postProcessor : this.postProcessors.get(order)) {
                if (postProcessor.preconditions(this, key, instance)) {
                    final T modified = postProcessor.process(this, key, instance);
                    if (modified != instance) {
                        throw new IllegalStateException(("Component %s was modified during phase %s (Phase 2) by %s. " +
                                "Component processors are only able to discard existing instances in phases: %s").formatted(key.type().name(), order.name(), TypeContext.of(postProcessor).name(), Arrays.toString(ProcessingOrder.PHASE_2)));
                    }
                }
            }
        }

        // Inject properties if applicable
        if (enable) {
            try {
                this.enable(instance);
            }
            catch (final ApplicationException e) {
                ExceptionHandler.unchecked(e);
            }
        }

        // May be null, but we have used all possible injectors, it's up to the developer now
        return instance;
    }

    protected <T> T modify(final ProcessingOrder order, final Key<T> key, T instance) {
        for (final ComponentPostProcessor<?> postProcessor : this.postProcessors.get(order)) {
            if (postProcessor.preconditions(this, key, instance))
                instance = postProcessor.process(this, key, instance);
        }
        return instance;
    }

    @Nullable
    public <T> T create(final Key<T> key) {
        final Exceptional<T> provision = this.provide(key).rethrowUnchecked();
        if (provision.present())
            return provision.get();

        final TypeContext<T> type = key.type();

        final Exceptional<T> raw = Exceptional.of(() -> this.raw(type)).rethrowUnchecked();
        if (raw.present())
            return raw.get();

        // If the component is functional and permits proxying, a post processor will be able to proxy the instance
        return null;
    }

    @Override
    public boolean hasActivator(final Class<? extends Annotation> activator) {
        final Exceptional<ServiceActivator> annotation = TypeContext.of(activator).annotation(ServiceActivator.class);
        if (annotation.absent())
            throw new IllegalArgumentException("Requested activator " + activator.getSimpleName() + " is not decorated with @ServiceActivator");

        if (this.modifiers.contains(Modifiers.ACTIVATE_ALL)) return true;
        else {
            return this.activators.stream()
                    .map(Annotation::annotationType)
                    .toList()
                    .contains(activator);
        }
    }

    public <T> Exceptional<T> provide(final Key<T> key) {
        return Exceptional.of(key)
                .map(this::hierarchy)
                .flatMap(hierarchy -> {
                    // Will continue going through each provider until a provider was successful or no other providers remain
                    for (final Provider<T> provider : hierarchy.providers()) {
                        final Exceptional<T> provided = provider.provide(this);
                        if (provided.present()) return provided;
                    }
                    return Exceptional.empty();
                });
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
    public <T> T populate(final T instance) {
        if (null != instance) {
            T modifiableInstance = instance;
            if (this.environment().manager().isProxy(instance)) {
                modifiableInstance = this.environment().manager().handler(instance).flatMap(ProxyHandler::instance).or(modifiableInstance);
            }
            final TypeContext<T> unproxied = TypeContext.unproxy(this, modifiableInstance);
            if (unproxied.annotation(Populate.class).map(Populate::fields).or(true))
                modifiableInstance = this.populateFields(unproxied, modifiableInstance);

            if (unproxied.annotation(Populate.class).map(Populate::executables).or(true))
                modifiableInstance = this.populateMethods(unproxied, modifiableInstance);
        }
        return instance;
    }

    private <T> T populateMethods(final TypeContext<T> type, final T instance) {
        for (final MethodContext<?, T> method : type.methods(Inject.class)) {
            method.invoke(this, instance).rethrowUnchecked();
        }
        return instance;
    }

    private <T> T populateFields(final TypeContext<T> type, final T instance) {
        for (final FieldContext<?> field : type.fields(Inject.class)) {
            Key<?> fieldKey = Key.of(field.type());
            if (field.annotation(Named.class).present()) fieldKey = Key.of(field.type(), field.annotation(Named.class).get());

            final Exceptional<Enable> enableAnnotation = field.annotation(Enable.class);
            final boolean enable = !enableAnnotation.present() || enableAnnotation.get().value();

            final Object fieldInstance = this.get(fieldKey, enable);

            final boolean required = field.annotation(Required.class).map(Required::value).or(false);
            if (required && fieldInstance == null) return ExceptionHandler.unchecked(new ApplicationException("Field " + field.name() + " in " + type.qualifiedName() + " is required"));

            field.set(instance, fieldInstance);
        }
        for (final FieldContext<?> field : type.fields(Context.class)) {
            this.populateContextField(field, instance);
        }
        return instance;
    }

    protected void populateContextField(final FieldContext<?> field, final Object instance) {
        final TypeContext<?> type = field.type();
        final Context annotation = field.annotation(Context.class).get();

        final Exceptional<org.dockbox.hartshorn.core.context.Context> context;
        if ("".equals(annotation.value())) {
            context = this.first((Class<org.dockbox.hartshorn.core.context.Context>) type.type());
        }
        else {
            context = this.first(annotation.value(), (Class<org.dockbox.hartshorn.core.context.Context>) type.type());
        }

        final boolean required = field.annotation(Required.class).map(Required::value).or(false);
        if (required && context.absent()) ExceptionHandler.unchecked(new ApplicationException("Field " + field.name() + " in " + type.qualifiedName() + " is required"));

        field.set(instance, context.orNull());
    }

    @Override
    public <T> void add(final ProviderContext<T> context) {
        final Key<T> key = context.key();
        this.inHierarchy(key, hierarchy -> {
            if (context.singleton()) {
                hierarchy.add(context.priority(), Providers.of(context.provider().get()));
            }
            else {
                hierarchy.add(context.priority(), Providers.of(context.provider()));
            }
        });
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

    @Override
    public <T> BindingHierarchy<T> hierarchy(final Key<T> key) {
        final BindingHierarchy<T> hierarchy = (BindingHierarchy<T>) this.hierarchies.getOrDefault(key, new NativeBindingHierarchy<>(key, this));
        // onUpdate callback is purely so updates will still be saved even if the reference is lost
        if (hierarchy instanceof ContextWrappedHierarchy) return hierarchy;
        else return new ContextWrappedHierarchy<>(hierarchy, this, updated -> this.hierarchies.put(key, updated));
    }

    private <T> void handleBinder(final TypeContext<T> implementer, final ComponentBinding annotation) {
        final TypeContext<T> target = TypeContext.of((Class<T>) annotation.value());

        if (implementer.boundConstructors().isEmpty()) {
            this.handleScanned(implementer, target, annotation);
        }
        else {
            this.inHierarchy(Key.of(target), hierarchy -> hierarchy.add(annotation.priority(), Providers.of(implementer)));
        }
    }

    private <C> void handleScanned(final TypeContext<? extends C> binder, final TypeContext<C> binds, final ComponentBinding bindAnnotation) {
        final Named meta = bindAnnotation.named();
        Key<C> key = Key.of(binds);
        if (!"".equals(meta.value())) {
            key = key.name(meta);
        }
        this.inHierarchy(key, hierarchy -> hierarchy.add(bindAnnotation.priority(), Providers.of(binder)));
    }

    @Override
    public <C> void bind(final Key<C> contract, final Supplier<C> supplier) {
        this.inHierarchy(contract, hierarchy -> hierarchy.add(Providers.of(supplier)));
    }

    @Override
    public <C, T extends C> void bind(final Key<C> contract, final Class<? extends T> implementation) {
        this.inHierarchy(contract, hierarchy -> hierarchy.add(Providers.of(implementation)));
    }

    @Override
    public <C, T extends C> void bind(final Key<C> contract, final T instance) {
        this.inHierarchy(contract, hierarchy -> hierarchy.add(Providers.of(instance)));
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
}
