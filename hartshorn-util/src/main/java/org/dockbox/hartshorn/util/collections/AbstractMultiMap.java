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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;

public abstract class AbstractMultiMap<K, V> implements MultiMap<K, V> {

    protected abstract Map<K, Collection<V>> map();
    protected abstract Collection<V> createEmptyCollection();

    protected AbstractMultiMap() {
    }

    protected AbstractMultiMap(MultiMap<K, V> map) {
        this.putAll(map);
    }

    @Override
    public Collection<V> allValues() {
        return this.values().stream().flatMap(Collection::stream).toList();
    }

    @Override
    public void putAll(K key, Collection<V> values) {
        values.forEach(value -> this.put(key, value));
    }

    @Override
    public void putAll(MultiMap<K, V> map) {
        for (Entry<K, Collection<V>> collection : map.entrySet()) {
            this.putAll(collection.getKey(), collection.getValue());
        }
    }

    @Override
    public void put(K key, V value) {
        this.map().computeIfAbsent(key, key0 -> this.createEmptyCollection()).add(value);
    }

    @Override
    public void putIfAbsent(K key, V value) {
        this.map().computeIfAbsent(key, key0 -> this.createEmptyCollection());
        if (!this.map().get(key).contains(value)) {
            this.map().get(key).add(value);
        }
    }

    @Override
    public Collection<V> get(K key) {
        return this.map().getOrDefault(key, this.createEmptyCollection());
    }

    @Override
    public Set<K> keySet() {
        return this.map().keySet();
    }

    @Override
    public Set<Map.Entry<K, Collection<V>>> entrySet() {
        return this.map().entrySet();
    }

    @Override
    public Collection<Collection<V>> values() {
        return this.map().values();
    }

    @Override
    public boolean containsKey(K key) {
        return this.map().containsKey(key);
    }

    @Override
    public boolean containsValue(V value) {
        return this.map().values().stream().anyMatch(value0 -> value0.contains(value));
    }

    @Override
    public boolean containsEntry(K key, V value) {
        return this.map().containsKey(key) && this.map().get(key).contains(value);
    }

    @Override
    public Collection<V> remove(K key) {
        return this.map().remove(key);
    }

    @Override
    public int size() {
        int size = 0;
        for (Collection<V> value : this.map().values()) {
            size += value.size();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return this.map().isEmpty();
    }

    @Override
    public void clear() {
        this.map().clear();
    }

    @Override
    public boolean remove(K key, V value) {
        if (this.map().get(key) != null) {
            return this.map().get(key).remove(value);
        }

        return false;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        if (this.map().get(key) != null) {
            if (this.map().get(key).remove(oldValue)) {
                return this.map().get(key).add(newValue);
            }
        }
        return false;
    }

    @Override
    public void forEach(BiConsumer<K, V> consumer) {
        this.map().forEach((key, value) -> value.forEach(value0 -> consumer.accept(key, value0)));
    }
}
