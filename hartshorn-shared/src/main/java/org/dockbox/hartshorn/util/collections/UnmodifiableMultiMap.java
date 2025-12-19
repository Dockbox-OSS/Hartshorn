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

import org.dockbox.hartshorn.util.stream.EntryStream;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * An unmodifiable {@link MultiMap} implementation that wraps another {@link MultiMap} and prevents
 * any modifications.
 *
 * @param <K> the type of the keys
 * @param <V> the type of the values
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class UnmodifiableMultiMap<K, V> implements MultiMap<K, V> {

    private final MultiMap<K, V> map;

    public UnmodifiableMultiMap(MultiMap<K, V> map) {
        this.map = map;
    }

    /**
     * Get the internal modifiable map. This may be used by subclasses to delegate read operations.
     *
     * @return the internal modifiable map
     */
    protected MultiMap<K, V> internalMap() {
        return this.map;
    }

    @Override
    public Collection<V> allValues() {
        return this.internalMap().allValues();
    }

    @Override
    public void putAll(K key, Collection<V> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(MultiMap<K, V> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putIfAbsent(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<V> get(K key) {
        return List.copyOf(this.internalMap().get(key));
    }

    @Override
    public Set<K> keySet() {
        return CollectionUtilities.copyOf(this.internalMap().keySet());
    }

    @Override
    public Set<Entry<K, Collection<V>>> entrySet() {
        return CollectionUtilities.copyOf(this.internalMap().entrySet());
    }

    @Override
    public Collection<Collection<V>> values() {
        return List.copyOf(this.internalMap().values());
    }

    @Override
    public boolean containsKey(K key) {
        return this.internalMap().containsKey(key);
    }

    @Override
    public boolean containsValue(V value) {
        return this.internalMap().containsValue(value);
    }

    @Override
    public boolean containsEntry(K key, V value) {
        return this.internalMap().containsEntry(key, value);
    }

    @Override
    public Collection<V> remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return this.internalMap().size();
    }

    @Override
    public boolean isEmpty() {
        return this.internalMap().isEmpty();
    }

    @Override
    public boolean remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int removeValue(V processor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forEach(BiConsumer<K, V> consumer) {
        this.internalMap().forEach(consumer);
    }

    @Override
    public int removeIf(BiPredicate<K, V> predicate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntryStream<K, Collection<V>> stream() {
        return this.internalMap().stream();
    }

    @Override
    public Iterator<Entry<K, Collection<V>>> iterator() {
        return this.internalMap().iterator();
    }
}
