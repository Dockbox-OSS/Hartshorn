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

import org.dockbox.hartshorn.proxy.lookup.StateAwareProxyFactory;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.ProxyLookup;
import org.dockbox.hartshorn.util.option.Option;

/**
 * The {@link ProxyOrchestrator} is responsible for creating proxies of components. It acts as middleware
 * between the application and the lower level proxying library, allowing for easy replacement of the
 * proxying library.
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public interface ProxyOrchestrator extends ProxyLookup {

    /**
     * Gets the real type of the given proxy instance. If the given instance is not a proxy, the returned
     * type is the same as the given type. This method is used to determine the type of a proxy, without
     * having to unproxy it manually.
     *
     * @param instance The instance to get the type of.
     * @param <T> The type of the instance.
     * @return The type of the instance.
     * @see ProxyLookup#unproxy(Object)
     */
    <T> Option<Class<T>> real(T instance);

    /**
     * Gets the {@link ProxyManager} for the given instance. If the given instance is not a proxy, an
     * empty {@link Option} is returned.
     *
     * @param instance The instance to get the manager for.
     * @return The manager of the instance, if it is a proxy.
     * @param <T> The type of the instance.
     */
    <T> Option<ProxyManager<T>> manager(T instance);

    /**
     * Gets the delegate for the given type for the given proxy instance. If the given instance is not a proxy, an
     * empty {@link Option} is returned.
     *
     * @param type The type to get the delegate for.
     * @param instance The instance to get the delegate for.
     * @return The delegate of the instance, if it is a proxy.
     * @param <D> The type of the delegate.
     * @param <T> The type of the instance.
     */
    <D, T extends D> Option<D> delegate(Class<D> type, T instance);

    /**
     * Creates a new proxy factory for the given type. The returned factory may be initialized with a minimal
     * set of configuration, but is not guaranteed to be fully configured. The factory is guaranteed to be
     * configured to create proxies for the given type.
     *
     * @param type The type to create a factory for.
     * @return A factory for the given type.
     * @param <T> The type of the factory.
     */
    <T> StateAwareProxyFactory<T> factory(Class<T> type);

    /**
     * Gets the {@link Introspector} that is used by this orchestrator.
     *
     * @return The introspector.
     */
    Introspector introspector();
}
