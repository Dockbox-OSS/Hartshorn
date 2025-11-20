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

package org.dockbox.hartshorn.util;

import java.util.Map.Entry;

/**
 * Represents a simple tuple holding a single key of type {@code K} and value of type {@code V}.
 * This can be used to populate {@link java.util.Map maps}.
 *
 * @param key the key
 * @param value the value
 * @param <K> The type of the key represented by this tuple
 * @param <V> The type of the value represented by this tuple
 *
 * @since 0.4.1
 * 
 * @author Guus Lieben
 */
public record Tuple<K, V>(K key, V value) implements Entry<K, V> {

    /**
     * Creates a new {@link Tuple} instance with the given key and value.
     *
     * @param key the key
     * @param value the value
     * @param <K> the type of the key
     * @param <V> the type of the value
     *
     * @return a new {@link Tuple} instance
     */
    public static <K, V> Tuple<K, V> of(K key, V value) {
        return new Tuple<>(key, value);
    }

    public K left() {
        return this.key();
    }

    public V right() {
        return this.value();
    }

    @Override
    public K getKey() {
        return this.key();
    }

    @Override
    public V getValue() {
        return this.value();
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException("Cannot modify Tuple value");
    }
}
