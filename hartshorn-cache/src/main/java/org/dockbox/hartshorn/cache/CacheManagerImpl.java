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

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.HartshornUtils;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Default implementation of {@link CacheManager}.
 *
 * @see CacheManager
 */
@SuppressWarnings("unchecked")
@Binds(CacheManager.class)
@Singleton
public class CacheManagerImpl implements CacheManager {

    protected final Map<String, Cache<?>> caches = HartshornUtils.emptyConcurrentMap();

    @Inject
    private ApplicationContext context;

    @Override
    public List<Cache<?>> caches() {
        return HartshornUtils.asUnmodifiableList(this.caches.values());
    }

    @Override
    public <T> Exceptional<Cache<T>> get(final String cache) {
        return Exceptional.of(this.caches.get(cache)).map(c -> (Cache<T>) c);
    }

    @Override
    public <T> void update(final String cache, final T object) {
        final Cache<T> c = (Cache<T>) this.caches.get(cache);
        if (null != c) c.update(object);
    }

    @Override
    public void evict(final String cache) {
        final Cache<?> c = this.caches.get(cache);
        if (null != c) c.evict();
    }

    @Override
    public <T> Cache<T> getOrCreate(final String name, final Expiration expiration) {
        return this.get(name)
                .orElse(() -> {
                    this.context.log().debug("Cache '" + name + "' does not exist, creating new instance");
                    final Cache<Object> cache = this.context.get(Cache.class, new ExpirationAttribute(expiration));
                    this.caches.put(name, cache);
                    return cache;
                })
                .map(c -> (Cache<T>) c)
                .get();
    }
}
