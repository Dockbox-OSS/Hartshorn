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

package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.AnnotationHelper.AnnotationInvocationHandler;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.context.BackingImplementationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.proxy.javassist.JavassistInterfaceHandler;
import org.dockbox.hartshorn.core.proxy.NativeProxyLookup;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;
import org.dockbox.hartshorn.core.proxy.ProxyLookup;
import org.dockbox.hartshorn.core.proxy.javassist.JavassistProxyHandler;
import org.dockbox.hartshorn.core.proxy.javassist.JavassistProxyLookup;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Set;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import lombok.Getter;

public class JavassistApplicationProxier implements ApplicationProxier, ApplicationManaged {

    @Getter
    private ApplicationManager applicationManager;
    private final Set<ProxyLookup> proxyLookups = HartshornUtils.emptyConcurrentSet();

    public JavassistApplicationProxier() {
        this.proxyLookups.add(new NativeProxyLookup());
        this.proxyLookups.add(new JavassistProxyLookup());
    }

    @Override
    public <T> Exceptional<T> proxy(final TypeContext<T> type) {
        return this.proxy(type, null);
    }

    @Override
    public <T> Exceptional<T> proxy(final TypeContext<T> type, final T instance) {
        return Exceptional.of(() -> this.handler(type, instance).proxy(this.applicationManager().applicationContext(), instance));
    }

    @Override
    public <T> Exceptional<TypeContext<T>> real(final T instance) {
        return Exceptional.of(TypeContext.of(this.unproxy(instance)));
    }

    @Override
    public <T, P extends T> Exceptional<T> delegator(final TypeContext<T> type, final P instance) {
        return this.handler(instance).flatMap(handler -> this.delegator(type, handler));
    }

    @Override
    public <T, P extends T> Exceptional<T> delegator(final TypeContext<T> type, final ProxyHandler<P> handler) {
        return handler.first(this.applicationManager().applicationContext(), BackingImplementationContext.class)
                .flatMap(backingContext -> backingContext.get(type.type()));
    }

    @Override
    public <T> ProxyHandler<T> handler(final TypeContext<T> type, final T instance) {
        return this.handler(type.type(), instance);
    }

    @Override
    public <T> Exceptional<ProxyHandler<T>> handler(final T instance) {
        if (instance != null) {
            if (ProxyFactory.isProxyClass(instance.getClass())) {
                final MethodHandler methodHandler = ProxyFactory.getHandler((javassist.util.proxy.Proxy) instance);
                if (methodHandler instanceof ProxyHandler proxyHandler) {
                    return Exceptional.of((ProxyHandler<T>) proxyHandler);
                }
            }
            else if (Proxy.isProxyClass(instance.getClass())) {

                final InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
                if (invocationHandler instanceof JavassistInterfaceHandler proxyInterfaceHandler) {
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

    @Override
    public void applicationManager(final ApplicationManager applicationManager) {
        if (this.applicationManager == null) this.applicationManager = applicationManager;
        else throw new IllegalArgumentException("Application manager has already been configured");
    }

    @Override
    public <T> Class<T> unproxy(final T instance) {
        for (final ProxyLookup lookup : this.proxyLookups) {
            if (lookup.isProxy(instance)) return lookup.unproxy(instance);
        }
        return instance != null ? (Class<T>) instance.getClass() : null;
    }

    @Override
    public boolean isProxy(final Object instance) {
        return this.proxyLookups.stream().anyMatch(lookup -> lookup.isProxy(instance));
    }

    @Override
    public boolean isProxy(final Class<?> candidate) {
        return this.proxyLookups.stream().anyMatch(lookup -> lookup.isProxy(candidate));
    }

    protected <T> ProxyHandler<T> handler(final Class<T> type, final T instance) {
        final Exceptional<ProxyHandler<T>> handler = this.handler(instance);
        return handler.orElse(() -> new JavassistProxyHandler<>(instance, type)).get();
    }

    public void registerProxyLookup(final ProxyLookup lookup) {
        this.proxyLookups.add(lookup);
    }
}
