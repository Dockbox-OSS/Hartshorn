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

package org.dockbox.selene.di;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.entity.annotations.DoNotEnable;
import org.dockbox.selene.di.annotations.Wired;
import org.dockbox.selene.di.binding.Bindings;
import org.dockbox.selene.di.exceptions.ApplicationException;
import org.dockbox.selene.di.inject.InjectSource;
import org.dockbox.selene.di.inject.Injector;
import org.dockbox.selene.di.inject.InjectorAdapter;
import org.dockbox.selene.di.properties.InjectableType;
import org.dockbox.selene.di.properties.InjectorProperty;
import org.dockbox.selene.di.properties.UseFactory;
import org.dockbox.selene.util.Reflect;
import org.dockbox.selene.util.SeleneUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.Set;

import lombok.Getter;

@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class InjectableBootstrap {

    private static final Logger log = LoggerFactory.getLogger("selene-di");
    private static InjectableBootstrap instance;
    private final transient Set<InjectionPoint<?>> injectionPoints = SeleneUtils.emptyConcurrentSet();

    @Getter
    private final InjectorAdapter adapter;

    protected InjectableBootstrap() {
        this.adapter = new InjectorAdapter(InjectSource.GUICE);
        instance(this);
    }

    public static InjectableBootstrap instance() {
        return instance;
    }

    protected static void instance(InjectableBootstrap bootstrap) {
        InjectableBootstrap.instance = bootstrap;
    }

    /**
     * Gets an instance of a provided {@link Class} type.
     *
     * @param <T>
     *         The type parameter for the instance to return
     * @param type
     *         The type of the instance
     * @param additionalProperties
     *         The properties to be passed into the type either during or after
     *         construction
     *
     * @return The instance, if present. Otherwise returns null
     */
    public <T> T instance(Class<T> type, InjectorProperty<?>... additionalProperties) {
        T typeInstance = null;

        @Nullable Exceptional<Object[]> value = Bindings.value(UseFactory.KEY, Object[].class, additionalProperties);
        if (value.present()) {
            typeInstance = this.instance(SeleneFactory.class).create(type, value.get());
            this.getInjector().populate(typeInstance);
        } else {
            // Type instance can be present if it is a module. These instances are also created using Guice
            // injectors and therefore do not need late member injection here.
            typeInstance = this.createInstanceOf(type, typeInstance, additionalProperties);
        }

        // Recreating field instances ensures all fields are created through bootstrapping, allowing injection
        // points to apply correctly
        this.getInjector().populate(typeInstance);

        typeInstance = this.applyInjectionPoints(type, typeInstance, additionalProperties);

        // Enables all fields which are not annotated with @Module or @DoNotEnable
        this.enableInjectionPoints(typeInstance);

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

    @Nullable
    private <T> T createInstanceOf(Class<T> type, T typeInstance, InjectorProperty<?>[] additionalProperties) {
        if (null == typeInstance) {
            typeInstance = this.getInjector().get(type, additionalProperties).then(() -> {
                try {
                    return this.getRawInstance(type);
                }
                catch (ProvisionFailure e) {
                    return this.getUnsafeInstance(type);
                }
            }).rethrow().get();
        }
        return typeInstance;
    }

    private <T> T applyInjectionPoints(Class<T> type, T typeInstance, InjectorProperty<?>... properties) {
        for (InjectionPoint<?> injectionPoint : this.injectionPoints) {
            if (injectionPoint.accepts(type)) {
                try {
                    //noinspection unchecked
                    typeInstance = ((InjectionPoint<T>) injectionPoint).apply(typeInstance, type, properties);
                }
                catch (ClassCastException e) {
                    log.warn("Attempted to apply injection point to incompatible type [" + type.getCanonicalName() + "]");
                }
            }
        }
        return typeInstance;
    }

    private <T> void enableInjectionPoints(T typeInstance) {
        if (typeInstance == null) return;
        SeleneUtils.merge(Reflect.annotatedFields(Wired.class, typeInstance.getClass())).stream()
                .filter(field -> field.isAnnotationPresent(DoNotEnable.class))
                .filter(field -> Reflect.assignableFrom(InjectableType.class, field.getType()))
                .map(field -> {
                    try {
                        return field.get(typeInstance);
                    }
                    catch (IllegalAccessException e) {
                        log.warn("Could not access field " + field.getName() + " in " + field.getDeclaringClass().getSimpleName());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(fieldInstance -> (InjectableType) fieldInstance)
                .filter(InjectableType::canEnable)
                .forEach(injectableType -> {
                    try {
                        injectableType.stateEnabling();
                    } catch (ApplicationException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public Injector getInjector() {
        return this.getAdapter().getInjector();
    }

    private <T> T getRawInstance(Class<T> type) throws ProvisionFailure {
        try {
            Constructor<T> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            T t = ctor.newInstance();
            this.getInjector().populate(t);
            return t;
        }
        catch (Exception e) {
            throw new ProvisionFailure("Could not provide raw instance", e);
        }
    }

    private <T> @Nullable T getUnsafeInstance(Class<T> type) {
        log.warn("Attempting to get instance of [" + type.getCanonicalName() + "] through Unsafe");
        try {
            T t = Reflect.unsafeInstance(type);
            this.getInjector().populate(t);
            return t;
        }
        catch (Exception e) {
            throw new ProvisionFailure("Could not create instance of [" + type.getCanonicalName() + "] through injected, raw or unsafe construction", e);
        }
    }

    public void injectAt(InjectionPoint<?> property) {
        if (null != property) this.injectionPoints.add(property);
    }
}
