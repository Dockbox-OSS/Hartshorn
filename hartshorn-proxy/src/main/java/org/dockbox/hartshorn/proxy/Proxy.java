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

/**
 * The common parent of all proxies created by the {@link ProxyFactory}. This class
 * is used to provide a common interface for all proxies, as well as provide easy
 * access to the proxy's {@link ProxyManager}.
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface Proxy<T> {

    /**
     * Returns the {@link ProxyManager} that is responsible for this proxy.
     * @return the {@link ProxyManager} that is responsible for this proxy
     */
    ProxyManager<T> manager();
}
