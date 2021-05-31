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

package org.dockbox.selene.proxy;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.annotations.PostBootstrap;
import org.dockbox.selene.api.annotations.UseBootstrap;
import org.dockbox.selene.api.exceptions.Except;
import org.dockbox.selene.di.InjectionPoint;
import org.dockbox.selene.di.annotations.Service;
import org.dockbox.selene.di.properties.InjectorProperty;
import org.dockbox.selene.proxy.handle.ProxyHandler;

import java.lang.reflect.InvocationTargetException;

@Service(activator = UseBootstrap.class)
public class ProxyInjectionService {

    @PostBootstrap
    public void prepareInjectionPoints() {
        ProxyableBootstrap.boostrapDelegates();
        Selene.context().add(InjectionPoint.of(Object.class, (instance, type, properties) -> {
            ProxyHandler<Object> handler = new ProxyHandler<>(instance, type);
            boolean proxy = false;
            for (InjectorProperty<?> property : properties) {
                if (property instanceof ProxyProperty) {
                    //noinspection unchecked
                    handler.delegate((ProxyProperty<Object, ?>) property);
                    proxy = true;
                }
            }
            if (proxy) {
                try {
                    return handler.proxy();
                }
                catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                    Except.handle(e);
                }
            }
            return instance;
        }));
    }
}