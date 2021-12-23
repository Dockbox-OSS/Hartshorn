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

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.MethodProxyContextImpl;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ProxyMethodBindingException;
import org.dockbox.hartshorn.core.proxy.MethodProxyContext;
import org.dockbox.hartshorn.core.proxy.ProxyFunction;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.annotation.Annotation;
import java.util.Collection;

public abstract class ServiceMethodPostProcessor<A extends Annotation> extends ServicePostProcessor<A> {

    @Override
    public <T> T process(final ApplicationContext context, final TypeContext<T> type, @Nullable final T instance) {
        final Collection<MethodContext<?, T>> methods = this.modifiableMethods(type);

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

        // TODO: Create a primary post processor which creates the proxy (when allowed), and only modify the existing proxy here.
        //  This should also add a precondition to check if the incoming instance is a proxy. If it is, we can modify it. If it is not,
        //  we can ignore the component and directly return the instance (yield a log message?).
        return Exceptional.of(() -> {
            if (context.environment().manager().isProxy(instance)) return instance;
            return handler.proxy(context, instance);
        }).or(instance);
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
