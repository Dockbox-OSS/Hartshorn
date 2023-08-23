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

import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.proxy.advice.ProxyAdvisor;
import org.dockbox.hartshorn.proxy.advice.registry.StateAwareAdvisorRegistry;
import org.dockbox.hartshorn.proxy.advice.registry.StateAwareTypeAdvisorRegistryStep;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A proxy manager is responsible for managing the lifecycle of a single proxy object. How the proxy is created is
 * determined by the {@link ProxyFactory} that is used to create the proxy. The proxy manager is responsible for
 * providing the proxy with the necessary information to function.
 *
 * <p>The necessary information is provided by the {@link ProxyFactory} and the {@link ProxyManager} is responsible
 * for providing it to the proxy after it has been created. As a legal side effect of this, the proxy manager is
 * capable of exposing the active delegates, interceptors, and proxy context.
 *
 * @param <T> the type of the proxy
 * @author Guus Lieben
 * @since 0.4.10
 */
public interface ProxyManager<T> extends Context {

    /**
     * Returns the {@link ProxyAdvisor} that is responsible for this proxy. The advisor is responsible for providing
     * the proxy with the necessary advisors to function.
     *
     * @return the {@link ProxyAdvisor} that is responsible for this proxy
     */
    ProxyAdvisor<T> advisor();

    /**
     * Returns the original type of the proxy. This is the type of the object that is proxied, but is not the proxied
     * type itself.
     *
     * @return the original type of the proxy
     */
    Class<T> targetClass();

    /**
     * Gets the proxied type of the proxy. This is the type of the object that is proxied, but is not the original
     * type of the proxy.
     *
     * @return the proxied type of the proxy
     */
    Class<T> proxyClass();

    /**
     * Returns the proxy instance managed by this manager.
     *
     * @return the proxy instance managed by this manager
     */
    T proxy();

    /**
     * Returns the original instance delegate of the proxy.
     * @return the original instance delegate of the proxy
     * @see StateAwareTypeAdvisorRegistryStep#delegate()
     * @see StateAwareAdvisorRegistry#type()
     */
    Option<T> delegate();

    /**
     * Returns the {@link ApplicationProxier} that is responsible for managing the lifecycle of proxies in the current
     * application, including the proxy managed by this manager.
     *
     * @return the {@link ApplicationProxier}
     */
    ApplicationProxier applicationProxier();
}
