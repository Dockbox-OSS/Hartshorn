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

import org.dockbox.hartshorn.cache.annotations.CacheService;
import org.dockbox.hartshorn.cache.annotations.Cached;
import org.dockbox.hartshorn.cache.annotations.EvictCache;
import org.dockbox.hartshorn.cache.annotations.UpdateCache;

@CacheService(TestCacheService.CLASS_CACHE_KEY)
public abstract class TestCacheService {

    public static final String METHOD_CACHE_KEY = "sampleMethodKey";
    public static final String CLASS_CACHE_KEY = "sampleClassKey";

    @Cached(key = METHOD_CACHE_KEY)
    public long getCachedTime() {
        // Return nanoseconds, as some tests evict and request within 1ms.
        return System.nanoTime();
    }

    @UpdateCache(key = METHOD_CACHE_KEY)
    public abstract void update(long number);

    @EvictCache(key = METHOD_CACHE_KEY)
    public abstract void evict();

}
