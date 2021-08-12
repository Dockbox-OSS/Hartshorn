/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.cache;

import org.dockbox.hartshorn.api.domain.Exceptional;

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
     * @param cache
     *         The cache ID
     * @param <T>
     *         The type of objects stored by the cache
     *
     * @return The cache, or {@link Exceptional#empty()}
     */
    <T> Exceptional<Cache<T>> get(String cache);

    /**
     * Updates the {@link Cache} associated with the given <code>cache</code>
     * ID, if it exists.
     *
     * @param cache
     *         The cache ID
     * @param object
     *         The object to update the cache with
     * @param <T>
     *         The type of the object
     */
    <T> void update(String cache, T object);

    /**
     * Evicts the {@link Cache} associated with the given <code>cache</code>
     * ID, if it exists.
     *
     * @param cache
     *         The cache ID
     */
    void evict(String cache);

    /**
     * Gets the {@link Cache} associated with the given <code>cache</code>
     * ID, if it exists. If the cache does not exist, a new cache is created
     * with the given {@link Expiration}. If the cache previously existed,
     * the expiration is not modified.
     *
     * @param name
     *         The cache ID
     * @param expiration
     *         The expiration of the cache
     * @param <T>
     *         The type of object stored in the cache
     *
     * @return The existing or created {@link Cache}
     */
    <T> Cache<T> getOrCreate(String name, Expiration expiration);
}
