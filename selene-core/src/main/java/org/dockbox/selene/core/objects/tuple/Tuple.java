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

package org.dockbox.selene.core.objects.tuple;

import java.util.Map.Entry;
import java.util.Objects;

public class Tuple<K, V> implements Entry<K, V>
{

    private final K key;
    private final V value;

    public Tuple(K key, V value)
    {
        this.key = key;
        this.value = value;
    }

    public static <K, V> Tuple<K, V> of(K key, V value)
    {
        return new Tuple<>(key, value);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.getKey(), this.getValue());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return this.getKey().equals(tuple.getKey()) &&
                Objects.equals(this.getValue(), tuple.getValue());
    }

    @Override
    public K getKey()
    {
        return this.key;
    }

    @Override
    public V getValue()
    {
        return this.value;
    }

    @Override
    public V setValue(V value)
    {
        throw new UnsupportedOperationException("Cannot modify final Tuple value");
    }
}
