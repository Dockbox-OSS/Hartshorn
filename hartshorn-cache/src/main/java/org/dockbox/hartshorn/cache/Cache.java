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

import org.dockbox.hartshorn.core.domain.Exceptional;

/**
 * Represents a collection of entries of type <code>T</code>.
 *
 * @param <T> The type of object stored in the cache.
 */
public interface Cache<T> {

    /**
     * Provides the stored values of the cache. If the cache is not
     * populated, this will return {@link Exceptional#empty()}.
     *
     * @return The content of the cache, or {@link Exceptional#empty()}
     */
    Exceptional<T> get();

    /**
     * Populates the cache with the given content. If the cache is
     * already populated the new content is rejected.
     *
     * @param content The content to populate the cache with.
     *
     * @throws IllegalStateException When the cache is already populated.
     */
    void populate(T content);

    /**
     * Updates the cache by adding the provided object to the cache. If
     * the cache has not been populated, this will initialize an empty
     * cache before adding the object.
     *
     * @param object The object to add.
     */
    void update(T object);

    /**
     * Evicts the cache, removing all content.
     */
    void evict();
}
