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

package org.dockbox.hartshorn.util;

import java.util.Map.Entry;
import java.util.Objects;

/**
 * Represents a simple tuple holding a single key of type {@code K} and value
 * of type {@code V}. This can be used to populate {@link java.util.Map maps}.
 *
 * @param <K> The type of the key represented by this tuple
 * @param <V> The type of the value represented by this tuple
 *
 * @since 0.4.0
 *
 * @author Guus Lieben
 */
public class Tuple<K, V> implements Entry<K, V> {

    private final K key;
    private final V value;

    public Tuple(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the key of this tuple. Equivalent to {@link #getKey()}.
     *
     * @return the key of this tuple
     */
    public K key() {
        return this.key;
    }

    /**
     * Returns the value of this tuple. Equivalent to {@link #getValue()}.
     *
     * @return the value of this tuple
     */
    public V value() {
        return this.value;
    }

    /**
     * Creates a new {@link Tuple} instance with the given key and value.
     *
     * @param key the key
     * @param value the value
     * @return a new {@link Tuple} instance
     * @param <K> the type of the key
     * @param <V> the type of the value
     */
    public static <K, V> Tuple<K, V> of(K key, V value) {
        return new Tuple<>(key, value);
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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        Tuple<?, ?> tuple = (Tuple<?, ?>) other;
        return Objects.equals(this.key, tuple.key) && Objects.equals(this.value, tuple.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.value);
    }
}
