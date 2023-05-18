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
import java.util.Set;
import java.util.function.Function;

public abstract class AbstractBiMap<K, V> implements BiMap<K, V> {

    protected final Map<K, V> forward;
    protected final Map<V, K> backward;

    protected AbstractBiMap(final Map<K, V> forward, final Map<V, K> backward) {
        this.forward = forward;
        this.backward = backward;
    }

    @Override
    public V put(final K key, final V value) {
        if (this.forward.containsKey(key)) {
            this.backward.remove(this.forward.get(key));
        }
        if (this.backward.containsKey(value)) {
            this.forward.remove(this.backward.get(value));
        }
        this.backward.put(value, key);
        return this.forward.put(key, value);
    }

    @Override
    public V remove(final Object key) {
        final V value = this.forward.remove(key);
        this.backward.remove(value);
        return value;
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        map.forEach(this::put);
    }

    @Override
    public void clear() {
        this.forward.clear();
        this.backward.clear();
    }

    @Override
    public boolean containsValue(final Object value) {
        return this.forward.containsKey(value);
    }

    @Override
    public Map<V, K> inverse() {
        return this.backward;
    }

    @Override
    public int size() {
        return this.checkConsistent(Map::size);
    }

    @Override
    public boolean isEmpty() {
        return this.checkConsistent(Map::isEmpty);
    }

    @Override
    public boolean containsKey(final Object key) {
        return this.forward.containsKey(key);
    }

    @Override
    public V get(final Object key) {
        return this.forward.get(key);
    }

    @Override
    public Set<K> keySet() {
        return this.forward.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.forward.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.forward.entrySet();
    }

    private <T> T checkConsistent(final Function<Map<?, ?>, T> function) {
        final T forwardResult = function.apply(this.forward);
        final T backwardResult = function.apply(this.backward);
        if (!forwardResult.equals(backwardResult)) {
            throw new IllegalStateException("BiMap is in an inconsistent state");
        }
        return forwardResult;
    }
}
