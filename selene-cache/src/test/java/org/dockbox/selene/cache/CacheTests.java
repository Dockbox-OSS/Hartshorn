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

package org.dockbox.selene.cache;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.test.SeleneJUnit5Runner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collection;
import java.util.List;

@ExtendWith(SeleneJUnit5Runner.class)
public class CacheTests {

    @Test
    void testCacheIsReused() {
        TestCacheService service = Selene.context().get(TestCacheService.class);
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
    void testCacheCanBeEvicted() {
        TestCacheService service = Selene.context().get(TestCacheService.class);
        List<String> cached = service.getCachedThings();
        Assertions.assertNotNull(cached);
        Assertions.assertFalse(cached.isEmpty());

        final CacheManager cacheManager = Selene.context().get(CacheManager.class);
        cacheManager.evict("test-cache.getcachedthings");

        final Cache<Object> cache = cacheManager.get("test-cache.getcachedthings").get();
        final Exceptional<Collection<Object>> content = cache.get();
        Assertions.assertTrue(content.absent());
    }
}
