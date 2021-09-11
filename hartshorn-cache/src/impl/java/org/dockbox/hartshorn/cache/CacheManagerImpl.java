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
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Default implementation of {@link CacheManager}.
 *
 * @see CacheManager
 */
@SuppressWarnings("unchecked")
@Binds(CacheManager.class)
public class CacheManagerImpl implements CacheManager {

    protected static final Map<String, Cache<?>> caches = HartshornUtils.emptyConcurrentMap();

    @Inject
    private ApplicationContext context;

    @Override
    public List<Cache<?>> caches() {
        return HartshornUtils.asUnmodifiableList(CacheManagerImpl.caches.values());
    }

    @Override
    public <T> Exceptional<Cache<T>> get(final String cache) {
        return Exceptional.of(CacheManagerImpl.caches.get(cache)).map(c -> (Cache<T>) c);
    }

    @Override
    public <T> void update(final String cache, final T object) {
        final Cache<T> c = (Cache<T>) CacheManagerImpl.caches.get(cache);
        if (null != c) c.update(object);
    }

    @Override
    public void evict(final String cache) {
        final Cache<?> c = CacheManagerImpl.caches.get(cache);
        if (null != c) c.evict();
    }

    @Override
    public <T> Cache<T> getOrCreate(final String name, final Expiration expiration) {
        return this.get(name)
                .orElse(() -> {
                    final Cache<Object> cache = this.context.get(Cache.class, new ExpirationAttribute(expiration));
                    CacheManagerImpl.caches.put(name, cache);
                    return cache;
                })
                .map(c -> (Cache<T>) c)
                .get();
    }
}
