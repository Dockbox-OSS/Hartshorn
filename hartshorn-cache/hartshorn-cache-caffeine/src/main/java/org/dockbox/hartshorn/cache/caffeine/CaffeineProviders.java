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

package org.dockbox.hartshorn.cache.caffeine;

import org.dockbox.hartshorn.cache.Cache;
import org.dockbox.hartshorn.cache.CacheManager;
import org.dockbox.hartshorn.cache.annotations.UseCaching;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.condition.RequiresClass;
import org.dockbox.hartshorn.component.processing.Binds;

/**
 * The provider configuration for Caffeine caches.
 * @author Guus Lieben
 * @since 22.4
 */
@Service
@RequiresActivator(UseCaching.class)
@RequiresClass("com.github.benmanes.caffeine.cache.Caffeine")
public class CaffeineProviders {

    /**
     * The provider for {@link Cache} instances. This uses class-based provision
     * to support both injected and bound provision.
     */
    @Binds
    public Class<? extends Cache> cache = CaffeineCache.class;

    @Binds(priority = 0)
    public CacheManager cacheManager() {
        return new CaffeineCacheManager();
    }
}
