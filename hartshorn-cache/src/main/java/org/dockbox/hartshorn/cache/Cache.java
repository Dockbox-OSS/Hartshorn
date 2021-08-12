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

import java.util.Collection;

/**
 * Represents a collection of entries of type <code>T</code>.
 *
 * @param <T>
 *         The type of object stored in the cache.
 */
public interface Cache<T> {

    /**
     * Provides the stored values of the cache. If the cache is not
     * populated, this will return {@link Exceptional#empty()}.
     *
     * @return The content of the cache, or {@link Exceptional#empty()}
     */
    Exceptional<Collection<T>> get();

    /**
     * Populates the cache with the given content. If the cache is
     * already populated the new content is rejected.
     *
     * @param content
     *         The content to populate the cache with.
     *
     * @throws IllegalStateException
     *         When the cache is already populated.
     */
    void populate(Collection<T> content);

    /**
     * Updates the cache by adding the provided object to the cache. If
     * the cache has not been populated, this will initialize an empty
     * cache before adding the object.
     *
     * @param object
     *         The object to add.
     */
    void update(T object);

    /**
     * Evicts the cache, removing all content.
     */
    void evict();

}
