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
import org.dockbox.hartshorn.test.ApplicationAwareTest;
import org.dockbox.hartshorn.test.util.JUnitCacheManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

public class CacheTests extends ApplicationAwareTest {

    @Test
    void testCacheIsReused() {
        final TestCacheService service = this.context().get(TestCacheService.class);
        List<String> cached = service.cachedObjects();
        Assertions.assertNotNull(cached);
        Assertions.assertFalse(cached.isEmpty());

        final String first = cached.get(0);
        Assertions.assertNotNull(first);

        cached = service.cachedObjects();
        Assertions.assertNotNull(cached);
        Assertions.assertFalse(cached.isEmpty());

        final String second = cached.get(0);
        Assertions.assertNotNull(second);

        Assertions.assertEquals(first, second);
    }

    @Test
    void testCacheCanBeUpdated() {
        final TestCacheService service = this.context().get(TestCacheService.class);
        List<String> cached = service.cachedObjects();
        Assertions.assertEquals(1, cached.size());
        final String first = cached.get(0);

        service.updateCache("second value");
        cached = service.cachedObjects();
        Assertions.assertEquals(2, cached.size());

        final String newFirst = cached.get(0);
        Assertions.assertEquals(first, newFirst);

        final String second = cached.get(1);
        Assertions.assertEquals("second value", second);
    }

    @Test
    void testCacheCanBeEvicted() {
        final TestCacheService service = this.context().get(TestCacheService.class);
        List<String> cached = service.cachedObjects();
        final String first = cached.get(0);

        service.evict();
        cached = service.cachedObjects();
        final String second = cached.get(0);
        Assertions.assertNotEquals(first, second);
    }

    @Test
    void testCacheCanBeUpdatedThroughManager() {
        final TestCacheService service = this.context().get(TestCacheService.class);
        final List<String> cached = service.cachedObjects();
        Assertions.assertEquals(1, cached.size());
        final String first = cached.get(0);

        final CacheManager cacheManager = this.context().get(CacheManager.class);
        cacheManager.update("sample", "second value");

        final Cache<Object> cache = cacheManager.get("sample").get();
        final Exceptional<Collection<Object>> content = cache.get();
        Assertions.assertTrue(content.present());

        final List<Object> objects = (List<Object>) content.get();
        Assertions.assertEquals(2, objects.size());

        final String newFirst = (String) objects.get(0);
        Assertions.assertEquals(first, newFirst);

        final String second = (String) objects.get(1);
        Assertions.assertEquals("second value", second);
    }

    @Test
    void testCacheCanBeEvictedThroughManager() {
        // Initial population through source service
        final TestCacheService service = this.context().get(TestCacheService.class);
        final List<String> cached = service.cachedObjects();
        Assertions.assertNotNull(cached);
        Assertions.assertFalse(cached.isEmpty());

        final CacheManager cacheManager = this.context().get(CacheManager.class);
        cacheManager.evict("sample");

        final Cache<Object> cache = cacheManager.get("sample").get();
        final Exceptional<Collection<Object>> content = cache.get();
        Assertions.assertTrue(content.absent());
    }

    @AfterEach
    void reset() {
        this.context().get(JUnitCacheManager.class).reset();
    }
}
