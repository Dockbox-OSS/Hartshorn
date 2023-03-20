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

package test.org.dockbox.hartshorn.cache.caffeine;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.cache.CacheManager;
import org.dockbox.hartshorn.cache.caffeine.CaffeineCacheManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

import test.org.dockbox.hartshorn.cache.CacheServiceTests;

public class CaffeineCacheServiceTests extends CacheServiceTests {

    private CacheManager cacheManager;

    @AfterEach
    public void tearDown() {
        this.cacheManager = null;
    }

    @Override
    protected CacheManager cacheManager(final ApplicationContext applicationContext) {
        if (this.cacheManager == null) {
            // Singleton, so expected to already be cached in the application context
            this.cacheManager = applicationContext.get(CacheManager.class);
            Assertions.assertTrue(this.cacheManager instanceof CaffeineCacheManager);
        }
        return this.cacheManager;
    }
}
