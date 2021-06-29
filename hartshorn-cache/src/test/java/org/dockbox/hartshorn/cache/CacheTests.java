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

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.test.HartshornRunner;
import org.dockbox.hartshorn.test.util.JUnitCacheManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collection;
import java.util.List;

@ExtendWith(HartshornRunner.class)
public class CacheTests {

    @Test
    void testCacheIsReused() {
        TestCacheService service = Hartshorn.context().get(TestCacheService.class);
        List<String> cached = service.getCachedThings();
        Assertions.assertNotNull(cached);
        Assertions.assertFalse(cached.isEmpty());

        final String first = cached.get(0);
        Assertions.assertNotNull(first);

        cached = service.getCachedThings();
        Assertions.assertNotNull(cached);
        Assertions.assertFalse(cached.isEmpty());

        final String second = cached.get(0);
        Assertions.assertNotNull(second);

        Assertions.assertEquals(first, second);
    }

    @Test
    void testCacheCanBeUpdated() {
        TestCacheService service = Hartshorn.context().get(TestCacheService.class);
        List<String> cached = service.getCachedThings();
        Assertions.assertEquals(1, cached.size());
        String first = cached.get(0);

        service.updateCache("second value");
        cached = service.getCachedThings();
        Assertions.assertEquals(2, cached.size());

        String newFirst = cached.get(0);
        Assertions.assertEquals(first, newFirst);

        String second = cached.get(1);
        Assertions.assertEquals("second value", second);
    }

    @Test
    void testCacheCanBeEvicted() {
        TestCacheService service = Hartshorn.context().get(TestCacheService.class);
        List<String> cached = service.getCachedThings();
        String first = cached.get(0);

        service.evict();
        cached = service.getCachedThings();
        String second = cached.get(0);
        Assertions.assertNotEquals(first, second);
    }

    @Test
    void testCacheCanBeUpdatedThroughManager() {
        TestCacheService service = Hartshorn.context().get(TestCacheService.class);
        List<String> cached = service.getCachedThings();
        Assertions.assertEquals(1, cached.size());
        String first = cached.get(0);

        final CacheManager cacheManager = Hartshorn.context().get(CacheManager.class);
        cacheManager.update("sample", "second value");

        final Cache<Object> cache = cacheManager.get("sample").get();
        final Exceptional<Collection<Object>> content = cache.get();
        Assertions.assertTrue(content.present());

        final List<Object> objects = (List<Object>) content.get();
        Assertions.assertEquals(2, objects.size());

        String newFirst = (String) objects.get(0);
        Assertions.assertEquals(first, newFirst);

        String second = (String) objects.get(1);
        Assertions.assertEquals("second value", second);
    }

    @Test
    void testCacheCanBeEvictedThroughManager() {
        // Initial population through source service
        TestCacheService service = Hartshorn.context().get(TestCacheService.class);
        List<String> cached = service.getCachedThings();
        Assertions.assertNotNull(cached);
        Assertions.assertFalse(cached.isEmpty());

        final CacheManager cacheManager = Hartshorn.context().get(CacheManager.class);
        cacheManager.evict("sample");

        final Cache<Object> cache = cacheManager.get("sample").get();
        final Exceptional<Collection<Object>> content = cache.get();
        Assertions.assertTrue(content.absent());
    }

    @AfterEach
    void reset() {
        Hartshorn.context().get(JUnitCacheManager.class).reset();
    }
}
