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

package org.dockbox.hartshorn.cache.context;

import org.dockbox.hartshorn.cache.Cache;
import org.dockbox.hartshorn.cache.CacheManager;

import java.util.function.Supplier;

import lombok.Getter;

public class SimpleCacheContext implements CacheContext {

    @Getter
    private final CacheManager manager;
    @Getter
    private final String name;
    private final Supplier<Cache<?>> supplier;

    public SimpleCacheContext(CacheManager manager, Supplier<Cache<?>> supplier, String name) {
        this.manager = manager;
        this.name = name;
        this.supplier = supplier;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Cache<T> cache() {
        return (Cache<T>) this.supplier.get();
    }
}
