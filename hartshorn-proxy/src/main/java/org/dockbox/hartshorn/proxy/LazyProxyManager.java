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

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.proxy.advice.RegistryProxyAdvisor;
import org.dockbox.hartshorn.proxy.advice.ProxyAdvisor;
import org.dockbox.hartshorn.proxy.advice.registry.StateAwareAdvisorRegistry;
import org.dockbox.hartshorn.util.IllegalModificationException;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.reflect.Method;

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
public class LazyProxyManager<T> extends DefaultContext implements ModifiableProxyManager<T> {

    private static final Method managerAccessor;

    static {
        try {
            managerAccessor = Proxy.class.getDeclaredMethod("manager");
        }
        catch (final NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    private final ApplicationProxier applicationProxier;
    private final RegistryProxyAdvisor<T> advisor;

    private Class<T> proxyClass;
    private final Class<T> targetClass;
    private T proxy;

    public LazyProxyManager(final DefaultProxyFactory<T> proxyFactory) {
        this(proxyFactory.applicationProxier(), null, proxyFactory.type(), proxyFactory.advisors());
    }

    public LazyProxyManager(final ApplicationProxier proxier, final Class<T> proxyClass, final Class<T> targetClass,
                            final StateAwareAdvisorRegistry<T> advisors) {
        this.applicationProxier = proxier;

        if (applicationProxier.isProxy(targetClass)) {
            throw new IllegalArgumentException("Target class is already a proxy");
        }
        if (proxyClass != null && !applicationProxier.isProxy(proxyClass)) {
            throw new IllegalArgumentException("Proxy class is not a proxy");
        }

        this.proxyClass = proxyClass;
        this.targetClass = targetClass;

        advisors.method(managerAccessor).intercept(context -> this);
        this.advisor = new RegistryProxyAdvisor<>(proxier, advisors);
    }

    public void proxy(final T proxy) {
        if (this.proxy != null) {
            throw new IllegalModificationException("Proxy instance already set.");
        }
        if (!this.applicationProxier.isProxy(proxy)) {
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
        return this.advisor().resolver().type().delegate();
    }

    @Override
    public ApplicationProxier applicationProxier() {
        return this.applicationProxier;
    }

    @Override
    public ProxyAdvisor<T> advisor() {
        return advisor;
    }

    @Override
    public ModifiableProxyManager<T> delegate(final T delegate) {
        advisor.resolver().type().delegate(delegate);
        return this;
    }
}
