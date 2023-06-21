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

package org.dockbox.hartshorn.cache;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.cache.annotations.UseCaching;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Binds;

import jakarta.inject.Singleton;

/**
 * Default providers for cache components. This implementation is active by
 * default when {@link UseCaching} is used.
 *
 * @author Guus Lieben
 * @since 21.2
 */
@Service
@RequiresActivator(UseCaching.class)
public class CacheProviders {

    /**
     * The default binding for {@link CacheManager}. This implementation is
     * active by default when {@link UseCaching} is used.
     */
    @Binds
    @Singleton
    public CacheManager cacheManager(final ApplicationContext applicationContext) {
        return new SimpleCacheManager(applicationContext);
    }

    /**
     * The default binding for {@link KeyGenerator}. This implementation is
     * active by default when {@link UseCaching} is used.
     * @return {@link HashCodeKeyGenerator}
     */
    @Binds
    public KeyGenerator keyGenerator() {
        return new HashCodeKeyGenerator();
    }
}
