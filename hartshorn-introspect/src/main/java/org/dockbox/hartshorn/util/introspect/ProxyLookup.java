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
 * Proxy lookups are used to obtain the real type of a proxy, and identify potential proxy types and instances.
 * Direct use of an implementation of this interface is not guaranteed to be accurate, and should be used with
 * caution.
 *
 * @author Guus Lieben
 * @since 0.4.10
 */
public interface ProxyLookup {

    /**
     * Get the real type of the given proxy. If the given instance is not a proxy, the given type is returned.
     *
     * @param instance the instance to get the real type of
     * @param <T> the type of the instance
     *
     * @return the real type of the given instance
     */
    <T> Option<Class<T>> unproxy(T instance);

    /**
     * Indicates whether the given instance is a proxy.
     *
     * @param instance the instance to check
     * @return true if the given instance is a proxy, false otherwise
     */
    boolean isProxy(Object instance);

    /**
     * Indicates whether the given type is a proxy type.
     *
     * @param candidate the type to check
     * @return true if the given type is a proxy type, false otherwise
     */
    boolean isProxy(Class<?> candidate);

    /**
     * Returns a proxy introspector for the given instance. If the given instance is not a proxy, or is not
     * supported by this lookup implementation, an empty {@link Option} is returned.
     *
     * @param instance the instance to introspect
     * @return a proxy introspector for the given instance
     * @param <T> the type of the instance
     */
    <T> Option<ProxyIntrospector<T>> introspector(T instance);

}
