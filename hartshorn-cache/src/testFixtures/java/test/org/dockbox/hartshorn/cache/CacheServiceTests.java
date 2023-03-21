/*
 * Copyright 2019-2023 the original author or authors.
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

package test.org.dockbox.hartshorn.cache;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.cache.Cache;
import org.dockbox.hartshorn.cache.CacheManager;
import org.dockbox.hartshorn.cache.annotations.UseCaching;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

@UseCaching
@HartshornTest(includeBasePackages = false)
public abstract class CacheServiceTests {

    @Inject
    private ApplicationContext applicationContext;

    protected abstract CacheManager cacheManager(ApplicationContext applicationContext);

    @Test
    @TestComponents(NonAbstractCacheService.class)
    void testEvictMethodIsCalled() {
        final NonAbstractCacheService service = this.applicationContext.get(NonAbstractCacheService.class);
        Assertions.assertTrue(service.evict());
    }

    @Test
    @TestComponents(NonAbstractCacheService.class)
    void testUpdateMethodIsCalled() {
        final NonAbstractCacheService service = this.applicationContext.get(NonAbstractCacheService.class);
        final long update = service.update(3L);
        Assertions.assertEquals(6, update);
    }

    @Test
    @TestComponents(TestCacheService.class)
    void testCacheIsReused() {
        final TestCacheService service = this.applicationContext.get(TestCacheService.class);
        final long first = service.getCachedTime();
        Assertions.assertTrue(first > 0);

        final long second = service.getCachedTime();
        Assertions.assertEquals(first, second);
    }

    @Test
    @TestComponents(TestCacheService.class)
    void testCacheCanBeUpdated() {
        final TestCacheService service = this.applicationContext.get(TestCacheService.class);
        long cached = service.getCachedTime();
        Assertions.assertTrue(cached > 0);

        service.update(3);
        cached = service.getCachedTime();
        Assertions.assertEquals(3, cached);
    }

    @Test
    @TestComponents(TestCacheService.class)
    void testCacheCanBeEvicted() {
        final TestCacheService service = this.applicationContext.get(TestCacheService.class);
        final long first = service.getCachedTime();
        service.evict();
        final long second = service.getCachedTime();
        Assertions.assertNotEquals(first, second);
    }

    @Test
    @TestComponents(TestCacheService.class)
    void testCacheCanBeUpdatedThroughManager() {
        final TestCacheService service = this.applicationContext.get(TestCacheService.class);
        final long cached = service.getCachedTime();
        Assertions.assertTrue(cached > 0);

        final CacheManager cacheManager = this.cacheManager(this.applicationContext);
        cacheManager.get("sample").peek(cache -> cache.put("sample_key", 3L));

        final Option<Cache<String, Long>> cache = cacheManager.get("sample");
        Assertions.assertTrue(cache.present());

        final Option<Long> content = cache.get().get("sample_key");
        Assertions.assertTrue(content.present());

        final long object = content.get();
        Assertions.assertEquals(3, object);
    }

    @Test
    @TestComponents(TestCacheService.class)
    void testCacheCanBeEvictedThroughManager() {
        // Initial population through source service
        final TestCacheService service = this.applicationContext.get(TestCacheService.class);
        final long cached = service.getCachedTime();
        Assertions.assertTrue(cached > 0);

        final CacheManager cacheManager = this.cacheManager(this.applicationContext);
        cacheManager.get("sample").peek(Cache::invalidate);

        final Option<Cache<String, Long>> cache = cacheManager.get("sample");
        Assertions.assertTrue(cache.present());

        final Option<Long> content = cache.get().get("sample_key");
        Assertions.assertTrue(content.absent());
    }
}