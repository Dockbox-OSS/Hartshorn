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

package org.dockbox.selene.proxy.service;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.di.properties.BindingMetaProperty;
import org.dockbox.selene.proxy.annotations.Provided;
import org.dockbox.selene.proxy.annotations.UseProxying;
import org.dockbox.selene.proxy.handle.ProxyFunction;
import org.dockbox.selene.util.Reflect;

public class ContextMethodModifier extends ServiceAnnotatedMethodModifier<Provided, UseProxying> {
    @Override
    public Class<UseProxying> activator() {
        return UseProxying.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, R> ProxyFunction<T, R> process(ApplicationContext context, MethodProxyContext<T> methodContext) {
        return (instance, args, proxyContext) -> {
            final Provided annotation = methodContext.getAnnotation(Provided.class);
            final String name = annotation.value();
            if ("".equals(name)) {
                return (R) Selene.context().get(methodContext.getReturnType());
            } else {
                return (R) Selene.context().get(methodContext.getReturnType(), BindingMetaProperty.of(name));
            }
        };
    }

    @Override
    public <T> boolean preconditions(ApplicationContext context, MethodProxyContext<T> methodContext) {
        return Reflect.isNotVoid(methodContext.getReturnType());
    }

    @Override
    public Class<Provided> annotation() {
        return Provided.class;
    }
}
