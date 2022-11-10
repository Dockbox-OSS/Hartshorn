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

package test.org.dockbox.hartshorn.cache;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.cache.Cache;
import org.dockbox.hartshorn.cache.CacheFactory;
import org.dockbox.hartshorn.cache.Expiration;
import org.dockbox.hartshorn.cache.annotations.UseCaching;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import jakarta.inject.Inject;

@UseCaching
@HartshornTest(includeBasePackages = false)
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
        final Option<String> result = cache.get("key");

        Assertions.assertTrue(result.present());
        Assertions.assertEquals("value", result.get());
    }

    @Test
    void testCacheInvalidation() {
        final Cache<String, String> cache = this.applicationContext.get(Cache.class);

        cache.put("key", "value");
        cache.invalidate("key");
        final Option<String> result = cache.get("key");

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
        final Option<String> result = cache.get("key");
        final Option<String> result2 = cache.get("key2");

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
        final Option<String> result = cache.get("key");
        final Option<String> result2 = cache.get("key2");

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
        final Option<String> result = cache.get("key");
        final Option<String> result2 = cache.get("key2");

        Assertions.assertTrue(result.absent());
        Assertions.assertTrue(result2.absent());
    }

    @Test
    void testCacheCanWriteAndReadMultipleWithExpiration() throws InterruptedException {
        final Cache<String, String> cache = this.applicationContext.get(CacheFactory.class)
                // Would be nice to use a lower amount here, but slower devices and CI environments
                // may not be able to keep up with 1ms reliably
                .cache(Expiration.of(10, TimeUnit.MILLISECONDS));

        cache.put("key", "value");
        cache.put("key2", "value2");

        Thread.sleep(15);

        final Option<String> result = cache.get("key");
        final Option<String> result2 = cache.get("key2");

        Assertions.assertTrue(result.absent());
        Assertions.assertTrue(result2.absent());
    }

    @Test
    void testCacheCanWriteAndReadMultipleWithExpirationAndInvalidation() throws InterruptedException {
        final Cache<String, String> cache = this.applicationContext.get(CacheFactory.class)
                // Would be nice to use a lower amount here, but slower devices and CI environments
                // may not be able to keep up with 1ms reliably
                .cache(Expiration.of(10, TimeUnit.MILLISECONDS));

        cache.put("key", "value");
        cache.put("key2", "value2");

        Thread.sleep(15);

        Assertions.assertDoesNotThrow(() -> cache.invalidate("key"));

        final Option<String> result = cache.get("key");
        final Option<String> result2 = cache.get("key2");

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
        final Cache<String, String> cache = this.applicationContext.get(CacheFactory.class)
                // Would be nice to use a lower amount here, but slower devices and CI environments
                // may not be able to keep up with 1ms reliably
                .cache(Expiration.of(10, TimeUnit.MILLISECONDS));

        cache.put("key", "value");
        Assertions.assertTrue(cache.contains("key"));

        Thread.sleep(15);

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

    @Test
    void testNullExpirationIsRejected() {
        final CacheFactory cacheFactory = this.applicationContext.get(CacheFactory.class);
        Assertions.assertThrows(NullPointerException.class, () -> cacheFactory.cache(null));
    }

    @Test
    void testNeverExpirationIsAccepted() {
        final CacheFactory cacheFactory = this.applicationContext.get(CacheFactory.class);
        Assertions.assertDoesNotThrow(() -> cacheFactory.cache(Expiration.never()));
    }
}
