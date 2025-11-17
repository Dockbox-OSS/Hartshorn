/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.util.collections;

import java.util.Collection;
import java.util.NavigableMap;

/**
 * A navigable variant of the {@link MultiMap} interface, providing additional methods to access
 * entries based on their order. This is the {@link MultiMap} equivalent of {@link NavigableMap}.
 *
 * @param <K> the key type
 * @param <V> the value type
 *
 * @see MultiMap
 * @see NavigableMap
 *
 * @since 0.7.0
 */
public interface NavigableMultiMap<K, V> extends MultiMap<K, V> {

    /**
     * Returns the first entry in the map, or an empty collection if the map is empty.
     *
     * @return the first entry in the map, or an empty collection if the map is empty
     */
    Collection<V> firstEntry();

    /**
     * Returns the last entry in the map, or an empty collection if the map is empty.
     *
     * @return the last entry in the map, or an empty collection if the map is empty
     */
    Collection<V> lastEntry();

    /**
     * Returns the collection associated with the greatest key less than or equal to the given key,
     * or an empty collection if there is no such key.
     *
     * @param key the key
     * @return the collection associated with the greatest key less than or equal to the given key
     */
    Collection<V> floorEntry(K key);

    /**
     * Returns the collection associated with the least key greater than or equal to the given key,
     * or an empty collection if there is no such key.
     *
     * @param key the key
     * @return the collection associated with the least key greater than or equal to the given key
     */
    Collection<V> ceilingEntry(K key);

    /**
     * Returns the collection associated with the greatest key strictly less than the given key, or
     * an empty collection if there is no such key.
     *
     * @param key the key
     * @return the collection associated with the greatest key strictly less than the given key
     */
    Collection<V> lowerEntry(K key);

    /**
     * Returns the collection associated with the least key strictly greater than the given key, or
     * an empty collection if there is no such key.
     *
     * @param key the key
     * @return the collection associated with the least key strictly greater than the given key
     */
    Collection<V> higherEntry(K key);

    /**
     * Removes and returns the collection associated with the least key in this map, or an empty
     * collection if the map is empty.
     *
     * @return the collection associated with the least key in this map
     */
    Collection<V> pollFirstEntry();

    /**
     * Removes and returns the collection associated with the greatest key in this map, or an empty
     * collection if the map is empty.
     *
     * @return the collection associated with the greatest key in this map
     */
    Collection<V> pollLastEntry();
}
