/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
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
 * @param <K>
 *         The type of the key represented by this tuple
 * @param <V>
 *         The type of the value represented by this tuple
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
