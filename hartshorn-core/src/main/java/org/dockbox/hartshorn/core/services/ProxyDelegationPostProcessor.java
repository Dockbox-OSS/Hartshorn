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
import org.dockbox.hartshorn.core.context.BackingImplementationContext;
import org.dockbox.hartshorn.core.context.MethodProxyContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.proxy.ProxyFunction;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;

import java.lang.annotation.Annotation;
import java.util.Collection;

public abstract class ProxyDelegationPostProcessor<P, A extends Annotation> extends ServiceMethodInterceptorPostProcessor<A> {

    protected abstract Class<P> parentTarget();

    @Override
    public <T> boolean modifies(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        return key.type().childOf(this.parentTarget());
    }

    @Override
    protected <T> Collection<MethodContext<?, T>> modifiableMethods(final TypeContext<T> type) {
        return type.methods().stream().filter(method -> method.parent().is(this.parentTarget())).toList();
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        return methodContext.method().parent().is(this.parentTarget());
    }

    @Override
    public <T, R> ProxyFunction<T, R> process(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        final TypeContext<P> parentContext = TypeContext.of(this.parentTarget());
        final MethodContext<?, T> method = methodContext.method();
        final Exceptional<MethodContext<?, P>> parentMethod = parentContext.method(method.name(), method.parameterTypes());
        final ProxyHandler<P> handler = (ProxyHandler<P>) methodContext.handler();

        final BackingImplementationContext backing = handler.first(context, BackingImplementationContext.class).get();
        final P concrete = backing.computeIfAbsent(this.parentTarget(), target -> this.concreteDelegator(context, handler, (TypeContext<? extends P>) methodContext.type()));

        if (parentMethod.present()) {
            final MethodContext<?, P> parent = parentMethod.get();
            final R defaultValue = (R) parent.returnType().defaultOrNull();
            return (instance, args, proxyContext) -> {
                final R out = parent.invoke(concrete, args).rethrow().map((r -> (R) r)).orElse(() -> defaultValue).orNull();
                if (out == concrete) return (R) handler.proxyInstance().orNull();
                return out;
            };
        }
        else {
            context.log().error("Attempted to delegate method " + method.qualifiedName() + " but it was not find on the indicated parent " + parentContext.qualifiedName());
            return null;
        }
    }

    protected P concreteDelegator(final ApplicationContext context, final ProxyHandler<P> handler, final TypeContext<? extends P> parent) {
        return context.get(this.parentTarget());
    }
}
