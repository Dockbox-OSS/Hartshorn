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

import java.util.Map;

/**
 * A {@link Map} that allows for bidirectional lookup of keys and values. This interface is a subset of the {@link Map}
 * interface, and does not include methods that would break the bidirectional lookup.
 *
 * <p>The inverse of a {@link BiMap} can be obtained by calling {@link #inverse()}. The inverse of a {@link BiMap} is
 * another {@link Map} that contains the same entries as the original {@link BiMap}, but with the keys and values
 * swapped.
 *
 * @param <K> the type of the keys
 * @param <V> the type of the values
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface BiMap<K, V> extends Map<K, V> {

    /**
     * Creates a new {@link BiMap} with the given {@link java.util.Map.Entry entries} as initial entries.
     *
     * @param entries the entries to add to the {@link BiMap}
     * @return a new {@link BiMap} with the given entries
     * @param <K> the type of the keys
     * @param <V> the type of the values
     */
    @SafeVarargs
    static <K, V> BiMap<K, V> ofEntries(Entry<K, V>... entries) {
        BiMap<K, V> map = new HashBiMap<>();
        for (Entry<K, V> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    /**
     * Returns the inverse of this {@link BiMap}. The inverse of a {@link BiMap} is another {@link Map} that contains
     * the same entries as the original {@link BiMap}, but with the keys and values swapped.
     *
     * @return the inverse of this {@link BiMap}
     */
    Map<V, K> inverse();
}