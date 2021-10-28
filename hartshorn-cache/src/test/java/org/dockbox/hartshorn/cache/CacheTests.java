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

import org.dockbox.hartshorn.cache.annotations.UseCaching;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.binding.Providers;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.testsuite.ApplicationAwareTest;
import org.dockbox.hartshorn.testsuite.JUnitInjector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@UseCaching
public class CacheTests extends ApplicationAwareTest {

    @BeforeAll
    public static void register() {
        JUnitInjector.register(Key.of(CacheManager.class), Providers.of(JUnitCacheManager.class));
    }

    @Test
    void testEvictMethodIsCalled() {
        final NonAbstractCacheService service = this.context().get(NonAbstractCacheService.class);
        Assertions.assertTrue(service.evict());
    }

    @Test
    void testUpdateMethodIsCalled() {
        final NonAbstractCacheService service = this.context().get(NonAbstractCacheService.class);
        final long update = service.update(3L);
        Assertions.assertEquals(6, update);
    }

    @Test
    void testCacheIsReused() {
        final TestCacheService service = this.context().get(TestCacheService.class);
        final long first = service.getCachedTime();
        Assertions.assertTrue(first > 0);

        final long second = service.getCachedTime();
        Assertions.assertEquals(first, second);
    }

    @Test
    void testCacheCanBeUpdated() {
        final TestCacheService service = this.context().get(TestCacheService.class);
        long cached = service.getCachedTime();
        Assertions.assertTrue(cached > 0);

        service.update(3);
        cached = service.getCachedTime();
        Assertions.assertEquals(3, cached);
    }

    @Test
    void testCacheCanBeEvicted() {
        final TestCacheService service = this.context().get(TestCacheService.class);
        final long first = service.getCachedTime();
        service.evict();
        final long second = service.getCachedTime();
        Assertions.assertNotEquals(first, second);
    }

    @Test
    void testCacheCanBeUpdatedThroughManager() {
        final TestCacheService service = this.context().get(TestCacheService.class);
        final long cached = service.getCachedTime();
        Assertions.assertTrue(cached > 0);

        final CacheManager cacheManager = this.context().get(CacheManager.class);
        cacheManager.update("sample", 3L);

        final Exceptional<Cache<Long>> cache = cacheManager.get("sample");
        Assertions.assertTrue(cache.present());

        final Exceptional<Long> content = cache.get().get();
        Assertions.assertTrue(content.present());

        final long object = content.get();
        Assertions.assertEquals(3, object);
    }

    @Test
    void testCacheCanBeEvictedThroughManager() {
        // Initial population through source service
        final TestCacheService service = this.context().get(TestCacheService.class);
        final long cached = service.getCachedTime();
        Assertions.assertTrue(cached > 0);

        final CacheManager cacheManager = this.context().get(CacheManager.class);
        cacheManager.evict("sample");

        final Exceptional<Cache<Long>> cache = cacheManager.get("sample");
        Assertions.assertTrue(cache.present());

        final Exceptional<Long> content = cache.get().get();
        Assertions.assertTrue(content.absent());
    }

    @AfterEach
    void reset() {
        this.context().get(JUnitCacheManager.class).reset();
    }
}
