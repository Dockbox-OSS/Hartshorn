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
import org.dockbox.hartshorn.cache.annotations.UseCaching;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import jakarta.inject.Inject;

@UseCaching
@HartshornTest
public class CacheTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testCacheCreation() {
        final Cache<?, ?> cache = this.applicationContext.get(Cache.class);
        Assertions.assertNotNull(cache);
    }

    @Test
    void testCacheCanWrite() {
        final Cache<String, String> cache = this.applicationContext.get(Cache.class);

        cache.put("key", "value");
        final Result<String> result = cache.get("key");

        Assertions.assertTrue(result.present());
        Assertions.assertEquals("value", result.get());
    }

    @Test
    void testCacheInvalidation() {
        final Cache<String, String> cache = this.applicationContext.get(Cache.class);

        cache.put("key", "value");
        cache.invalidate("key");
        final Result<String> result = cache.get("key");

        Assertions.assertTrue(result.absent());
    }

    @Test
    void testCacheSize() {
        final Cache<String, String> cache = this.applicationContext.get(Cache.class);

        cache.put("key", "value");
        final int size = cache.size();

        Assertions.assertEquals(1, size);
    }

    @Test
    void testCacheCanWriteAndReadMultiple() {
        final Cache<String, String> cache = this.applicationContext.get(Cache.class);

        cache.put("key", "value");
        cache.put("key2", "value2");
        final Result<String> result = cache.get("key");
        final Result<String> result2 = cache.get("key2");

        Assertions.assertTrue(result.present());
        Assertions.assertEquals("value", result.get());
        Assertions.assertTrue(result2.present());
        Assertions.assertEquals("value2", result2.get());
    }

    @Test
    void testCacheMultipleInvalidation() {
        final Cache<String, String> cache = this.applicationContext.get(Cache.class);

        cache.put("key", "value");
        cache.put("key2", "value2");

        cache.invalidate("key");
        final Result<String> result = cache.get("key");
        final Result<String> result2 = cache.get("key2");

        Assertions.assertTrue(result.absent());
        Assertions.assertTrue(result2.present());
        Assertions.assertEquals("value2", result2.get());
    }

    @Test
    void testCacheInvalidateAll() {
        final Cache<String, String> cache = this.applicationContext.get(Cache.class);

        cache.put("key", "value");
        cache.put("key2", "value2");

        cache.invalidate();
        final Result<String> result = cache.get("key");
        final Result<String> result2 = cache.get("key2");

        Assertions.assertTrue(result.absent());
        Assertions.assertTrue(result2.absent());
    }

    @Test
    void testCacheCanWriteAndReadMultipleWithExpiration() throws InterruptedException {
        final Cache<String, String> cache = this.applicationContext.get(CacheFactory.class).cache(Expiration.of(1, TimeUnit.MILLISECONDS));

        cache.put("key", "value");
        cache.put("key2", "value2");

        Thread.sleep(2);

        final Result<String> result = cache.get("key");
        final Result<String> result2 = cache.get("key2");

        Assertions.assertTrue(result.absent());
        Assertions.assertTrue(result2.absent());
    }

    @Test
    void testCacheCanWriteAndReadMultipleWithExpirationAndInvalidation() throws InterruptedException {
        final Cache<String, String> cache = this.applicationContext.get(CacheFactory.class).cache(Expiration.of(1, TimeUnit.MILLISECONDS));

        cache.put("key", "value");
        cache.put("key2", "value2");

        Thread.sleep(2);

        Assertions.assertDoesNotThrow(() -> cache.invalidate("key"));

        final Result<String> result = cache.get("key");
        final Result<String> result2 = cache.get("key2");

        Assertions.assertTrue(result.absent()); // Through expiration and invalidation
        Assertions.assertTrue(result2.absent()); // Through expiration
    }

    @Test
    void testCacheContainsBeforeAfterInvalidation() {
        final Cache<String, String> cache = this.applicationContext.get(Cache.class);

        cache.put("key", "value");
        Assertions.assertTrue(cache.contains("key"));

        cache.invalidate("key");
        Assertions.assertFalse(cache.contains("key"));
    }

    @Test
    void testCacheContainsBeforeAfterExpiration() throws InterruptedException {
        final Cache<String, String> cache = this.applicationContext.get(CacheFactory.class).cache(Expiration.of(1, TimeUnit.MILLISECONDS));

        cache.put("key", "value");
        Assertions.assertTrue(cache.contains("key"));

        Thread.sleep(2);

        Assertions.assertFalse(cache.contains("key"));
    }

    @Test
    void testCachePutIfAbsent() {
        final Cache<String, String> cache = this.applicationContext.get(Cache.class);

        cache.put("key", "value");
        Assertions.assertTrue(cache.contains("key"));

        cache.putIfAbsent("key", "value2");
        Assertions.assertTrue(cache.contains("key"));
        Assertions.assertEquals("value", cache.get("key").get());
    }
}
