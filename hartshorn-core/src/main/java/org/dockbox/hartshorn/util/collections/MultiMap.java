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
import java.util.function.BiConsumer;

public interface MultiMap<K, V> {

    Collection<V> allValues();

    void putAll(K key, Collection<V> values);

    void putAll(MultiMap<K, V> map);

    void put(K key, V value);

    void putIfAbsent(K key, V value);

    Collection<V> get(K key);

    Set<K> keySet();

    Set<Map.Entry<K, Collection<V>>> entrySet();

    Collection<Collection<V>> values();

    boolean containsKey(K key);

    boolean containsValue(V value);

    boolean containsEntry(K key, V value);

    Collection<V> remove(K key);

    void clear();

    int size();

    boolean isEmpty();

    boolean remove(K key, V value);

    boolean replace(K key, V oldValue, V newValue);

    void forEach(BiConsumer<K, V> consumer);

    static <K, V> MultiMapBuilder<K, V> builder() {
        return new MultiMapBuilder<>();
    }
}
