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

package org.dockbox.hartshorn.util;

import java.util.Map;

public class MapBuilder<K, V> {

    private final Map<K, V> map = HartshornUtils.emptyMap();

    public MapBuilder<K, V> add(K k, V v) {
        this.map.put(k, v);
        return this;
    }

    public MapBuilder<K, V> add(Map<K, V> map) {
        this.map.putAll(map);
        return this;
    }

    public MapBuilder<K, V> reset() {
        this.map.clear();
        return this;
    }

    public Map<K, V> get() {
        return this.map;
    }

}
