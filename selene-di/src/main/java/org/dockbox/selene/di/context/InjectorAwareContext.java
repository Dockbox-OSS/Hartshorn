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

package org.dockbox.selene.di.context;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.di.InjectConfiguration;
import org.dockbox.selene.di.ProvisionFailure;
import org.dockbox.selene.di.SeleneFactory;
import org.dockbox.selene.di.adapter.ContextAdapter;
import org.dockbox.selene.di.annotations.Named;
import org.dockbox.selene.di.annotations.Service;
import org.dockbox.selene.di.binding.Bindings;
import org.dockbox.selene.di.exceptions.ApplicationException;
import org.dockbox.selene.di.inject.BeanContext;
import org.dockbox.selene.di.inject.Binder;
import org.dockbox.selene.di.inject.Injector;
import org.dockbox.selene.di.inject.wired.WireContext;
import org.dockbox.selene.di.properties.InjectableType;
import org.dockbox.selene.di.properties.InjectorProperty;
import org.dockbox.selene.di.properties.UseFactory;
import org.dockbox.selene.di.services.ServiceLocator;
import org.dockbox.selene.di.services.ServiceModifier;
import org.dockbox.selene.util.Reflect;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import lombok.Getter;

public class InjectorAwareContext extends ManagedSeleneContext {

    @Getter
    private final ContextAdapter adapter;

    public InjectorAwareContext(Class<?> activationSource) {
        super(activationSource);
        this.adapter = new ContextAdapter(this.getActivator().inject(), this.getActivator().services());
        this.bind(ApplicationContext.class, this);
    }

    @Override
    public <T> T get(Class<T> type, InjectorProperty<?>... additionalProperties) {
        T typeInstance = null;

        @Nullable Exceptional<Object[]> value = Bindings.value(UseFactory.KEY, Object[].class, additionalProperties);
        if (value.present()) {
            typeInstance = this.get(SeleneFactory.class).with(additionalProperties).create(type, value.get());
            this.injector().populate(typeInstance);
        } else {
            // Type instance can be present if it is a module. These instances are also created using Guice
            // injectors and therefore do not need late member injection here.
            typeInstance = this.create(type, typeInstance, additionalProperties);
        }

        // Recreating field instances ensures all fields are created through bootstrapping, allowing injection
        // points to apply correctly
        this.injector().populate(typeInstance);

        typeInstance = this.inject(type, typeInstance, additionalProperties);

        if (type.isAnnotationPresent(Service.class)) {
            for (ServiceModifier<?> serviceModifier : this.serviceModifiers) {
                if (serviceModifier.preconditions(type, typeInstance, additionalProperties))
                    typeInstance = serviceModifier.process(this, type, typeInstance, additionalProperties);
            }
        }

        // Enables all fields which are not decorated with @Module or @DoNotEnable
        this.enable(typeInstance);

        // Inject properties if applicable
        if (typeInstance instanceof InjectableType && ((InjectableType) typeInstance).canEnable()) {
            try {
                ((InjectableType) typeInstance).stateEnabling(additionalProperties);
            } catch (ApplicationException e) {
                throw new RuntimeException(e);
            }
        }

        // May be null, but we have used all possible injectors, it's up to the developer now
        return typeInstance;
    }

    @Override
    public <T> T get(Class<T> type, Object... varargs) {
        return this.get(type, SeleneFactory.use(varargs));
    }

    @Override
    public <T> void with(Class<T> type, Consumer<T> consumer) {
        final T instance = this.get(type);
        if (null != instance) consumer.accept(instance);
    }

    @Override
    public Binder getBinder() {
        return this.injector();
    }

    @Override
    public Injector injector() {
        return this.adapter.getInjector();
    }

    @Nullable
    public <T> T create(Class<T> type, T typeInstance, InjectorProperty<?>[] additionalProperties) {
        try {
            if (null == typeInstance) {
                typeInstance = this.injector().get(type, additionalProperties).then(() -> this.raw(type)).rethrow().get();
            }
            return typeInstance;
        } catch (ProvisionFailure e) {
            // Services can have no explicit implementation even if they are abstract.
            // Typically these services are expected to be populated through injection points later in time.
            if (!Reflect.isConcrete(type) && type.isAnnotationPresent(Service.class)) return null;
            throw e;
        }
    }

    @Override
    protected ServiceLocator locator() {
        return this.adapter.getLocator();
    }

    @Override
    public <T> T populate(T o) {
        return this.injector().populate(o);
    }

    @Override
    public void add(WireContext<?, ?> context) {
        this.injector().add(context);
    }

    @Override
    public void add(BeanContext<?, ?> context) {
        this.injector().add(context);
    }

    @Override
    public void bind(InjectConfiguration configuration) {
        this.injector().bind(configuration);
    }

    @Override
    public void bind(String prefix) {
        this.injector().bind(prefix);
        super.process(prefix);
    }

    @Override
    public <T, I extends T> Exceptional<WireContext<T, I>> firstWire(Class<T> contract, InjectorProperty<Named> property) {
        return this.injector().firstWire(contract, property);
    }
}
