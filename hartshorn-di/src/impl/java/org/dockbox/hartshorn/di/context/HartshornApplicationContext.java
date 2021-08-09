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

package org.dockbox.hartshorn.di.context;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.MetaProvider;
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.ApplicationContextAware;
import org.dockbox.hartshorn.di.DefaultModifiers;
import org.dockbox.hartshorn.di.InjectConfiguration;
import org.dockbox.hartshorn.di.InjectorMetaProvider;
import org.dockbox.hartshorn.di.Key;
import org.dockbox.hartshorn.di.MetaProviderModifier;
import org.dockbox.hartshorn.di.Modifier;
import org.dockbox.hartshorn.di.NativeBindingHierarchy;
import org.dockbox.hartshorn.di.TypeFactory;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.annotations.inject.Bound;
import org.dockbox.hartshorn.di.annotations.inject.Combines;
import org.dockbox.hartshorn.di.annotations.inject.Named;
import org.dockbox.hartshorn.di.annotations.inject.Wired;
import org.dockbox.hartshorn.di.annotations.service.ServiceActivator;
import org.dockbox.hartshorn.di.binding.BindingHierarchy;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.binding.InstanceProvider;
import org.dockbox.hartshorn.di.binding.Provider;
import org.dockbox.hartshorn.di.binding.StaticProvider;
import org.dockbox.hartshorn.di.binding.SupplierProvider;
import org.dockbox.hartshorn.di.inject.InjectionModifier;
import org.dockbox.hartshorn.di.inject.ProviderContext;
import org.dockbox.hartshorn.di.inject.wired.BoundContext;
import org.dockbox.hartshorn.di.inject.wired.ConstructorBoundContext;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.di.properties.BindingMetaAttribute;
import org.dockbox.hartshorn.di.properties.UseFactory;
import org.dockbox.hartshorn.di.services.ComponentLocator;
import org.dockbox.hartshorn.di.services.SimpleComponentLocator;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HartshornApplicationContext extends ManagedHartshornContext {

    private final ComponentLocator locator;
    private final List<Modifier> modifiers;
    private final Map<Class<?>, Object> singletons = HartshornUtils.emptyConcurrentMap();
    private MetaProvider metaProvider;

    private final transient Set<BoundContext<?, ?>> bindings = HartshornUtils.emptyConcurrentSet();
    private final Map<Key<?>, BindingHierarchy<?>> hierarchies = HartshornUtils.emptyConcurrentMap();


    public HartshornApplicationContext(final Class<?> activationSource, final Modifier... modifiers) {
        super(activationSource);

        this.locator = new SimpleComponentLocator();
        this.modifiers = HartshornUtils.asUnmodifiableList(modifiers);
        this.modify(this.modifiers);

        this.bind(Key.of(ApplicationContext.class), this);
        this.bind(Key.of(MetaProvider.class), this.metaProvider);
        this.bind(Key.of(ComponentLocator.class), this.locator());
    }

    protected void modify(final List<Modifier> modifiers) {
        for (final Modifier modifier : modifiers) {
            if (modifier instanceof MetaProviderModifier metaProviderModifier) {
                this.metaProvider = metaProviderModifier.provider();
            }
        }
        if (this.metaProvider == null) this.metaProvider = new InjectorMetaProvider();
    }

    @Override
    public <T> T get(final Class<T> type, final Named named) {
        return this.get(type, BindingMetaAttribute.of(named));
    }

    @Override
    public <T> T get(final Class<T> type, final Attribute<?>... properties) {
        T instance = null;

        if (this.singletons.containsKey(type)) //noinspection unchecked
            return (T) this.singletons.get(type);

        final Exceptional<Object[]> value = Bindings.lookup(UseFactory.class, properties);
        if (value.present()) {
            instance = this.get(TypeFactory.class).with(properties).create(type, value.get());
        } else {
            instance = this.create(type, instance, properties);
        }

        // Recreating field instances ensures all fields are created through bootstrapping, allowing injection
        // points to apply correctly
        this.populate(instance);

        instance = this.inject(type, instance, properties);

        for (final InjectionModifier<?> serviceModifier : this.injectionModifiers) {
            if (serviceModifier.preconditions(type, instance, properties))
                instance = serviceModifier.process(this, type, instance, properties);
        }

        // Enables all fields which are decorated with @Wired(enable=true)
        this.enable(instance);

        // Inject properties if applicable
        try {
            Bindings.enable(instance, properties);
        }
        catch (final ApplicationException e) {
            throw e.runtime();
        }

        final MetaProvider meta = ApplicationContextAware.instance().context().meta();
        // Ensure the order of resolution is to first resolve the instance singleton state, and only after check the type state.
        // Typically the implementation decided whether it should be a singleton, so this cuts time complexity in half.
        if (instance != null && (meta.singleton(instance.getClass()) || meta.singleton(type)))
            this.singletons.put(type, instance);

        // May be null, but we have used all possible injectors, it's up to the developer now
        return instance;
    }

    @Override
    public <T> T get(final Class<T> type, final Object... varargs) {
        return this.get(type, TypeFactory.use(varargs));
    }

    @Override
    public <T> void with(final Class<T> type, final Consumer<T> consumer) {
        final T instance = this.get(type);
        if (null != instance) consumer.accept(instance);
    }

    @Nullable
    public <T> T create(final Class<T> type, final T typeInstance, final Attribute<?>[] additionalProperties) {
        try {
            if (null == typeInstance) {
                final Exceptional<T> instanceCandidate = this.provide(type, additionalProperties);
                Throwable cause = null;
                if (instanceCandidate.caught()) {
                    cause = instanceCandidate.error();
                }

                if (instanceCandidate.absent()) {
                    final Exceptional<T> rawCandidate = instanceCandidate.orElse(() -> this.raw(type));
                    if (rawCandidate.absent() && rawCandidate.caught()) {
                        final Throwable finalCause = cause;
                        return ApplicationContextAware.instance().proxy(type, typeInstance).orThrow(() -> finalCause);
                    }
                    else {
                        return rawCandidate.get();
                    }
                }

                return instanceCandidate.get();
            }
            return typeInstance;
        } catch (final Throwable e) {
            // Services can have no explicit implementation even if they are abstract.
            // Typically these services are expected to be populated through injection points later in time.
            if (Reflect.isAbstract(type) && ApplicationContextAware.instance().context().meta().component(type)) return null;
            throw new ApplicationException(e).runtime();
        }
    }

    @Override
    public boolean hasActivator(final Class<? extends Annotation> activator) {
        final Exceptional<ServiceActivator> annotation = Reflect.annotation(activator, ServiceActivator.class);
        if (annotation.absent())
            throw new IllegalArgumentException("Requested activator " + activator.getSimpleName() + " is not decorated with @ServiceActivator");

        if (this.modifiers.contains(DefaultModifiers.ACTIVATE_ALL)) return true;
        else return super.hasActivator(activator);
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
        this.hierarchies.clear();
        this.bindings.clear();
    }

    @Override
    public <T> T populate(final T instance) {
        if (null != instance) {
            for (final Field field : Reflect.fields(instance.getClass(), Wired.class)) {
                final Object fieldInstance = ApplicationContextAware.instance().context().get(field.getType());
                Reflect.set(field, instance, fieldInstance);
            }
        }
        return instance;
    }

    @Override
    public void add(final BoundContext<?, ?> context) {
        this.bindings.add(context);
    }

    @Override
    public void add(final ProviderContext<?, ?> context) {
        //noinspection unchecked
        final Key<Object> key = (Key<Object>) context.key();
        this.inHierarchy(key, hierarchy -> {
            if (context.singleton()) {
                hierarchy.add(new InstanceProvider<>(context.provider().get()));
            }
            else {
                //noinspection unchecked
                hierarchy.add(new SupplierProvider<>((Supplier<Object>) context.provider()));
            }
        });
    }

    @Override
    public void bind(final InjectConfiguration configuration) {
        configuration.binder(this).collect();
    }

    @Override
    public void bind(final String prefix) {
        Reflect.prefix(prefix);

        final Collection<Class<?>> binders = Reflect.types(Binds.class);
        for (final Class<?> binder : binders) {
            final Binds bindAnnotation = Reflect.annotation(binder, Binds.class).get();
            this.handleBinder(binder, bindAnnotation);
        }

        final Collection<Class<?>> multiBinders = Reflect.types(Combines.class);
        for (final Class<?> binder : multiBinders) {
            final Combines bindAnnotation = Reflect.annotation(binder, Combines.class).get();
            for (final Binds annotation : bindAnnotation.value()) {
                this.handleBinder(binder, annotation);
            }
        }
        super.process(prefix);
    }

    @Override
    public <T, I extends T> Exceptional<BoundContext<T, I>> firstWire(final Class<T> contract, final Named property) {
        for (final BoundContext<?, ?> binding : this.bindings) {
            if (binding.contract().equals(contract)) {
                if (!"".equals(binding.name())) {
                    if (property == null || !binding.name().equals(property.value())) continue;
                }
                //noinspection unchecked
                return Exceptional.of((BoundContext<T, I>) binding);
            }
        }
        return Exceptional.empty();
    }

    @Override
    public <T> T invoke(final Method method) {
        return this.invoke(method, ApplicationContextAware.instance().context().get(method.getDeclaringClass()));
    }

    @Override
    public <T> T invoke(final Method method, final Object instance) {
        final Parameter[] parameters = method.getParameters();
        final Object[] invokingParameters = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            final Exceptional<Named> annotation = Reflect.annotation(parameter, Named.class);
            if (annotation.present()) {
                invokingParameters[i] = ApplicationContextAware.instance().context().get(parameter.getType(), annotation.get());
            } else {
                invokingParameters[i] = ApplicationContextAware.instance().context().get(parameter.getType());
            }
        }
        try {
            //noinspection unchecked
            return (T) method.invoke(instance, invokingParameters);
        }
        catch (final Throwable e) {
            return null;
        }
    }

    @Override
    public <T> void add(final BindingHierarchy<T> hierarchy) {
        this.hierarchies.put(hierarchy.key(), hierarchy);
    }

    @Override
    public <T> void merge(final BindingHierarchy<T> hierarchy) {
        //noinspection unchecked
        final BindingHierarchy<T> existing = (BindingHierarchy<T>) this.hierarchies.get(hierarchy.key());
        if (existing != null) {
            this.hierarchies.put(hierarchy.key(), hierarchy.merge(existing));
        } else {
            this.add(hierarchy);
        }
    }

    @Override
    public <T> Exceptional<BindingHierarchy<T>> hierarchy(final Key<T> key) {
        //noinspection unchecked
        return Exceptional.of(this.hierarchies.getOrDefault(key, null)).map(hierarchy -> (BindingHierarchy<T>) hierarchy);
    }

    public <T> Exceptional<T> provide(final Class<T> type, final Attribute<?>... additionalProperties) {
        //noinspection unchecked
        return Exceptional.of(() -> {
                    @Nullable final Exceptional<Named> meta = Bindings.lookup(BindingMetaAttribute.class, additionalProperties);
                    if (meta.present()) return Key.of(type, meta.get());
                    else return Key.of(type);
                })
                .map(key -> this.hierarchies.getOrDefault(key, null))
                .map(hierarchy -> (BindingHierarchy<T>) hierarchy)
                .flatMap(hierarchy -> {
                    // Will continue going through each provider until a provider was successful or no other providers remain
                    for (final Provider<T> provider : hierarchy.providers()) {
                        final Exceptional<T> provided = provider.provide();
                        if (provided.present()) return provided;
                    }
                    return Exceptional.empty();
                });
    }

    @Override
    public <C> void provide(final Key<C> contract, final Supplier<C> supplier) {
        this.inHierarchy(contract, hierarchy -> hierarchy.add(new SupplierProvider<>(supplier)));
    }

    @Override
    public <C, T extends C> void bind(final Key<C> contract, final Class<? extends T> implementation) {
        this.inHierarchy(contract, hierarchy -> hierarchy.add(new StaticProvider<>(implementation)));
    }

    @Override
    public <C, T extends C> void bind(final Key<C> contract, final T instance) {
        this.inHierarchy(contract, hierarchy -> hierarchy.add(new InstanceProvider<>(instance)));
    }

    @Override
    public <C, T extends C> void manual(final Key<C> contract, final Class<? extends T> implementation) {
        if (Reflect.constructors(implementation, Bound.class).isEmpty())
            throw new IllegalArgumentException("Implementation should contain at least one constructor decorated with @Bound");
        this.bindings.add(new ConstructorBoundContext<>(contract, implementation));
    }

    private <C, T extends C> void handleBinder(final Class<T> binder, final Binds annotation) {
        //noinspection unchecked
        final Class<C> binds = (Class<C>) annotation.value();

        if (Reflect.constructors(binder, Bound.class).isEmpty()) {
            this.handleScanned(binder, binds, annotation);
        }
        else {
            //noinspection unchecked
            this.manual(Key.of((Class<Object>) binds), binder);
        }
    }

    private <C> void handleScanned(final Class<? extends C> binder, final Class<C> binds, final Binds bindAnnotation) {
        final Named meta = bindAnnotation.named();
        final Key<C> key;
        if (!"".equals(meta.value())) {
            key = Key.of(binds, meta);
        }
        else {
            key = Key.of(binds);
        }
        this.inHierarchy(key, hierarchy -> hierarchy.add(new StaticProvider<>(binder)));
    }

    private <C> void inHierarchy(final Key<C> key, final Consumer<BindingHierarchy<C>> consumer) {
        //noinspection unchecked
        final BindingHierarchy<C> hierarchy = (BindingHierarchy<C>) this.hierarchies.getOrDefault(key, new NativeBindingHierarchy<>(key));
        consumer.accept(hierarchy);
        this.hierarchies.put(key, hierarchy);
    }
}
