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

package org.dockbox.hartshorn.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public abstract class MultiMap<K, V> {

    protected Map<K, Collection<V>> map;

    protected Map<K, Collection<V>> map() {
        if (this.map == null) {
            this.map = new ConcurrentHashMap<>();
        }
        return this.map;
    }

    protected abstract Collection<V> baseCollection();

    public Collection<V> allValues() {
        return this.values().stream().flatMap(Collection::stream).toList();
    }

    public void putAll(final K key, final Collection<V> values) {
        values.forEach(v -> this.put(key, v));
    }

    public void put(final K key, final V value) {
        this.map().computeIfAbsent(key, k -> this.baseCollection()).add(value);
    }

    public void putIfAbsent(final K key, final V value) {
        this.map().computeIfAbsent(key, k -> this.baseCollection());
        if (!this.map().get(key).contains(value)) {
            this.map().get(key).add(value);
        }
    }

    public Collection<V> get(final K key) {
        return this.map().getOrDefault(key, this.baseCollection());
    }

    public Set<K> keySet() {
        return this.map().keySet();
    }

    public Set<Map.Entry<K, Collection<V>>> entrySet() {
        return this.map().entrySet();
    }

    public Collection<Collection<V>> values() {
        return this.map().values();
    }

    public boolean containsKey(final K key) {
        return this.map().containsKey(key);
    }

    public Collection<V> remove(final K key) {
        return this.map().remove(key);
    }

    public int size() {
        int size = 0;
        for (final Collection<V> value : this.map().values()) {
            size += value.size();
        }
        return size;
    }

    public boolean isEmpty() {
        return this.map().isEmpty();
    }

    public void clear() {
        this.map().clear();
    }

    public boolean remove(final K key, final V value) {
        if (this.map().get(key) != null)
            return this.map().get(key).remove(value);

        return false;
    }

    public boolean replace(final K key, final V oldValue, final V newValue) {
        if (this.map().get(key) != null) {
            if (this.map().get(key).remove(oldValue)) {
                return this.map().get(key).add(newValue);
            }
        }
        return false;
    }

    public void forEach(BiConsumer<K, V> consumer) {
        this.map().forEach((k, v) -> v.forEach(v1 -> consumer.accept(k, v1)));
    }
}
