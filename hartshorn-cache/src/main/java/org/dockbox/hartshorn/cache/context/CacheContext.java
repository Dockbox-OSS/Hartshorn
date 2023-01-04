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

package org.dockbox.hartshorn.cache.context;

import org.dockbox.hartshorn.cache.Cache;
import org.dockbox.hartshorn.cache.CacheManager;
import org.dockbox.hartshorn.cache.KeyGenerator;

/**
 * Context carrier for cache service modifiers, indicating the {@link CacheManager},
 * {@link Cache}, and cache ID to use. These are typically derived from
 * {@link CacheMethodContext} during processing. {@link Cache Caches} in
 * this context always use {@link String} as their key type.
 *
 * @author Guus Lieben
 * @since 21.2
 */
public interface CacheContext {

    /**
     * The {@link CacheManager} to use, if any.
     */
    CacheManager manager();

    /**
     * The {@link Cache} to use, if any.
     */
    <T> Cache<String, T> cache();

    /**
     * The cache ID to use, if any.
     */
    String cacheName();

    /**
     * The cache key to use, if any. The cache key represents the key of the
     * method signature as determined by the active {@link KeyGenerator}.
     */
    String key();

}
