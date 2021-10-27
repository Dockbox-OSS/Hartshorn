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

package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.AnnotationHelper.AnnotationInvocationHandler;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.proxy.handle.ProxyHandler;
import org.dockbox.hartshorn.proxy.handle.ProxyInterfaceHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

public class ProxyUtil {

    public static <T> ProxyHandler<T> handler(final TypeContext<T> context, final T instance) {
        return handler(context.type(), instance);
    }

    public static <T> ProxyHandler<T> handler(final Class<T> type, final T instance) {
        final Exceptional<ProxyHandler<T>> handler = handler(instance);
        return handler.orElse(() -> new ProxyHandler<>(instance, type)).get();
    }

    @SuppressWarnings("unchecked")
    public static <T> Exceptional<ProxyHandler<T>> handler(final T instance) {
        if (instance != null) {
            if (ProxyFactory.isProxyClass(instance.getClass())) {
                final MethodHandler methodHandler = ProxyFactory.getHandler((javassist.util.proxy.Proxy) instance);
                if (methodHandler instanceof ProxyHandler proxyHandler) {
                    return Exceptional.of((ProxyHandler<T>) proxyHandler);
                }
            }
            else if (Proxy.isProxyClass(instance.getClass())) {
                final InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
                if (invocationHandler instanceof ProxyInterfaceHandler proxyInterfaceHandler) {
                    return Exceptional.of(proxyInterfaceHandler.handler());
                }
                else if (invocationHandler instanceof AnnotationInvocationHandler annotationInvocationHandler) {
                    return Exceptional.of(() -> new ProxyHandler<>((T) annotationInvocationHandler.annotation()));
                }
                else if (instance instanceof Annotation annotation) {
                    return Exceptional.of(() -> new ProxyHandler<>(instance, (Class<T>) annotation.annotationType()));
                }
            }
        }
        return Exceptional.empty();
    }

}
