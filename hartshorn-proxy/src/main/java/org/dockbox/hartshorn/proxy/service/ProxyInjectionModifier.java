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

package org.dockbox.hartshorn.proxy.service;

import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.inject.InjectionModifier;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.proxy.ProxyProperty;
import org.dockbox.hartshorn.proxy.ProxyUtil;
import org.dockbox.hartshorn.proxy.annotations.UseProxying;
import org.dockbox.hartshorn.proxy.handle.ProxyHandler;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public class ProxyInjectionModifier implements InjectionModifier<UseProxying> {

    @Override
    public <T> boolean preconditions(Class<T> type, @Nullable T instance, InjectorProperty<?>... properties) {
        // Unchecked as ProxyProperty has generic type parameters
        //noinspection unchecked
        return Bindings.has(ProxyProperty.class, properties);
    }

    @Override
    public <T> T process(ApplicationContext context, Class<T> type, @Nullable T instance, InjectorProperty<?>... properties) {
        try {
            ProxyHandler<T> handler = ProxyUtil.handler(type, instance);

            for (InjectorProperty<?> property : properties) {
                if (property instanceof ProxyProperty) {
                    //noinspection unchecked
                    ProxyProperty<T, ?> proxyProperty = (ProxyProperty<T, ?>) property;
                    handler.delegate(proxyProperty);
                }
            }
            return handler.proxy();
        }
        catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException | ClassCastException e) {
            return instance;
        }
    }

    @Override
    public Class<UseProxying> activator() {
        return UseProxying.class;
    }
}
