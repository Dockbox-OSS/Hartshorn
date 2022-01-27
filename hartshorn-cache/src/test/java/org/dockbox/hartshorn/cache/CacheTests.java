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

import org.dockbox.hartshorn.cache.annotations.UseCaching;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import lombok.Getter;

@UseCaching
@HartshornTest
public class CacheTests {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    @Test
    void testEvictMethodIsCalled() {
        final NonAbstractCacheService service = this.applicationContext().get(NonAbstractCacheService.class);
        Assertions.assertTrue(service.evict());
    }

    @Test
    void testUpdateMethodIsCalled() {
        final NonAbstractCacheService service = this.applicationContext().get(NonAbstractCacheService.class);
        final long update = service.update(3L);
        Assertions.assertEquals(6, update);
    }

    @Test
    void testCacheIsReused() {
        final TestCacheService service = this.applicationContext().get(TestCacheService.class);
        final long first = service.getCachedTime();
        Assertions.assertTrue(first > 0);

        final long second = service.getCachedTime();
        Assertions.assertEquals(first, second);
    }

    @Test
    void testCacheCanBeUpdated() {
        final TestCacheService service = this.applicationContext().get(TestCacheService.class);
        long cached = service.getCachedTime();
        Assertions.assertTrue(cached > 0);

        service.update(3);
        cached = service.getCachedTime();
        Assertions.assertEquals(3, cached);
    }

    @Test
    void testCacheCanBeEvicted() {
        final TestCacheService service = this.applicationContext().get(TestCacheService.class);
        final long first = service.getCachedTime();
        service.evict();
        final long second = service.getCachedTime();
        Assertions.assertNotEquals(first, second);
    }

    @Test
    void testCacheCanBeUpdatedThroughManager() {
        final TestCacheService service = this.applicationContext().get(TestCacheService.class);
        final long cached = service.getCachedTime();
        Assertions.assertTrue(cached > 0);

        final CacheManager cacheManager = this.applicationContext().get(CacheManager.class);
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
        final TestCacheService service = this.applicationContext().get(TestCacheService.class);
        final long cached = service.getCachedTime();
        Assertions.assertTrue(cached > 0);

        final CacheManager cacheManager = this.applicationContext().get(CacheManager.class);
        cacheManager.evict("sample");

        final Exceptional<Cache<Long>> cache = cacheManager.get("sample");
        Assertions.assertTrue(cache.present());

        final Exceptional<Long> content = cache.get().get();
        Assertions.assertTrue(content.absent());
    }

    @AfterEach
    void reset() {
        this.applicationContext().get(JUnitCacheManager.class).reset();
    }
}
