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

package org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.util.option.Option;

/**
 * A proxy introspector is used to introspect a proxy instance. It provides access to the original type of
 * the proxy, the proxied type of the proxy, the proxy instance itself and the original instance delegate
 * of the proxy if it is present and accessible.
 *
 * @param <T> the type of the proxy
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ProxyIntrospector<T> {

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
     *
     * @return the original instance delegate of the proxy
     */
    Option<T> delegate();
}
