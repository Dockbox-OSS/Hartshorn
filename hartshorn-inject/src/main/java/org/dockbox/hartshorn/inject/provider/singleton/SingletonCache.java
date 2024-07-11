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

package org.dockbox.hartshorn.inject.provider.singleton;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.util.IllegalModificationException;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A singleton cache is used to store singleton instances of components. This is used to
 * prevent the creation of multiple instances of the same component, and improve tracking
 * of the components that are currently active.
 *
 * @since 0.4.11
 *
 * @author Guus Lieben
 */
public interface SingletonCache {

    /**
     * Locks the given key in the cache. This means that the instance stored for the given key
     * cannot be replaced in the cache.
     *
     * @param key The key to lock.
     */
    void lock(ComponentKey<?> key);

    /**
     * Stores the given instance in the cache, using the given key. If an instance is already
     * stored for the given key, the implementation may decide whether to replace the existing
     * instance, or to throw an exception.
     *
     * @param key The key to store the instance under.
     * @param instance The instance to store.
     * @param <T> The type of the instance.
     *
     * @throws IllegalModificationException If an instance is already stored for the given key.
     */
    <T> void put(ComponentKey<T> key, T instance) throws IllegalModificationException;

    /**
     * Returns the instance stored in the cache for the given key. If no instance is stored
     * for the given key, an empty {@link Option} is returned.
     *
     * @param key The key to retrieve the instance for.
     * @return The instance stored in the cache for the given key, or an empty {@link Option}
     * @param <T> The type of the instance.
     */
    <T> Option<T> get(ComponentKey<T> key);

    /**
     * Returns {@code true} if an instance is stored in the cache for the given key.
     *
     * @param key The key to check.
     * @return {@code true} if an instance is stored in the cache for the given key.
     * @param <T> The type of the instance.
     */
    <T> boolean contains(ComponentKey<T> key);
}
