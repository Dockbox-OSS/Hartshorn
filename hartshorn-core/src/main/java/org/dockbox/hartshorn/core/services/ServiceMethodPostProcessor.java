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
import org.dockbox.hartshorn.core.context.MethodProxyContextImpl;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.exceptions.ProxyMethodBindingException;
import org.dockbox.hartshorn.core.proxy.MethodProxyContext;
import org.dockbox.hartshorn.core.proxy.ProxyFunction;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;

import java.lang.annotation.Annotation;
import java.util.Collection;

public abstract class ServiceMethodPostProcessor<A extends Annotation> extends FunctionalComponentPostProcessor<A> {

    @Override
    public <T> T process(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        final TypeContext<T> type = key.type();
        final Collection<MethodContext<?, T>> methods = this.modifiableMethods(type);

        // Will reuse existing handler of proxy
        final ProxyHandler<T> handler = context.environment().manager().handler(type, instance);

        for (final MethodContext<?, T> method : methods) {
            final org.dockbox.hartshorn.core.context.MethodProxyContext ctx = new MethodProxyContextImpl<>(context, instance, type, method, handler);

            if (this.preconditions(context, ctx)) {
                final ProxyFunction<T, Object> function = this.process(context, ctx);
                if (function != null) {
                    final MethodProxyContext<T, ?> property = MethodProxyContext.of(type, method, function);
                    handler.delegate(property);
                }
            }
            else {
                if (this.failOnPrecondition()) {
                    throw new ProxyMethodBindingException(method);
                }
            }
        }

        return instance;
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        return super.preconditions(context, key, instance) && context.environment().manager().isProxy(instance);
    }

    protected abstract <T> Collection<MethodContext<?, T>> modifiableMethods(TypeContext<T> type);

    public abstract <T> boolean preconditions(ApplicationContext context, org.dockbox.hartshorn.core.context.MethodProxyContext<T> methodContext);

    public abstract <T, R> ProxyFunction<T, R> process(ApplicationContext context, org.dockbox.hartshorn.core.context.MethodProxyContext<T> methodContext);

    public boolean failOnPrecondition() {
        return true;
    }

    @Override
    public ProcessingOrder order() {
        return ProcessingOrder.EARLY;
    }
}
