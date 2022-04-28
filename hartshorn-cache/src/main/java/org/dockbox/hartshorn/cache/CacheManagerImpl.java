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

package org.dockbox.hartshorn.cache;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.util.Exceptional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

/**
 * Default implementation of {@link CacheManager}.
 *
 * @see CacheManager
 */
@Component(singleton = true)
public class CacheManagerImpl implements CacheManager {

    protected final Map<String, Cache<?>> caches = new ConcurrentHashMap<>();

    @Inject
    private ApplicationContext context;

    @Override
    public List<Cache<?>> caches() {
        return List.copyOf(this.caches.values());
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
                    final Cache<?> cache = this.context.get(CacheFactory.class).cache(expiration);
                    this.caches.put(name, cache);
                    return (Cache<Object>) cache;
                })
                .map(c -> (Cache<T>) c)
                .get();
    }
}
