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

import org.dockbox.hartshorn.util.option.Option;

/**
 * Represents a semi-persistent mapping between keys of type {@link K} and values
 * of type {@link V}. Cache values are manually added using either {@link #put(Object, Object)}
 * or {@link #putIfAbsent(Object, Object)}, but never by an implementation. Cache values
 * are retrieved using {@link #get(Object)}.
 *
 * <p>Caches can automatically expire values after a certain amount of time. This is
 * done by providing a {@link Expiration} to the implementation (either through the
 * constructor, or {@link CacheFactory#cache(Expiration)}). If the expiration is
 * {@link Expiration#never()}, then the cache will never expire values.
 *
 * <p>Caches can also be manually cleared using {@link #invalidate()}. Individual keys
 * can also be removed using {@link #invalidate(Object)}.
 *
 * <p>Implementations are typically thread-safe, though they are not required to be.
 *
 * @see CacheFactory
 * @see Expiration
 * @author Guus Lieben
 * @since 21.2
 */
public interface Cache<K, V> {

    /**
     * Provides the stored values of the cache. If the cache is not
     * populated, this will return {@link Option#empty()}.
     *
     * @return The content of the cache, or {@link Option#empty()}
     */
    Option<V> get(K key);

    /**
     * Returns {@code true} if the cache contains the given key. If the cache is not
     * populated, this will return {@code false}.
     *
     * @param key The key to check
     * @return {@code true} if the cache contains the given key
     */
    boolean contains(K key);

    /**
     * Puts the given value into the cache if the key is not already present. If the
     * key is already present, this will not change the value.
     *
     * @param key The key to store the value under
     * @param value The value to store
     */
    void putIfAbsent(K key, V value);

    /**
     * Puts the given value into the cache. If the key is already present, this will
     * change the value. If the cache is configured to expire values, this will reset
     * the expiration of the key to start counting from the moment the new value is
     * stored.
     *
     * @param key The key to store the value under
     * @param value The value to store
     */
    void put(K key, V value);

    /**
     * Removes the given key from the cache. If the key is not present, this will
     * not change the cache.
     *
     * @param key The key to remove
     */
    void invalidate(K key);

    /**
     * Removes all keys from the cache.
     */
    void invalidate();

    /**
     * Returns the number of keys in the cache.
     * @return The number of keys in the cache
     */
    int size();
}
