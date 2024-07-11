/*
 * Copyright 2019-2024 the original author or authors.
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

import org.dockbox.hartshorn.proxy.advice.ProxyAdvisor;
import org.dockbox.hartshorn.proxy.advice.TypeAdvisorResolver;
import org.dockbox.hartshorn.proxy.lookup.HartshornProxyLookup;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.NativeProxyLookup;
import org.dockbox.hartshorn.util.introspect.ProxyIntrospector;
import org.dockbox.hartshorn.util.introspect.ProxyLookup;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A base implementation of {@link ProxyOrchestrator} that provides a default set of {@link ProxyLookup}s, and
 * allows for registration of additional lookups. Lookup operations are performed with the assumption that Hartshorn's
 * own {@link Proxy} implementation is used.
 *
 * @since 0.4.12
 * @author Guus Lieben
 */
public abstract class AbstractProxyOrchestrator implements ProxyOrchestrator {

    private final Set<ProxyLookup> proxyLookups = ConcurrentHashMap.newKeySet();
    private final Introspector introspector;

    protected AbstractProxyOrchestrator(Introspector introspector) {
        this.introspector = introspector;
        this.registerProxyLookup(new NativeProxyLookup());
        this.registerProxyLookup(new HartshornProxyLookup());
    }

    @Override
    public Introspector introspector() {
        return this.introspector;
    }

    @Override
    public <T> Option<Class<T>> real(T instance) {
        return this.manager(instance).map(ProxyManager::targetClass);
    }

    @Override
    public <T> Option<ProxyManager<T>> manager(T instance) {
        if (instance instanceof Proxy<?> proxyInstance) {
            Proxy<T> proxy = TypeUtils.unchecked(proxyInstance, Proxy.class);
            return Option.of(proxy.manager());
        }
        return Option.empty();
    }

    @Override
    public <D, T extends D> Option<D> delegate(Class<D> type, T instance) {
        return this.manager(instance)
                .map(ProxyManager::advisor)
                .map(ProxyAdvisor::resolver)
                .map(resolver -> resolver.type(type))
                .flatMap(TypeAdvisorResolver::delegate);
    }

    @Override
    public <T> Option<Class<T>> unproxy(T instance) {
        for (ProxyLookup lookup : this.proxyLookups) {
            if (lookup.isProxy(instance)) {
                Option<Class<T>> unproxied = lookup.unproxy(instance);
                if (unproxied.present()) {
                    return unproxied;
                }
            }
        }
        return Option.empty();
    }

    @Override
    public boolean isProxy(Object instance) {
        return this.proxyLookups.stream().anyMatch(lookup -> lookup.isProxy(instance));
    }

    @Override
    public boolean isProxy(Class<?> candidate) {
        return this.proxyLookups.stream().anyMatch(lookup -> lookup.isProxy(candidate));
    }

    @Override
    public <T> Option<ProxyIntrospector<T>> introspector(T instance) {
        for (ProxyLookup lookup : this.proxyLookups) {
            if (lookup.isProxy(instance)) {
                Option<ProxyIntrospector<T>> introspector = lookup.introspector(instance);
                if (introspector.present()) {
                    return introspector;
                }
            }
        }
        return Option.empty();
    }

    /**
     * Registers a new {@link ProxyLookup} with this orchestrator. The lookup will be used as a candidate for all
     * future proxy operations.
     *
     * @param proxyLookup the lookup to register
     */
    public void registerProxyLookup(ProxyLookup proxyLookup) {
        this.proxyLookups.add(proxyLookup);
    }
}
