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

package org.dockbox.hartshorn.core.proxy.javassist;

import org.dockbox.hartshorn.core.AnnotationHelper.AnnotationInvocationHandler;
import org.dockbox.hartshorn.core.boot.HartshornApplicationProxier;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.BackingImplementationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.proxy.JavaInterfaceProxyHandler;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

/**
 * @see HartshornApplicationProxier
 */
public class JavassistProxyUtil {

    private static final TypeContext<?> HIBERNATE_PROXY = TypeContext.lookup("org.hibernate.proxy.HibernateProxy");

    public static <T> ProxyHandler<T> handler(final TypeContext<T> context, final T instance) {
        return handler(context.type(), instance);
    }

    static <T> ProxyHandler<T> handler(final Class<T> type, final T instance) {
        final Exceptional<ProxyHandler<T>> handler = handler(instance);
        return handler.orElse(() -> new JavassistProxyHandler<>(instance, type)).get();
    }

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
                if (invocationHandler instanceof JavaInterfaceProxyHandler proxyInterfaceHandler) {
                    return Exceptional.of(proxyInterfaceHandler.handler());
                }
                else if (invocationHandler instanceof AnnotationInvocationHandler annotationInvocationHandler) {
                    return Exceptional.of(() -> new JavassistProxyHandler<>((T) annotationInvocationHandler.annotation()));
                }
                else if (instance instanceof Annotation annotation) {
                    return Exceptional.of(() -> new JavassistProxyHandler<>(instance, (Class<T>) annotation.annotationType()));
                }
            }
        }
        return Exceptional.empty();
    }

    public static <T, P extends T> Exceptional<T> delegator(final ApplicationContext context, final TypeContext<T> type, final P proxied) {
        return JavassistProxyUtil.handler(proxied).flatMap(handler -> delegator(context, type, handler));
    }

    public static <T, P extends T> Exceptional<T> delegator(final ApplicationContext context, final TypeContext<T> type, final ProxyHandler<P> handler) {
        return handler.first(context, BackingImplementationContext.class).flatMap(backingContext -> backingContext.get(type.type()));
    }

    public static boolean isProxy(final Class<?> type) {
        if (ProxyFactory.isProxyClass(type) || Proxy.isProxyClass(type)) return true;
        if (!HIBERNATE_PROXY.isVoid()) HIBERNATE_PROXY.type().isAssignableFrom(type);
        return false;
    }
}
