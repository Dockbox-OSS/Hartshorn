/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

/**
 * The default implementation of the {@link ApplicationProxier} interface. This implementation uses the JavaAssist
 * library to create and manage proxies.
 *
 * <p>To support unproxying external proxy objects, this implementation uses {@link ProxyLookup}s to lookup the
 * original object. By default, this implementation uses {@link NativeProxyLookup} and {@link JavassistProxyLookup}.
 *
 * @author Guus Lieben
 * @since 21.9
 * @see ProxyLookup
 */
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
        return Exceptional.of(() -> this.handler(type, instance).proxy(instance));
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
                    return Exceptional.of(() -> new JavassistProxyHandler<>(this.applicationManager().applicationContext(), (T) annotationInvocationHandler.annotation()));
                }
                else if (instance instanceof Annotation annotation) {
                    return Exceptional.of(() -> new JavassistProxyHandler<>(this.applicationManager().applicationContext(), instance, (Class<T>) annotation.annotationType()));
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
        return handler.orElse(() -> new JavassistProxyHandler<>(this.applicationManager().applicationContext(), instance, type)).get();
    }

    public void registerProxyLookup(final ProxyLookup lookup) {
        this.proxyLookups.add(lookup);
    }
}
