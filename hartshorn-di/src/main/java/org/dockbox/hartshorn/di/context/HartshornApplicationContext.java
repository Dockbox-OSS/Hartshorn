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
import org.dockbox.hartshorn.di.adapter.ContextAdapter;
import org.dockbox.hartshorn.di.annotations.inject.Named;
import org.dockbox.hartshorn.di.annotations.service.ServiceActivator;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.inject.BeanContext;
import org.dockbox.hartshorn.di.inject.Binder;
import org.dockbox.hartshorn.di.inject.InjectionModifier;
import org.dockbox.hartshorn.di.inject.Injector;
import org.dockbox.hartshorn.di.inject.wired.WireContext;
import org.dockbox.hartshorn.di.properties.InjectableType;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.di.properties.UseFactory;
import org.dockbox.hartshorn.di.services.ComponentLocator;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import lombok.Getter;

public class HartshornApplicationContext extends ManagedHartshornContext {

    @Getter
    private final ContextAdapter adapter;
    private final List<Modifier> modifiers;
    private final Map<Class<?>, Object> singletons = HartshornUtils.emptyConcurrentMap();
    private MetaProvider metaProvider;


    public HartshornApplicationContext(Class<?> activationSource, Modifier... modifiers) {
        super(activationSource);
        this.adapter = new ContextAdapter(this.getActivator().inject(), this.getActivator().services());
        this.modifiers = HartshornUtils.asUnmodifiableList(modifiers);
        this.modify(this.modifiers);

        this.bind(ApplicationContext.class, this);
        this.bind(MetaProvider.class, this.metaProvider);
        this.bind(ComponentLocator.class, this.locator());
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
    public <T> T get(Class<T> type, InjectorProperty<?>... additionalProperties) {
        T typeInstance = null;

        if (this.singletons.containsKey(type)) //noinspection unchecked
            return (T) this.singletons.get(type);

        Exceptional<Object[]> value = Bindings.value(UseFactory.KEY, Object[].class, additionalProperties);
        if (value.present()) {
            typeInstance = this.get(TypeFactory.class).with(additionalProperties).create(type, value.get());
            this.populate(typeInstance);
        } else {
            // Type instance can be present if it is a service. These instances are also created using Guice
            // injectors and therefore do not need late member injection here.
            typeInstance = this.create(type, typeInstance, additionalProperties);
        }

        // Recreating field instances ensures all fields are created through bootstrapping, allowing injection
        // points to apply correctly
        this.populate(typeInstance);

        typeInstance = this.inject(type, typeInstance, additionalProperties);

        for (InjectionModifier<?> serviceModifier : this.injectionModifiers) {
            if (serviceModifier.preconditions(type, typeInstance, additionalProperties))
                typeInstance = serviceModifier.process(this, type, typeInstance, additionalProperties);
        }

        // Enables all fields which are decorated with @Wired(enable=true)
        this.enable(typeInstance);

        // Inject properties if applicable
        if (typeInstance instanceof InjectableType && ((InjectableType) typeInstance).canEnable()) {
            try {
                ((InjectableType) typeInstance).stateEnabling(additionalProperties);
            } catch (ApplicationException e) {
                throw new RuntimeException(e);
            }
        }

        if (typeInstance != null && ApplicationContextAware.instance().getContext().meta().isSingleton(type)) this.singletons.put(type, typeInstance);

        // May be null, but we have used all possible injectors, it's up to the developer now
        return typeInstance;
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
    public Binder getBinder() {
        return this.internalInjector();
    }
    
    protected Injector internalInjector() {
        return this.adapter.getInjector();
    }

    @Nullable
    public <T> T create(Class<T> type, T typeInstance, InjectorProperty<?>[] additionalProperties) {
        try {
            if (null == typeInstance) {
                typeInstance = this.internalInjector()
                        .get(type, additionalProperties)
                        .orElse(() -> this.raw(type)).rethrow().get();
            }
            return typeInstance;
        } catch (ProvisionFailure e) {
            // Services can have no explicit implementation even if they are abstract.
            // Typically these services are expected to be populated through injection points later in time.
            if (Reflect.isAbstract(type) && ApplicationContextAware.instance().getContext().meta().isComponent(type)) return null;
            throw e;
        }
    }

    @Override
    public boolean hasActivator(Class<? extends Annotation> activator) {
        if (!activator.isAnnotationPresent(ServiceActivator.class))
            throw new IllegalArgumentException("Requested activator " + activator.getSimpleName() + " is not decorated with @ServiceActivator");

        if (this.modifiers.contains(DefaultModifiers.ACTIVATE_ALL)) return true;
        else return super.hasActivator(activator);
    }

    @Override
    public ComponentLocator locator() {
        return this.adapter.getLocator();
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
    public void add(WireContext<?, ?> context) {
        this.internalInjector().add(context);
    }

    @Override
    public void add(BeanContext<?, ?> context) {
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
    public <T, I extends T> Exceptional<WireContext<T, I>> firstWire(Class<T> contract, InjectorProperty<Named> property) {
        return this.internalInjector().firstWire(contract, property);
    }

    @Override
    public <T, I extends T> Exceptional<Class<I>> type(Class<T> type) {
        return this.internalInjector().type(type);
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
