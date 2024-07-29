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

package org.dockbox.hartshorn.proxy.lookup;

import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.ProxyIntrospector;
import org.dockbox.hartshorn.util.introspect.ProxyLookup;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A {@link ProxyLookup} implementation for Hartshorn's own proxy implementation. This implementation
 * supports proxies that are created through a {@link ProxyFactory}, or otherwise implement {@link Proxy}
 * directly.
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public class HartshornProxyLookup implements ProxyLookup {

    @Override
    public <T> Option<Class<T>> unproxy(T instance) {
        if (instance instanceof Proxy<?> proxy) {
            Class<T> unproxied = TypeUtils.unchecked(proxy.manager().targetClass(), Class.class);
            return Option.of(unproxied);
        }
        return Option.empty();
    }

    @Override
    public boolean isProxy(Object instance) {
        return instance != null && (instance instanceof Proxy || this.isProxy(instance.getClass()));
    }

    @Override
    public boolean isProxy(Class<?> candidate) {
        return Proxy.class.isAssignableFrom(candidate) && !Proxy.class.equals(candidate);
    }

    @Override
    public <T> Option<ProxyIntrospector<T>> introspector(T instance) {
        if (instance instanceof Proxy<?> proxy) {
            ProxyManager<?> manager = proxy.manager();
            if (manager.proxy() == instance) {
                ProxyManager<T> adjustedManager = TypeUtils.unchecked(manager, ProxyManager.class);
                return Option.of(adjustedManager);
            }
        }
        return Option.empty();
    }
}
