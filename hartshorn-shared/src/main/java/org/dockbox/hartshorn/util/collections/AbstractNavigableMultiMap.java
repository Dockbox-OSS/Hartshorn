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
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * A {@link MultiMap} implementation that uses a {@link NavigableMap} as its backing map.
 *
 * @param <K> the type of the keys
 * @param <V> the type of the values
 *
 * @see TreeMap
 * @see MultiMap
 * @see StandardMultiMap
 * @see NavigableMultiMap
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public abstract class AbstractNavigableMultiMap<K, V>
    extends StandardMultiMap<K, V>
    implements NavigableMultiMap<K, V> {

    private final Comparator<? super K> comparator;

    protected AbstractNavigableMultiMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }

    protected AbstractNavigableMultiMap(Comparator<? super K> comparator, MultiMap<K, V> map) {
        super(map);
        this.comparator = comparator;
    }

    @Override
    public Collection<V> firstEntry() {
        if (this.isEmpty()) {
            return List.of();
        }
        return this.map().firstEntry().getValue();
    }

    @Override
    public Collection<V> lastEntry() {
        if (this.isEmpty()) {
            return List.of();
        }
        return this.map().lastEntry().getValue();
    }

    @Override
    public Collection<V> floorEntry(K key) {
        Entry<K, Collection<V>> entry = this.map().floorEntry(key);
        return entry == null ? List.of() : entry.getValue();
    }

    @Override
    public Collection<V> ceilingEntry(K key) {
        Entry<K, Collection<V>> entry = this.map().ceilingEntry(key);
        return entry == null ? List.of() : entry.getValue();
    }

    @Override
    public Collection<V> lowerEntry(K key) {
        Entry<K, Collection<V>> entry = this.map().lowerEntry(key);
        return entry == null ? List.of() : entry.getValue();
    }

    @Override
    public Collection<V> higherEntry(K key) {
        Entry<K, Collection<V>> entry = this.map().higherEntry(key);
        return entry == null ? List.of() : entry.getValue();
    }

    @Override
    public Collection<V> pollFirstEntry() {
        if (this.isEmpty()) {
            return List.of();
        }
        return this.map().pollFirstEntry().getValue();
    }

    @Override
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
