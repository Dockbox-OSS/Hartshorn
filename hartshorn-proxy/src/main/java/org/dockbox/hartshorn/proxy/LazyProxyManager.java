/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.application.context.IllegalModificationException;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.util.collections.ConcurrentClassMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.ConcurrentSetMultiMap;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * A lazy-loading proxy manager. This implementation tracks the proxy's delegates and interceptors, and allows
 * the proxy to be set lazily. This is useful for when the manager is created in a context where the proxy is not
 * yet available, such as when the proxy requires modification to access its manager before being constructed. The
 * proxy is set lazily, and the manager is set when the proxy is created.
 *
 * <p>The manager will only allow the proxy to be set once, and will throw an exception if the proxy is set more
 * than once. This is to prevent the proxy from being set multiple times, which can cause unexpected behavior.
 *
 * @param <T> the type of the proxy
 * @author Guus Lieben
 * @since 22.2
 */
public class LazyProxyManager<T> extends DefaultApplicationAwareContext implements ProxyManager<T>, ModifiableProxyManager<T> {

    private static final Method managerAccessor;

    static {
        try {
            managerAccessor = Proxy.class.getDeclaredMethod("manager");
        }
        catch (final NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    private Class<T> proxyClass;
    private final Class<T> targetClass;
    private T proxy;

    private final Map<Method, ?> delegates;
    private final ConcurrentClassMap<Object> typeDelegates;
    private final Map<Method, MethodInterceptor<T, ?>> interceptors;
    private final MultiMap<Method, MethodWrapper<T>> wrappers;
    private final Supplier<MethodStub<T>> defaultStub;
    private T delegate;

    public LazyProxyManager(final DefaultProxyFactory<T> proxyFactory) {
        this(applicationContext, null, proxyFactory.type(), proxyFactory.typeDelegate(),
                proxyFactory.delegates(), proxyFactory.typeDelegates(),
                proxyFactory.interceptors(), proxyFactory.wrappers(),
                proxyFactory.defaultStub());
    }

    public LazyProxyManager(final Class<T> proxyClass, final Class<T> targetClass,
                            final T delegate, final Map<Method, ?> delegates, final ConcurrentClassMap<Object> typeDelegates,
                            final Map<Method, MethodInterceptor<T, ?>> interceptors, final MultiMap<Method, MethodWrapper<T>> wrappers,
                            final Supplier<MethodStub<T>> defaultStub) {
        super(applicationContext);
        // TODO: Check if the proxy class is a proxy
        if (applicationContext.environment().isProxy(targetClass)) {
            throw new IllegalArgumentException("Target class is already a proxy");
        }
        if (proxyClass != null && !applicationContext.environment().isProxy(proxyClass)) {
            throw new IllegalArgumentException("Proxy class is not a proxy");
        }

        this.proxyClass = proxyClass;
        this.targetClass = targetClass;
        this.delegate = delegate;

        this.delegates = new ConcurrentHashMap<>(delegates);
        this.typeDelegates = new ConcurrentClassMap<>(typeDelegates);
        this.interceptors = new HashMap<>(interceptors);
        this.wrappers = new ConcurrentSetMultiMap<>(wrappers);
        this.defaultStub = defaultStub;

        this.interceptors.put(managerAccessor, context -> this);
    }

    public void proxy(final T proxy) {
        if (this.proxy != null) {
            throw new IllegalModificationException("Proxy instance already set.");
        }
        if (!this.applicationContext().environment().isProxy(proxy)) {
            throw new IllegalArgumentException("Provided object is not a proxy");
        }
        this.proxy = proxy;
    }

    @Override
    public Class<T> targetClass() {
        return this.targetClass;
    }

    @Override
    public Class<T> proxyClass() {
        if (this.proxyClass == null) {
            this.proxyClass = (Class<T>) this.proxy().getClass();
        }
        return this.proxyClass;
    }

    @Override
    public T proxy() {
        if (this.proxy == null) {
            throw new IllegalStateException("Proxy instance has not been set");
        }
        return this.proxy;
    }

    @Override
    public Option<T> delegate() {
        return Option.of(this.delegate);
    }

    @Override
    public Option<T> delegate(final Method method) {
        return Option.of(this.delegates).map(map -> map.get(method)).map(delegate -> (T) delegate);
    }

    @Override
    public <S> Option<S> delegate(final Class<S> type) {
        return Option.of(this.typeDelegates).map(map -> map.get(type)).map(type::cast);
    }

    @Override
    public Option<MethodInterceptor<T, ?>> interceptor(final Method method) {
        return Option.of(this.interceptors).map(map -> map.get(method));
    }

    @Override
    public Set<MethodWrapper<T>> wrappers(final Method method) {
        return Set.copyOf(this.wrappers.get(method));
    }

    @Override
    public MethodStub<T> stub() {
        return this.defaultStub.get();
    }

    @Override
    public LazyProxyManager<T> delegate(final T delegate) {
        this.delegate = delegate;
        return this;
    }
}
