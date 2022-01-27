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

package org.dockbox.hartshorn.core.domain.tuple;

import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a simple tuple holding a single key of type <code>K</code> and value
 * of type <code>V</code>. This can be used to populate {@link java.util.Map maps}.
 *
 * @param <K> The type of the key represented by this tuple
 * @param <V> The type of the value represented by this tuple
 */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Tuple<K, V> implements Entry<K, V> {

    private final K key;
    private final V value;

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
}
