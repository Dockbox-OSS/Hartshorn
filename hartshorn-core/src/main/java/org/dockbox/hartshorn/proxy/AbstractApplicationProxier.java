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
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ApplicationManaged;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractApplicationProxier implements ApplicationProxier, ApplicationManaged {

    private ApplicationEnvironment environment;
    private final Set<ProxyLookup> proxyLookups = ConcurrentHashMap.newKeySet();

    public AbstractApplicationProxier() {
        this.registerProxyLookup(new NativeProxyLookup());
    }

    @Override
    public ApplicationEnvironment environment() {
        return this.environment;
    }

    @Override
    public void environment(final ApplicationEnvironment environment) {
        if (this.environment == null) this.environment = environment;
        else throw new IllegalModificationException("Application manager has already been configured");
    }

    @Override
    public <T> Option<Class<T>> real(final T instance) {
        if (instance instanceof Proxy) {
            final Proxy<T> proxy = TypeUtils.adjustWildcards(instance, Proxy.class);
            return Option.of(proxy.manager().targetClass());
        }
        return Option.empty();
    }

    @Override
    public <T> Option<ProxyManager<T>> manager(final T instance) {
        if (instance instanceof Proxy) {
            final Proxy<T> proxy = TypeUtils.adjustWildcards(instance, Proxy.class);
            return Option.of(proxy.manager());
        }
        return Option.empty();
    }

    @Override
    public <D, T extends D> Option<D> delegate(final TypeView<D> type, final T instance) {
        return this.delegate(type.type(), instance);
    }

    @Override
    public <D, T extends D> Option<D> delegate(final Class<D> type, final T instance) {
        if (instance instanceof Proxy) {
            final Proxy<T> proxy = TypeUtils.adjustWildcards(instance, Proxy.class);
            final ProxyManager<?> manager = proxy.manager();
            return manager.delegate(type);
        }
        return Option.empty();
    }

    @Override
    public <T> StateAwareProxyFactory<T, ?> factory(final TypeView<T> type) {
        return this.factory(type.type());
    }

    @Override
    public <T> Class<T> unproxy(final T instance) {
        for (final ProxyLookup lookup : this.proxyLookups) {
            if (lookup.isProxy(instance)) return lookup.unproxy(instance);
        }
        return instance != null ? TypeUtils.adjustWildcards(instance.getClass(), Class.class) : null;
    }

    @Override
    public boolean isProxy(final Object instance) {
        return this.proxyLookups.stream().anyMatch(lookup -> lookup.isProxy(instance));
    }

    @Override
    public boolean isProxy(final Class<?> candidate) {
        return this.proxyLookups.stream().anyMatch(lookup -> lookup.isProxy(candidate));
    }

    public void registerProxyLookup(final ProxyLookup proxyLookup) {
        this.proxyLookups.add(proxyLookup);
    }
}
