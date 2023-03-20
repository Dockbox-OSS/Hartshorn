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
 */
public class Tuple<K, V> implements Entry<K, V> {

    private final K key;
    private final V value;

    public Tuple(final K key, final V value) {
        this.key = key;
        this.value = value;
    }

    public K key() {
        return this.key;
    }

    public V value() {
        return this.value;
    }

    public static <K, V> Tuple<K, V> of(final K key, final V value) {
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
    public V setValue(final V value) {
        throw new UnsupportedOperationException("Cannot modify final Tuple value");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(this.key, tuple.key) && Objects.equals(this.value, tuple.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.value);
    }
}
