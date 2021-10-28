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

package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.boot.annotations.PostBootstrap;
import org.dockbox.hartshorn.core.boot.annotations.UseBootstrap;
import org.dockbox.hartshorn.core.exceptions.Except;
import org.dockbox.hartshorn.core.InjectionPoint;
import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.properties.Attribute;

import java.lang.reflect.InvocationTargetException;

@Service(activators = UseBootstrap.class)
public class ProxyInjectionService {

    @PostBootstrap
    public void prepareInjectionPoints(final ApplicationContext context) {
        ProxyableBootstrap.boostrapDelegates(context);
        context.add(InjectionPoint.of(TypeContext.of(Object.class), (instance, type, properties) -> {
            final ProxyHandler<Object> handler = new ProxyHandler<>(instance, type);
            boolean proxy = false;
            for (final Attribute<?> property : properties) {
                if (property instanceof ProxyAttribute) {
                    //noinspection unchecked
                    handler.delegate((ProxyAttribute<Object, ?>) property);
                    proxy = true;
                }
            }
            if (proxy) {
                try {
                    return handler.proxy();
                }
                catch (final InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                    Except.handle(e);
                }
            }
            return instance;
        }));
    }
}
