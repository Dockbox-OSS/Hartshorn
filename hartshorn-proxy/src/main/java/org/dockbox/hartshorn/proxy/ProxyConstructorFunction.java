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

import org.dockbox.hartshorn.util.ApplicationException;

import java.lang.reflect.Constructor;

/**
 * A function that creates a proxy instance. This is used to allow for custom proxy implementations.
 *
 * @param <T> The type of the proxy
 *
 * @since 0.4.9
 *
 * @author Guus Lieben
 */
public interface ProxyConstructorFunction<T> {

    /**
     * Creates a new proxy instance. This will attempt to use the default constructor of the proxy type.
     *
     * @return The created proxy instance
     * @throws ApplicationException If the proxy instance could not be created
     */
    T create() throws ApplicationException;

    /**
     * Creates a new proxy instance. This will attempt to use the given constructor of the proxy type.
     *
     * @param constructor The constructor to use
     * @param args The arguments to pass to the constructor
     * @return The created proxy instance
     * @throws ApplicationException If the proxy instance could not be created
     */
    T create(Constructor<? extends T> constructor, Object[] args) throws ApplicationException;
}
