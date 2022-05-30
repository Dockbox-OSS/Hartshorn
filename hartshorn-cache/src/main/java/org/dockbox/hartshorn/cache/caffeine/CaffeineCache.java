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

package org.dockbox.hartshorn.cache.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;

import org.dockbox.hartshorn.cache.Cache;
import org.dockbox.hartshorn.cache.Expiration;
import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.util.Result;

public class CaffeineCache<K, V> implements Cache<K, V> {

    private final com.github.benmanes.caffeine.cache.Cache<K, V> cache;

    @Bound
    public CaffeineCache(final Expiration expiration) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        if (expiration.amount() > 0) {
            builder = builder.expireAfterWrite(expiration.toDuration());
        }
        this.cache = builder.build();
    }

    public com.github.benmanes.caffeine.cache.Cache<K, V> cache() {
        return this.cache;
    }

    @Override
    public Result<V> get(final K key) {
        return Result.of(this.cache.getIfPresent(key));
    }

    @Override
    public boolean contains(final K key) {
        return this.cache.getIfPresent(key) != null;
    }

    @Override
    public void putIfAbsent(final K key, final V value) {
        this.cache.get(key, k -> value);
    }

    @Override
    public void put(final K key, final V value) {
        this.cache.put(key, value);
    }

    @Override
    public void invalidate(final K key) {
        this.cache.invalidate(key);
    }

    @Override
    public int size() {
        return (int) this.cache.estimatedSize();
    }

    @Override
    public void invalidate() {
        this.cache.invalidateAll();
    }
}
