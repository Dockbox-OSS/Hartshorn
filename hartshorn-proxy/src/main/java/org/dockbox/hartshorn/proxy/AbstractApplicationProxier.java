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

import org.dockbox.hartshorn.proxy.lookup.HartshornProxyLookup;
import org.dockbox.hartshorn.proxy.lookup.NativeProxyLookup;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.ProxyLookup;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractApplicationProxier implements ApplicationProxier {

    private final Set<ProxyLookup> proxyLookups = ConcurrentHashMap.newKeySet();
    private final Introspector introspector;

    protected AbstractApplicationProxier(final Introspector introspector) {
        this.introspector = introspector;
        this.registerProxyLookup(new NativeProxyLookup());
        this.registerProxyLookup(new HartshornProxyLookup());
    }

    @Override
    public Introspector introspector() {
        return this.introspector;
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
    public <D, T extends D> Option<D> delegate(final Class<D> type, final T instance) {
        if (instance instanceof Proxy) {
            final Proxy<T> proxy = TypeUtils.adjustWildcards(instance, Proxy.class);
            final ProxyManager<?> manager = proxy.manager();
            return manager.advisor()
                    .resolver()
                    .type(type)
                    .delegate();
        }
        return Option.empty();
    }

    @Override
    public <T> Option<Class<T>> unproxy(final T instance) {
        for (final ProxyLookup lookup : this.proxyLookups) {
            if (lookup.isProxy(instance)) {
                final Option<Class<T>> unproxied = lookup.unproxy(instance);
                if (unproxied.present()) return unproxied;
            }
        }
        return Option.empty();
    }

    @Override
    public boolean isProxy(final Object instance) {
        return this.proxyLookups.stream().anyMatch(lookup -> lookup.isProxy(instance));
    }

    @Override
    public boolean isProxy(final Class<?> candidate) {
        return this.proxyLookups.stream().anyMatch(lookup -> lookup.isProxy(candidate));
    }

    public final void registerProxyLookup(final ProxyLookup proxyLookup) {
        this.proxyLookups.add(proxyLookup);
    }
}
