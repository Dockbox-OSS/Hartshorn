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
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.di.services.ServiceModifier;
import org.dockbox.hartshorn.proxy.ProxyAttribute;
import org.dockbox.hartshorn.proxy.ProxyUtil;
import org.dockbox.hartshorn.proxy.exception.ProxyMethodBindingException;
import org.dockbox.hartshorn.proxy.handle.ProxyFunction;
import org.dockbox.hartshorn.proxy.handle.ProxyHandler;
import org.dockbox.hartshorn.util.Reflect;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

public abstract class ServiceAnnotatedMethodModifier<M extends Annotation, A extends Annotation> extends ServiceModifier<A> {

    @Override
    protected <T> boolean modifies(Class<T> type, @Nullable T instance, Attribute<?>... properties) {
        return !Reflect.methods(type, this.annotation()).isEmpty();
    }

    public abstract Class<M> annotation();

    @Override
    public <T> T process(ApplicationContext context, Class<T> type, @Nullable T instance, Attribute<?>... properties) {
        final Collection<Method> methods = Reflect.methods(type, this.annotation());

        ProxyHandler<T> handler = ProxyUtil.handler(type, instance);

        for (Method method : methods) {
            MethodProxyContext<T> ctx = new SimpleMethodProxyContext<>(instance, type, method, properties);

            if (this.preconditions(context, ctx)) {
                final ProxyFunction<T, Object> function = this.process(context, ctx);
                if (function != null) {
                    ProxyAttribute<T, ?> property = ProxyAttribute.of(type, method, function);
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
