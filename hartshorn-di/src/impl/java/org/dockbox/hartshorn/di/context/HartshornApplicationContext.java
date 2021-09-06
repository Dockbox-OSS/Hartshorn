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
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.ApplicationContextAware;
import org.dockbox.hartshorn.di.ContextWrappedHierarchy;
import org.dockbox.hartshorn.di.DefaultModifiers;
import org.dockbox.hartshorn.di.InjectConfiguration;
import org.dockbox.hartshorn.di.InjectorMetaProvider;
import org.dockbox.hartshorn.di.Key;
import org.dockbox.hartshorn.di.MetaProvider;
import org.dockbox.hartshorn.di.MetaProviderModifier;
import org.dockbox.hartshorn.di.Modifier;
import org.dockbox.hartshorn.di.NativeBindingHierarchy;
import org.dockbox.hartshorn.di.TypeFactory;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.annotations.inject.Combines;
import org.dockbox.hartshorn.di.annotations.service.ServiceActivator;
import org.dockbox.hartshorn.di.binding.BindingHierarchy;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.binding.Provider;
import org.dockbox.hartshorn.di.binding.Providers;
import org.dockbox.hartshorn.di.context.element.FieldContext;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.inject.InjectionModifier;
import org.dockbox.hartshorn.di.inject.ProviderContext;
import org.dockbox.hartshorn.di.inject.wired.BoundContext;
import org.dockbox.hartshorn.di.inject.wired.ConstructorBoundContext;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.di.properties.BindingMetaAttribute;
import org.dockbox.hartshorn.di.properties.UseFactory;
import org.dockbox.hartshorn.di.services.ComponentLocator;
import org.dockbox.hartshorn.di.services.ComponentLocatorImpl;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;

@SuppressWarnings("unchecked")
public class HartshornApplicationContext extends ManagedHartshornContext {

    private final ComponentLocator locator;
    private final List<Modifier> modifiers;
    private final Map<Key<?>, Object> singletons = HartshornUtils.emptyConcurrentMap();
    private final transient Set<BoundContext<?, ?>> bindings = HartshornUtils.emptyConcurrentSet();
    private final Map<Key<?>, BindingHierarchy<?>> hierarchies = HartshornUtils.emptyConcurrentMap();
    private MetaProvider metaProvider;

    public HartshornApplicationContext(final ApplicationContextAware application, final Class<?> activationSource, final Collection<String> prefixes, final Modifier... modifiers) {
        super(application, activationSource, prefixes);

        this.locator = new ComponentLocatorImpl(this);
        this.modifiers = HartshornUtils.asUnmodifiableList(modifiers);
        this.modify(this.modifiers);

        this.bind(Key.of(ApplicationContext.class), this);
        this.bind(Key.of(MetaProvider.class), this.metaProvider);
        this.bind(Key.of(ComponentLocator.class), this.locator());
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
        super.reset();
        this.hierarchies.clear();
        this.bindings.clear();
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
        T instance = null;

        if (this.singletons.containsKey(key)) return (T) this.singletons.get(key);

        final Exceptional<Object[]> value = Bindings.lookup(UseFactory.class, properties);
        if (value.present()) {
            instance = this.get(TypeFactory.class).with(properties).create(key.contract(), value.get());
        }
        else {
            instance = this.create(key, instance, properties);
        }

        // Recreating field instances ensures all fields are created through bootstrapping, allowing injection
        // points to apply correctly
        this.populate(instance);

        instance = this.inject(key, instance, properties);

        for (final InjectionModifier<?> serviceModifier : this.injectionModifiers) {
            if (serviceModifier.preconditions(this, key.contract(), instance, properties))
                instance = serviceModifier.process(this, key.contract(), instance, properties);
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

        final MetaProvider meta = this.meta();
        // Ensure the order of resolution is to first resolve the instance singleton state, and only after check the type state.
        // Typically the implementation decided whether it should be a singleton, so this cuts time complexity in half.
        if (instance != null && (meta.singleton(TypeContext.of(instance)) || meta.singleton(key.contract())))
            this.singletons.put(key, instance);

        // May be null, but we have used all possible injectors, it's up to the developer now
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
        return this.get(type, TypeFactory.use(varargs));
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
        else return super.hasActivator(activator);
    }

    public <T> Exceptional<T> provide(final Key<T> type, final Attribute<?>... additionalProperties) {
        return Exceptional.of(type)
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
        super.process(prefix);
    }

    @Override
    public <T, I extends T> Exceptional<BoundContext<T, I>> firstWire(final TypeContext<T> contract, final Named property) {
        for (final BoundContext<?, ?> binding : this.bindings) {
            if (binding.contract().equals(contract)) {
                if (!"".equals(binding.name())) {
                    if (property == null || !binding.name().equals(property.value())) continue;
                }
                return Exceptional.of((BoundContext<T, I>) binding);
            }
        }
        return Exceptional.empty();
    }

    @Override
    public <T> T populate(final T instance) {
        if (null != instance) {
            for (final FieldContext<?> field : TypeContext.unproxy(this, instance).fields(Inject.class)) {
                final Object fieldInstance = this.get(field.type().type());
                field.set(instance, fieldInstance);
            }
        }
        return instance;
    }

    @Override
    public void add(final BoundContext<?, ?> context) {
        this.bindings.add(context);
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
        if (context.boundConstructors().isEmpty()) {
            this.inHierarchy(contract, hierarchy -> hierarchy.add(Providers.of(context)));
        } else {
            this.bindings.add(new ConstructorBoundContext<>(contract, context));
        }
    }

    @Override
    public <C, T extends C> void bind(final Key<C> contract, final T instance) {
        this.inHierarchy(contract, hierarchy -> hierarchy.add(Providers.of(instance)));
    }
}
