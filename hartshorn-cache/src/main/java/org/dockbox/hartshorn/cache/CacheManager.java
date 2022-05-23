/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.cache;

import org.dockbox.hartshorn.util.Result;

import java.util.List;

/**
 * Manager type responsible for storing and providing {@link Cache caches}. Caches can be
 * created, updated, and evicted through the manager.
 */
public interface CacheManager {

    /**
     * Gets a list of all currently known {@link Cache caches} managed
     * by this manager.
     *
     * @return all caches, or an empty list.
     */
    List<Cache<?>> caches();

    /**
     * Gets the {@link Cache} associated with the given <code>cache</code>
     * ID, if it exists.
     *
     * @param cache The cache ID
     * @param <T> The type of objects stored by the cache
     *
     * @return The cache, or {@link Result#empty()}
     */
    <T> Result<Cache<T>> get(String cache);

    /**
     * Updates the {@link Cache} associated with the given <code>cache</code>
     * ID, if it exists.
     *
     * @param cache The cache ID
     * @param object The object to update the cache with
     * @param <T> The type of the object
     */
    <T> void update(String cache, T object);

    /**
     * Evicts the {@link Cache} associated with the given <code>cache</code>
     * ID, if it exists.
     *
     * @param cache The cache ID
     */
    void evict(String cache);

    /**
     * Gets the {@link Cache} associated with the given <code>cache</code>
     * ID, if it exists. If the cache does not exist, a new cache is created
     * with the given {@link Expiration}. If the cache previously existed,
     * the expiration is not modified.
     *
     * @param name The cache ID
     * @param expiration The expiration of the cache
     * @param <T> The type of object stored in the cache
     *
     * @return The existing or created {@link Cache}
     */
    <T> Cache<T> getOrCreate(String name, Expiration expiration);
}
