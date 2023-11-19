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
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public abstract class TreeMultiMap<K extends Comparable<K>, V> extends StandardMultiMap<K, V> {

    private final Comparator<? super K> comparator;

    protected TreeMultiMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }

    protected TreeMultiMap(Comparator<? super K> comparator, MultiMap<K, V> map) {
        super(map);
        this.comparator = comparator;
    }

    public Collection<V> firstEntry() {
        return this.map().firstEntry().getValue();
    }

    public Collection<V> lastEntry() {
        return this.map().lastEntry().getValue();
    }

    public Collection<V> floorEntry(K key) {
        return this.map().floorEntry(key).getValue();
    }

    public Collection<V> ceilingEntry(K key) {
        return this.map().ceilingEntry(key).getValue();
    }

    public Collection<V> lowerEntry(K key) {
        return this.map().lowerEntry(key).getValue();
    }

    public Collection<V> higherEntry(K key) {
        return this.map().higherEntry(key).getValue();
    }

    public Collection<V> pollFirstEntry() {
        return this.map().pollFirstEntry().getValue();
    }

    public Collection<V> pollLastEntry() {
        return this.map().pollLastEntry().getValue();
    }

    @Override
    protected NavigableMap<K, Collection<V>> map() {
        return (NavigableMap<K, Collection<V>>) super.map();
    }

    @Override
    protected Map<K, Collection<V>> createEmptyMap() {
        return new TreeMap<>(this.comparator);
    }
}
