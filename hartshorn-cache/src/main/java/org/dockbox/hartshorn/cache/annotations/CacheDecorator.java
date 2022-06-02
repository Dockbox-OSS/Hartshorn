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

package org.dockbox.hartshorn.cache.annotations;

import org.dockbox.hartshorn.cache.KeyGenerator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Synthetic parent annotation for cache annotations.
 * @see Cached
 * @see EvictCache
 * @see UpdateCache
 *
 * @author Guus Lieben
 * @since 22.4
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheDecorator {

    /**
     * The ID of the target cache. If this is left empty a name will be
     * generated based on the owning service.
     *
     * @return the cache ID
     */
    String cacheName() default "";

    /**
     * The type of the key generator to use. The key generator is responsible for
     * turning method signatures into cache keys. The default key generator is
     * based on the binding of {@link KeyGenerator} so it blends in with the active
     * application configuration.
     *
     * <p>This will only be used when {@link #key()} is left empty.
     *
     * @return the key generator type
     */
    Class<? extends KeyGenerator> keyGenerator() default KeyGenerator.class;

    /**
     *The cache key to use. If this is left empty a key will be generated based on
     * the method signature.
     *
     * @return the cache key
     */
    String key() default "";
}
