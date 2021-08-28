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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.di.services.ServiceModifier;
import org.dockbox.hartshorn.proxy.ProxyAttribute;
import org.dockbox.hartshorn.proxy.ProxyUtil;
import org.dockbox.hartshorn.proxy.exception.ProxyMethodBindingException;
import org.dockbox.hartshorn.proxy.handle.ProxyFunction;
import org.dockbox.hartshorn.proxy.handle.ProxyHandler;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Collection;

public abstract class ServiceAnnotatedMethodModifier<M extends Annotation, A extends Annotation> extends ServiceModifier<A> {

    @Override
    protected <T> boolean modifies(final ApplicationContext context, final TypeContext<T> type, @Nullable final T instance, final Attribute<?>... properties) {
        return !type.flatMethods(this.annotation()).isEmpty();
    }

    public abstract Class<M> annotation();

    @Override
    public <T> T process(final ApplicationContext context, final TypeContext<T> type, @Nullable final T instance, final Attribute<?>... properties) {
        final Collection<MethodContext<?, T>> methods = type.flatMethods(this.annotation());

        final ProxyHandler<T> handler = ProxyUtil.handler(type, instance);

        for (final MethodContext<?, T> method : methods) {
            final MethodProxyContext<T> ctx = new MethodProxyContextImpl<>(context, instance, type, method, properties);

            if (this.preconditions(context, ctx)) {
                final ProxyFunction<T, Object> function = this.process(context, ctx);
                if (function != null) {
                    final ProxyAttribute<T, ?> property = ProxyAttribute.of(type, method, function);
                    handler.delegate(property);
                }
            }
            else {
                if (this.failOnPrecondition()) {
                    throw new ProxyMethodBindingException(method);
                }
            }
        }
        return Exceptional.of(handler::proxy).or(instance);
    }

    public abstract <T> boolean preconditions(ApplicationContext context, MethodProxyContext<T> methodContext);

    public abstract <T, R> ProxyFunction<T, R> process(ApplicationContext context, MethodProxyContext<T> methodContext);

    public boolean failOnPrecondition() {
        return true;
    }
}
