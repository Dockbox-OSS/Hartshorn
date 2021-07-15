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

package org.dockbox.hartshorn.cache;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.annotations.inject.Wired;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link CacheManager}.
 * @see CacheManager
 */
@SuppressWarnings("unchecked")
public class SimpleCacheManager implements CacheManager {

    protected static final Map<String, Cache<?>> caches = HartshornUtils.emptyConcurrentMap();

    @Wired
    private ApplicationContext context;

    @Override
    public List<Cache<?>> caches() {
        return HartshornUtils.asUnmodifiableList(SimpleCacheManager.caches.values());
    }

    @Override
    public <T> Exceptional<Cache<T>> get(String cache) {
        return Exceptional.of(SimpleCacheManager.caches.get(cache)).map(c -> (Cache<T>) c);
    }

    @Override
    public <T> void update(String cache, T object) {
        final Cache<T> c = (Cache<T>) SimpleCacheManager.caches.get(cache);
        if (null != c) c.update(object);
    }

    @Override
    public void evict(String cache) {
        final Cache<?> c = SimpleCacheManager.caches.get(cache);
        if (null != c) c.evict();
    }

    @Override
    public <T> Cache<T> getOrCreate(String name, Expiration expiration) {
        return this.get(name)
                .orElse(() -> {
                    final Cache<Object> cache = this.context.get(Cache.class, new ExpirationProperty(expiration));
                    SimpleCacheManager.caches.put(name, cache);
                    return cache;
                })
                .map(c -> (Cache<T>) c)
                .get();
    }
}
