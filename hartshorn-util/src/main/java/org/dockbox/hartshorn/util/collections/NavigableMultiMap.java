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

package org.dockbox.hartshorn.util.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * A {@link MultiMap} implementation that uses {@link TreeMap} as its backing map. As the backing
 * map is a {@link NavigableMap}, this implementation provides methods to retrieve entries based
 * on their relation to other entries in the map, such as {@link #firstEntry()}, {@link
 * #lastEntry()}, etc.
 *
 * @param <K> the type of the keys
 * @param <V> the type of the values
 *
 * @since 0.5.0
 *
 * @see TreeMap
 * @see MultiMap
 * @see StandardMultiMap
 *
 * @author Guus Lieben
 */
public abstract class NavigableMultiMap<K extends Comparable<K>, V> extends StandardMultiMap<K, V> {

    private final Comparator<? super K> comparator;

    protected NavigableMultiMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }

    protected NavigableMultiMap(Comparator<? super K> comparator, MultiMap<K, V> map) {
        super(map);
        this.comparator = comparator;
    }

    /**
     * Returns the first entry in the map, or an empty collection if the map is empty.
     *
     * @return the first entry in the map, or an empty collection if the map is empty
     */
    public Collection<V> firstEntry() {
        if (this.isEmpty()) {
            return List.of();
        }
        return this.map().firstEntry().getValue();
    }

    /**
     * Returns the last entry in the map, or an empty collection if the map is empty.
     *
     * @return the last entry in the map, or an empty collection if the map is empty
     */
    public Collection<V> lastEntry() {
        if (this.isEmpty()) {
            return List.of();
        }
        return this.map().lastEntry().getValue();
    }

    /**
     * Returns the collection associated with the greatest key less than or equal to the given key, or
     * an empty collection if there is no such key.
     *
     * @param key the key
     * @return the collection associated with the greatest key less than or equal to the given key
     */
    public Collection<V> floorEntry(K key) {
        Entry<K, Collection<V>> entry = this.map().floorEntry(key);
        return entry == null ? List.of() : entry.getValue();
    }

    /**
     * Returns the collection associated with the least key greater than or equal to the given key, or
     * an empty collection if there is no such key.
     *
     * @param key the key
     * @return the collection associated with the least key greater than or equal to the given key
     */
    public Collection<V> ceilingEntry(K key) {
        Entry<K, Collection<V>> entry = this.map().ceilingEntry(key);
        return entry == null ? List.of() : entry.getValue();
    }

    /**
     * Returns the collection associated with the greatest key strictly less than the given key, or
     * an empty collection if there is no such key.
     *
     * @param key the key
     * @return the collection associated with the greatest key strictly less than the given key
     */
    public Collection<V> lowerEntry(K key) {
        Entry<K, Collection<V>> entry = this.map().lowerEntry(key);
        return entry == null ? List.of() : entry.getValue();
    }

    /**
     * Returns the collection associated with the least key strictly greater than the given key, or
     * an empty collection if there is no such key.
     *
     * @param key the key
     * @return the collection associated with the least key strictly greater than the given key
     */
    public Collection<V> higherEntry(K key) {
        Entry<K, Collection<V>> entry = this.map().higherEntry(key);
        return entry == null ? List.of() : entry.getValue();
    }

    /**
     * Removes and returns the collection associated with the least key in this map, or an empty collection if
     * the map is empty.
     *
     * @return the collection associated with the least key in this map
     */
    public Collection<V> pollFirstEntry() {
        if (this.isEmpty()) {
            return List.of();
        }
        return this.map().pollFirstEntry().getValue();
    }

    /**
     * Removes and returns the collection associated with the greatest key in this map, or an empty collection if
     * the map is empty.
     *
     * @return the collection associated with the greatest key in this map
     */
    public Collection<V> pollLastEntry() {
        if (this.isEmpty()) {
            return List.of();
        }
        return this.map().pollLastEntry().getValue();
    }

    @Override
    protected NavigableMap<K, Collection<V>> map() {
        return (NavigableMap<K, Collection<V>>) super.map();
    }

    @Override
    protected NavigableMap<K, Collection<V>> createEmptyMap() {
        return new TreeMap<>(this.comparator);
    }
}
