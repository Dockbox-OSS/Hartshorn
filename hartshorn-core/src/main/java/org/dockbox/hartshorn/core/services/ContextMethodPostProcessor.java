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

package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.inject.Provided;
import org.dockbox.hartshorn.core.annotations.activate.UseProxying;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.MethodProxyContext;
import org.dockbox.hartshorn.core.proxy.ProxyFunction;

@AutomaticActivation
public class ContextMethodPostProcessor extends ServiceAnnotatedMethodPostProcessor<Provided, UseProxying> {

    @Override
    public Class<UseProxying> activator() {
        return UseProxying.class;
    }

    @Override
    public <T, R> ProxyFunction<T, R> process(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        return (instance, args, proxyContext) -> {
            final Provided annotation = methodContext.annotation(Provided.class);
            final String name = annotation.value();

            Key<?> key = Key.of(methodContext.method().returnType());
            if (!name.isEmpty()) key = key.named(name);
            return (R) context.get(key);
        };
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        return !methodContext.method().returnType().isVoid();
    }

    @Override
    public Class<Provided> annotation() {
        return Provided.class;
    }
}
