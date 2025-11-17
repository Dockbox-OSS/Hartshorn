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
public class UnmodifiableNavigableMultiMap<K, V>
        extends UnmodifiableMultiMap<K, V>
        implements NavigableMultiMap<K, V> {

    public UnmodifiableNavigableMultiMap(NavigableMultiMap<K, V> map) {
        super(map);
    }

    @Override
    protected NavigableMultiMap<K, V> internalMap() {
        return (NavigableMultiMap<K, V>) super.internalMap();
    }

    @Override
    public Collection<V> firstEntry() {
        return this.internalMap().firstEntry();
    }

    @Override
    public Collection<V> lastEntry() {
        return this.internalMap().lastEntry();
    }

    @Override
    public Collection<V> floorEntry(K key) {
        return this.internalMap().floorEntry(key);
    }

    @Override
    public Collection<V> ceilingEntry(K key) {
        return this.internalMap().ceilingEntry(key);
    }

    @Override
    public Collection<V> lowerEntry(K key) {
        return this.internalMap().lowerEntry(key);
    }

    @Override
    public Collection<V> higherEntry(K key) {
        return this.internalMap().higherEntry(key);
    }

    @Override
    public Collection<V> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<V> pollLastEntry() {
        throw new UnsupportedOperationException();
    }
}
