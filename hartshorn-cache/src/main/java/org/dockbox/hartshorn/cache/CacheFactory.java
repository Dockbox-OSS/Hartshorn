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

/**
 * The factory for {@link Cache} implementations to allow for custom
 * {@link Expiration} values.
 *
 * @see Cache
 * @author Guus Lieben
 * @since 21.2
 */
@FunctionalInterface
public interface CacheFactory {

    /**
     * Creates a new {@link Cache} instance with the given {@link Expiration}.
     * @param expiration the {@link Expiration} to use
     * @return the new {@link Cache} instance
     * @param <K> the type of keys
     * @param <V> the type of values
     */
    <K, V> Cache<K, V> cache(Expiration expiration);
}
