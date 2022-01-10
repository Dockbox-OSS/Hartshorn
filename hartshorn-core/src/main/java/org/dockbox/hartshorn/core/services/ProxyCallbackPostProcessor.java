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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.proxy.ProxyMethodCallback;
import org.dockbox.hartshorn.core.proxy.ProxyCallback;
import org.dockbox.hartshorn.core.proxy.ProxyMethodCallbackImpl;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;
import org.dockbox.hartshorn.core.proxy.CallbackPhase;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.stream.Collectors;

public abstract class ProxyCallbackPostProcessor<A extends Annotation> extends FunctionalComponentPostProcessor<A>{

    public abstract CallbackPhase phase();

    public abstract <T> boolean preconditions(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance);

    protected <T> Collection<MethodContext<?, T>> modifiableMethods(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        return key.type().methods().stream()
                .filter(method -> this.preconditions(context, method, key, instance))
                .collect(Collectors.toList());
    }

    @Override
    public <T> T process(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        final TypeContext<T> type = key.type();
        final Collection<MethodContext<?, T>> methods = this.modifiableMethods(context, key, instance);

        // Will reuse existing handler of proxy
        final ProxyHandler<T> handler = context.environment().manager().handler(type, instance);
        final CallbackPhase phase = this.phase();

        for (final MethodContext<?, T> method : methods) {
            final ProxyCallback<T> callback = this.callback(context, method, key, instance);
            final ProxyMethodCallback<T> methodCallback = new ProxyMethodCallbackImpl<>(phase, callback);
            handler.callback(method, methodCallback);
        }

        return instance;
    }

    public abstract <T> ProxyCallback<T> callback(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance);
}
