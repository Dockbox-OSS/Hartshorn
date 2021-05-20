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

import org.dockbox.selene.di.InjectionPoint;
import org.dockbox.selene.di.ProvisionFailure;
import org.dockbox.selene.di.annotations.Wired;
import org.dockbox.selene.di.exceptions.ApplicationException;
import org.dockbox.selene.di.properties.InjectableType;
import org.dockbox.selene.di.properties.InjectorProperty;
import org.dockbox.selene.util.Reflect;
import org.dockbox.selene.util.SeleneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.Set;

public abstract class ManagedSeleneContext implements ApplicationContext {

    private static final Logger log = LoggerFactory.getLogger("Selene Managed Context");
    protected final transient Set<InjectionPoint<?>> injectionPoints = SeleneUtils.emptyConcurrentSet();

    @Override
    public void add(InjectionPoint<?> property) {
        if (null != property) this.injectionPoints.add(property);
    }

    public abstract <T> T create(Class<T> type, T typeInstance, InjectorProperty<?>... properties);

    public <T> T inject(Class<T> type, T typeInstance, InjectorProperty<?>... properties) {
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

    public <T> void enable(T typeInstance) {
        if (typeInstance == null) return;
        SeleneUtils.merge(Reflect.annotatedFields(typeInstance.getClass(), Wired.class)).stream()
                .filter(field -> field.getAnnotation(Wired.class).enable())
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

    public <T> T raw(Class<T> type) throws ProvisionFailure {
        try {
            Constructor<T> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            T t = ctor.newInstance();
            this.populate(t);
            return t;
        }
        catch (Exception e) {
            throw new ProvisionFailure("Could not provide raw instance", e);
        }
    }

    protected <T> @Nullable T getUnsafeInstance(Class<T> type) {
        log.warn("Attempting to get instance of [" + type.getCanonicalName() + "] through Unsafe");
        try {
            T t = Reflect.unsafeInstance(type);
            this.populate(t);
            return t;
        }
        catch (Exception e) {
            throw new ProvisionFailure("Could not create instance of [" + type.getCanonicalName() + "] through injected, raw or unsafe construction", e);
        }
    }

}
