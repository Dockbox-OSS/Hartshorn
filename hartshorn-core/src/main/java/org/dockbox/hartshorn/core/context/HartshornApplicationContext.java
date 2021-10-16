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

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.ApplicationContextAware;
import org.dockbox.hartshorn.core.ArrayListMultiMap;
import org.dockbox.hartshorn.core.ComponentType;
import org.dockbox.hartshorn.core.binding.ContextWrappedHierarchy;
import org.dockbox.hartshorn.core.DefaultModifiers;
import org.dockbox.hartshorn.core.InjectConfiguration;
import org.dockbox.hartshorn.core.InjectionPoint;
import org.dockbox.hartshorn.core.InjectorMetaProvider;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.MetaProvider;
import org.dockbox.hartshorn.core.MetaProviderModifier;
import org.dockbox.hartshorn.core.Modifier;
import org.dockbox.hartshorn.core.MultiMap;
import org.dockbox.hartshorn.core.binding.NativeBindingHierarchy;
import org.dockbox.hartshorn.core.exceptions.TypeProvisionException;
import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.annotations.inject.Combines;
import org.dockbox.hartshorn.core.annotations.inject.Context;
import org.dockbox.hartshorn.core.annotations.inject.Enable;
import org.dockbox.hartshorn.core.annotations.service.ServiceActivator;
import org.dockbox.hartshorn.core.binding.BindingHierarchy;
import org.dockbox.hartshorn.core.binding.Bindings;
import org.dockbox.hartshorn.core.binding.Provider;
import org.dockbox.hartshorn.core.binding.Providers;
import org.dockbox.hartshorn.core.context.element.FieldContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.inject.InjectionModifier;
import org.dockbox.hartshorn.core.inject.ProviderContext;
import org.dockbox.hartshorn.core.properties.Attribute;
import org.dockbox.hartshorn.core.properties.AttributeHolder;
import org.dockbox.hartshorn.core.properties.BindingMetaAttribute;
import org.dockbox.hartshorn.core.properties.UseFactory;
import org.dockbox.hartshorn.core.services.ComponentContainer;
import org.dockbox.hartshorn.core.services.ComponentLocator;
import org.dockbox.hartshorn.core.services.ComponentLocatorImpl;
import org.dockbox.hartshorn.core.services.ComponentProcessor;
import org.dockbox.hartshorn.core.services.ServiceImpl;
import org.dockbox.hartshorn.core.services.ServiceOrder;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.AccessLevel;
import lombok.Getter;

@SuppressWarnings("unchecked")
public class HartshornApplicationContext extends DefaultContext implements ApplicationContext {

    private static final Pattern ARGUMENTS = Pattern.compile("-H([a-zA-Z0-9\\.]+)=(.+)");
    protected static final Logger log = LoggerFactory.getLogger(HartshornApplicationContext.class);

    protected final transient Set<InjectionPoint<?>> injectionPoints = HartshornUtils.emptyConcurrentSet();
    protected final transient MultiMap<ServiceOrder, InjectionModifier<?>> injectionModifiers = new ArrayListMultiMap<>();
    protected final transient MultiMap<ServiceOrder, ComponentProcessor<?>> processors = new ArrayListMultiMap<>();
    protected final transient Properties environmentValues = new Properties();

    @Getter(AccessLevel.PROTECTED) private final Activator activator;
    @Getter private final ApplicationEnvironment environment;

    private final ComponentLocator locator;
    private final List<Modifier> modifiers;
    private final List<Annotation> activators = HartshornUtils.emptyList();
    private final Map<Key<?>, Object> singletons = HartshornUtils.emptyConcurrentMap();
    private final Map<Key<?>, BindingHierarchy<?>> hierarchies = HartshornUtils.emptyConcurrentMap();
    private MetaProvider metaProvider;

    public HartshornApplicationContext(final ApplicationContextAware application, final TypeContext<?> activationSource, final Collection<String> prefixes, final String[] args, final Modifier... modifiers) {
        this.environment = new ApplicationEnvironment(prefixes, application);
        final Exceptional<Activator> activator = activationSource.annotation(Activator.class);
        if (activator.absent()) {
            throw new IllegalStateException("Activation source is not marked with @Activator");
        }
        this.activator = activator.get();
        this.environment().annotationsWith(activationSource, ServiceActivator.class).forEach(this::addActivator);
        this.addActivator(new ServiceImpl());

        this.log().debug("Located %d service activators".formatted(this.activators().size()));

        this.populateArguments(args);

        this.locator = new ComponentLocatorImpl(this);
        this.modifiers = HartshornUtils.asUnmodifiableList(modifiers);
        this.modify(this.modifiers);

        this.bind(Key.of(ApplicationContext.class), this);
        this.bind(Key.of(MetaProvider.class), this.metaProvider);
        this.bind(Key.of(ComponentLocator.class), this.locator());
    }

    public void addActivator(final Annotation annotation) {
        if (this.activators.contains(annotation)) return;
        if (TypeContext.of(annotation.annotationType()).annotation(ServiceActivator.class).present()) {
            this.activators.add(annotation);
            this.environment().annotationsWith(TypeContext.unproxy(this, annotation), ServiceActivator.class).forEach(this::addActivator);
        }
    }

    @Override
    public void add(final InjectionPoint<?> property) {
        if (null != property) this.injectionPoints.add(property);
    }

    public <T> T inject(final Key<T> key, T typeInstance, final Attribute<?>... properties) {
        for (final InjectionPoint<?> injectionPoint : this.injectionPoints) {
            if (injectionPoint.accepts(key.contract())) {
                try {
                    //noinspection unchecked
                    typeInstance = ((InjectionPoint<T>) injectionPoint).apply(typeInstance, key.contract(), properties);
                }
                catch (final ClassCastException e) {
                    log.warn("Attempted to apply injection point to incompatible type [" + key.contract().qualifiedName() + "]");
                }
            }
        }
        return typeInstance;
    }

    public <T> void enable(final T typeInstance) {
        if (typeInstance == null) return;
        TypeContext.unproxy(this, typeInstance).fields(Inject.class).stream()
                .filter(field -> field.type().childOf(AttributeHolder.class))
                .filter(field -> {
                    final Exceptional<Enable> enable = field.annotation(Enable.class);
                    return (enable.absent() || enable.get().value());
                })
                .map(field -> field.get(typeInstance))
                .filter(Objects::nonNull)
                .forEach(injectableType -> {
                    try {
                        Bindings.enable(injectableType);
                    }
                    catch (final ApplicationException e) {
                        throw e.runtime();
                    }
                });
    }

    public <T> T raw(final TypeContext<T> type) throws TypeProvisionException {
        return this.raw(type, true);
    }

    @Override
    public <T> T raw(final TypeContext<T> type, final boolean populate) throws TypeProvisionException {
        try {
            final Exceptional<T> instance = Providers.of(type).provide(this);
            if (instance.present()) {
                final T t = instance.get();
                if (populate) this.populate(t);
                return t;
            }
        }
        catch (final Exception e) {
            throw new TypeProvisionException("Could not provide raw instance of " + type.name(), e);
        }
        return null;
    }

    @Override
    public void add(final ComponentProcessor<?> processor) {
        final ServiceOrder order = processor.order();
        this.processors.put(order, processor);
        this.log().debug("Added " + TypeContext.of(processor).name() + " for component processing at phase " + order);
    }

    @Override
    public void add(final InjectionModifier<?> modifier) {
        final ServiceOrder order = modifier.order();
        this.injectionModifiers.put(order, modifier);
        this.log().debug("Added " + TypeContext.of(modifier).name() + " for component modification at phase " + order);
    }

    @Override
    public List<Annotation> activators() {
        return HartshornUtils.asUnmodifiableList(this.activators);
    }

    @Override
    public <A> A activator(final Class<A> activator) {
        //noinspection unchecked
        return (A) this.activators.stream().filter(a -> a.annotationType().equals(activator)).findFirst().orElse(null);
    }

    protected void process(final String prefix) {
        this.locator().register(prefix);
        final Collection<ComponentContainer> containers = this.locator().containers(ComponentType.FUNCTIONAL);
        this.log().debug("Located %d functional components in prefix %s".formatted(containers.size(), prefix));
        for (final ServiceOrder order : ServiceOrder.values()) this.process(order, containers);
    }

    protected void process(final ServiceOrder order, final Collection<ComponentContainer> containers) {
        for (final ComponentProcessor<?> serviceProcessor : this.processors.get(order)) {
            for (final ComponentContainer container : containers) {
                if (container.activators().stream().allMatch(this::hasActivator)) {
                    final TypeContext<?> service = container.type();
                    if (serviceProcessor.processable(this, service)) {
                        this.log().debug("Processing component %s with registered processor %s in phase %s".formatted(container.id(), TypeContext.of(serviceProcessor).name(), order));
                        serviceProcessor.process(this, service);
                    }
                }
            }
        }
    }

    @Override
    public <T> Exceptional<T> property(final String key) {
        //noinspection unchecked
        return Exceptional.of(() -> (T) this.environmentValues.getOrDefault(key, System.getenv(key)));
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

    private void populateArguments(final String[] args) {
        for (final String arg: args) {
            final Matcher matcher = ARGUMENTS.matcher(arg);
            if (matcher.find()) this.property(matcher.group(1), matcher.group(2));
        }
    }

    protected void modify(final List<Modifier> modifiers) {
        for (final Modifier modifier : modifiers) {
            if (modifier instanceof MetaProviderModifier metaProviderModifier) {
                this.metaProvider = metaProviderModifier.provider(this);
            }
        }
        if (this.metaProvider == null) this.metaProvider = new InjectorMetaProvider(this);
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
    public void reset() {
        this.environment.context().reset();
        this.hierarchies.clear();
        this.contexts.clear();
        this.singletons.clear();
    }

    private <C> void inHierarchy(final Key<C> key, final Consumer<BindingHierarchy<C>> consumer) {
        final BindingHierarchy<C> hierarchy = (BindingHierarchy<C>) this.hierarchies.getOrDefault(key, new NativeBindingHierarchy<>(key, this));
        consumer.accept(hierarchy);
        this.hierarchies.put(key, hierarchy);
    }

    @Override
    public <T> T get(final Class<T> type, final Named named) {
        return this.get(type, BindingMetaAttribute.of(named));
    }

    @Override
    public <T> T get(final Key<T> key, final Attribute<?>... properties) {
        if (this.singletons.containsKey(key)) return (T) this.singletons.get(key);

        T instance = this.create(key, null, properties);

        // Recreating field instances ensures all fields are created through bootstrapping, allowing injection
        // points to apply correctly
        this.populate(instance);

        instance = this.inject(key, instance, properties);

        for (final ServiceOrder order : ServiceOrder.values()) instance = this.modify(order, key, instance, properties);

        // Enables all fields which are decorated with @Wired(enable=true)
        this.enable(instance);

        // Inject properties if applicable
        try {
            Bindings.enable(instance, properties);
        }
        catch (final ApplicationException e) {
            throw e.runtime();
        }

        final MetaProvider meta = this.meta();
        // Ensure the order of resolution is to first resolve the instance singleton state, and only after check the type state.
        // Typically the implementation decided whether it should be a singleton, so this cuts time complexity in half.
        if (instance != null && (meta.singleton(TypeContext.of(instance)) || meta.singleton(key.contract())))
            this.singletons.put(key, instance);

        // May be null, but we have used all possible injectors, it's up to the developer now
        return instance;
    }

    protected <T> T modify(final ServiceOrder order, final Key<T> key, T instance, final Attribute<?>... properties) {
        for (final InjectionModifier<?> serviceModifier : this.injectionModifiers.get(order)) {
            if (serviceModifier.preconditions(this, key.contract(), instance, properties))
                instance = serviceModifier.process(this, key.contract(), instance, properties);
        }
        return instance;
    }

    @Override
    public <T> T get(final TypeContext<T> type, final Attribute<?>... properties) {
        @Nullable final Exceptional<Named> meta = Bindings.lookup(BindingMetaAttribute.class, properties);
        return this.get(Key.of(type, meta.orNull()), properties);
    }

    @Override
    public <T> T get(final Class<T> type, final Attribute<?>... additionalProperties) {
        return this.get(TypeContext.of(type), additionalProperties);
    }

    @Override
    public <T> T get(final Class<T> type, final Object... varargs) {
        return this.get(type, new UseFactory(varargs));
    }

    @Nullable
    public <T> T create(final Key<T> key, final T typeInstance, final Attribute<?>[] additionalProperties) {
        final TypeContext<T> type = key.contract();
        try {
            if (null == typeInstance) {
                final Exceptional<T> instanceCandidate = this.provide(key, additionalProperties);
                Throwable cause = null;
                if (instanceCandidate.caught()) {
                    cause = instanceCandidate.error();
                }

                if (instanceCandidate.absent()) {
                    final Exceptional<T> rawCandidate = instanceCandidate.orElse(() -> this.raw(type));
                    if (rawCandidate.absent()) {
                        final Throwable finalCause = cause;
                        return this.environment().application().proxy(type, typeInstance).rethrow().orThrow(() -> finalCause);
                    }
                    else {
                        return rawCandidate.get();
                    }
                }

                return instanceCandidate.get();
            }
            return typeInstance;
        }
        catch (final Throwable e) {
            // Services can have no explicit implementation even if they are abstract.
            // Typically these services are expected to be populated through injection points later in time.
            if (type.isAbstract() && this.meta().isComponent(type)) return null;
            throw new ApplicationException(e).runtime();
        }
    }

    @Override
    public boolean hasActivator(final Class<? extends Annotation> activator) {
        final Exceptional<ServiceActivator> annotation = TypeContext.of(activator).annotation(ServiceActivator.class);
        if (annotation.absent())
            throw new IllegalArgumentException("Requested activator " + activator.getSimpleName() + " is not decorated with @ServiceActivator");

        if (this.modifiers.contains(DefaultModifiers.ACTIVATE_ALL)) return true;
        else {
            return this.activators.stream()
                    .map(Annotation::annotationType)
                    .toList()
                    .contains(activator);
        }
    }

    public <T> Exceptional<T> provide(final Key<T> type, final Attribute<?>... additionalProperties) {
        return Exceptional.of(type)
                .map(this::hierarchy)
                .flatMap(hierarchy -> {
                    // Will continue going through each provider until a provider was successful or no other providers remain
                    for (final Provider<T> provider : hierarchy.providers()) {
                        final Exceptional<T> provided = provider.provide(this, additionalProperties);
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
        this.environment().prefix(prefix);

        final Collection<TypeContext<?>> binders = this.environment().types(Binds.class);
        for (final TypeContext<?> binder : binders) {
            final Binds bindAnnotation = binder.annotation(Binds.class).get();
            this.handleBinder(binder, bindAnnotation);
        }

        final Collection<TypeContext<?>> multiBinders = this.environment().types(Combines.class);
        for (final TypeContext<?> binder : multiBinders) {
            final Combines bindAnnotation = binder.annotation(Combines.class).get();
            for (final Binds annotation : bindAnnotation.value()) {
                this.handleBinder(binder, annotation);
            }
        }
        this.process(prefix);
    }

    @Override
    public <T> T populate(final T instance) {
        if (null != instance) {
            final TypeContext<T> unproxied = TypeContext.unproxy(this, instance);
            for (final FieldContext<?> field : unproxied.fields(Inject.class)) {
                final Object fieldInstance = this.get(field.type().type());
                field.set(instance, fieldInstance);
            }
            for (final FieldContext<?> field : unproxied.fields(Context.class)) {
                this.populateContextField(field, instance);
            }
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
        field.set(instance, context.orNull());
    }

    @Override
    public void add(final ProviderContext<?> context) {
        final Key<Object> key = (Key<Object>) context.key();
        this.inHierarchy(key, hierarchy -> {
            if (context.singleton()) {
                hierarchy.add(Providers.of(context.provider().get()));
            }
            else {
                hierarchy.add(Providers.of((Supplier<Object>) context.provider()));
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
    public <T> BindingHierarchy<T> hierarchy(final Key<T> key) {
        final BindingHierarchy<T> hierarchy = (BindingHierarchy<T>) this.hierarchies.getOrDefault(key, new NativeBindingHierarchy<>(key, this));
        // onUpdate callback is purely so updates will still be saved even if the reference is lost
        if (hierarchy instanceof ContextWrappedHierarchy) return hierarchy;
        else return new ContextWrappedHierarchy<>(hierarchy, this, updated -> this.hierarchies.put(key, updated));
    }

    private <C, T extends C> void handleBinder(final TypeContext<T> binder, final Binds annotation) {
        final TypeContext<C> binds = TypeContext.of((Class<C>) annotation.value());

        if (binder.boundConstructors().isEmpty()) {
            this.handleScanned(binder, binds, annotation);
        }
        else {
            this.bind(Key.of(binds), binder.type());
        }
    }

    private <C> void handleScanned(final TypeContext<? extends C> binder, final TypeContext<C> binds, final Binds bindAnnotation) {
        final Named meta = bindAnnotation.named();
        final Key<C> key;
        if (!"".equals(meta.value())) {
            key = Key.of(binds, meta);
        }
        else {
            key = Key.of(binds);
        }
        this.inHierarchy(key, hierarchy -> hierarchy.add(Providers.of(binder)));
    }

    @Override
    public <C> void bind(final Key<C> contract, final Supplier<C> supplier) {
        this.inHierarchy(contract, hierarchy -> hierarchy.add(Providers.of(supplier)));
    }

    @Override
    public <C, T extends C> void bind(final Key<C> contract, final Class<? extends T> implementation) {
        final TypeContext<? extends T> context = TypeContext.of(implementation);
        if (context.defaultConstructor().present() || !context.injectConstructors().isEmpty()) {
            this.inHierarchy(contract, hierarchy -> hierarchy.add(Providers.of(context)));
        }
        if (!context.boundConstructors().isEmpty()) {
            this.inHierarchy(contract, hierarchy -> hierarchy.addNext(Providers.bound(implementation)));
        }
    }

    @Override
    public <C, T extends C> void bind(final Key<C> contract, final T instance) {
        this.inHierarchy(contract, hierarchy -> hierarchy.add(Providers.of(instance)));
    }
}
