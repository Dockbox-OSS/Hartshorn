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
import org.dockbox.hartshorn.di.MetaProviderModifier;
import org.dockbox.hartshorn.di.Modifier;
import org.dockbox.hartshorn.di.ProvisionFailure;
import org.dockbox.hartshorn.di.TypeFactory;
import org.dockbox.hartshorn.di.adapter.InjectSource;
import org.dockbox.hartshorn.di.adapter.ServiceSource;
import org.dockbox.hartshorn.di.annotations.inject.Named;
import org.dockbox.hartshorn.di.annotations.service.ServiceActivator;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.inject.Binder;
import org.dockbox.hartshorn.di.inject.InjectionModifier;
import org.dockbox.hartshorn.di.inject.Injector;
import org.dockbox.hartshorn.di.inject.ProviderContext;
import org.dockbox.hartshorn.di.inject.wired.BoundContext;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.di.properties.BindingMetaAttribute;
import org.dockbox.hartshorn.di.properties.UseFactory;
import org.dockbox.hartshorn.di.services.ComponentLocator;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class HartshornApplicationContext extends ManagedHartshornContext {

    private final Injector injector;
    private final ComponentLocator locator;
    private final List<Modifier> modifiers;
    private final Map<Class<?>, Object> singletons = HartshornUtils.emptyConcurrentMap();
    private MetaProvider metaProvider;


    public HartshornApplicationContext(Class<?> activationSource, Modifier... modifiers) {
        super(activationSource);
        InjectSource injectSource = this.getSource(this.activator().injectSource(), this.activator().inject());
        ServiceSource serviceSource = this.getSource(this.activator().serviceSource(), this.activator().service());

        this.injector = injectSource.create();
        this.locator = serviceSource.create();
        this.modifiers = HartshornUtils.asUnmodifiableList(modifiers);
        this.modify(this.modifiers);

        this.bind(ApplicationContext.class, this);
        this.bind(MetaProvider.class, this.metaProvider);
        this.bind(ComponentLocator.class, this.locator());
    }

    private <T, E extends T> T getSource(Class<T> type, String target) {
        try {
            if (type.isEnum()) {
                for (Enum<?> enumConstant : (Enum<?>[]) type.getEnumConstants()) {
                    if (enumConstant.name().equalsIgnoreCase(target)) {
                        //noinspection unchecked
                        return (T) enumConstant;
                    }
                }
            }
            else {
                final Constructor<T> constructor = type.getConstructor();
                return constructor.newInstance();
            }
        }
        catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException | ClassCastException e) {
            return null;
        }
        return null;
    }

    protected void modify(List<Modifier> modifiers) {
        for (Modifier modifier : modifiers) {
            if (modifier instanceof MetaProviderModifier metaProviderModifier) {
                this.metaProvider = metaProviderModifier.provider();
            }
        }
        if (this.metaProvider == null) this.metaProvider = new InjectorMetaProvider();
    }

    @Override
    public <T> T get(Class<T> type, Named named) {
        return this.get(type, BindingMetaAttribute.of(named));
    }

    @Override
    public <T> T get(Class<T> type, Attribute<?>... properties) {
        T instance = null;

        if (this.singletons.containsKey(type)) //noinspection unchecked
            return (T) this.singletons.get(type);

        Exceptional<Object[]> value = Bindings.lookup(UseFactory.class, properties);
        if (value.present()) {
            instance = this.get(TypeFactory.class).with(properties).create(type, value.get());
        } else {
            // Type instance can be present if it is a service. These instances are also created using Guice
            // injectors and therefore do not need late member injection here.
            instance = this.create(type, instance, properties);
        }

        // Recreating field instances ensures all fields are created through bootstrapping, allowing injection
        // points to apply correctly
        this.populate(instance);

        instance = this.inject(type, instance, properties);

        for (InjectionModifier<?> serviceModifier : this.injectionModifiers) {
            if (serviceModifier.preconditions(type, instance, properties))
                instance = serviceModifier.process(this, type, instance, properties);
        }

        // Enables all fields which are decorated with @Wired(enable=true)
        this.enable(instance);

        // Inject properties if applicable
        try {
            Bindings.enable(instance, properties);
        }
        catch (ApplicationException e) {
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
    public <T> T get(Class<T> type, Object... varargs) {
        return this.get(type, TypeFactory.use(varargs));
    }

    @Override
    public <T> void with(Class<T> type, Consumer<T> consumer) {
        final T instance = this.get(type);
        if (null != instance) consumer.accept(instance);
    }

    @Override
    public Binder binder() {
        return this.internalInjector();
    }
    
    protected Injector internalInjector() {
        return this.injector;
    }

    @Nullable
    public <T> T create(Class<T> type, T typeInstance, Attribute<?>[] additionalProperties) {
        try {
            if (null == typeInstance) {
                Exceptional<T> instanceCandidate = this.internalInjector().get(type, additionalProperties);
                Throwable cause = null;
                if (instanceCandidate.caught()) {
                    cause = instanceCandidate.error();
                }

                if (instanceCandidate.absent()) {
                    final Exceptional<T> rawCandidate = instanceCandidate.orElse(() -> this.raw(type));
                    if (rawCandidate.absent() && rawCandidate.caught()) {
                        if (cause == null) rawCandidate.rethrow();
                        else {
                            return ApplicationContextAware.instance().proxy(type, typeInstance).orNull();
                        }
                    }
                    else {
                        return rawCandidate.get();
                    }
                }

                return instanceCandidate.get();
            }
            return typeInstance;
        } catch (ProvisionFailure e) {
            // Services can have no explicit implementation even if they are abstract.
            // Typically these services are expected to be populated through injection points later in time.
            if (Reflect.isAbstract(type) && ApplicationContextAware.instance().context().meta().component(type)) return null;
            throw e;
        }
    }

    @Override
    public boolean hasActivator(Class<? extends Annotation> activator) {
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
    public <T> T populate(T o) {
        return this.internalInjector().populate(o);
    }

    @Override
    public void add(BoundContext<?, ?> context) {
        this.internalInjector().add(context);
    }

    @Override
    public void add(ProviderContext<?, ?> context) {
        this.internalInjector().add(context);
    }

    @Override
    public void bind(InjectConfiguration configuration) {
        this.internalInjector().bind(configuration);
    }

    @Override
    public void bind(String prefix) {
        this.internalInjector().bind(prefix);
        super.process(prefix);
    }

    @Override
    public <T, I extends T> Exceptional<BoundContext<T, I>> firstWire(Class<T> contract, Named named) {
        return this.internalInjector().firstWire(contract, named);
    }

    @Override
    public <T> T invoke(Method method) {
        return this.internalInjector().invoke(method);
    }

    @Override
    public <T> T invoke(Method method, Object instance) {
        return this.internalInjector().invoke(method, instance);
    }
}
