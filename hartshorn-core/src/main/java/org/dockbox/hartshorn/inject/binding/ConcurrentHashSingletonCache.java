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

package org.dockbox.hartshorn.inject.binding;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.IllegalModificationException;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A singleton cache implementation that uses a {@link ConcurrentHashMap} to store
 * instances. This implementation is thread-safe.
 *
 * @author Guus Lieben
 * @since 0.4.11
 *
 * @see SingletonCache
 * @see ConcurrentHashMap
 */
public class ConcurrentHashSingletonCache implements SingletonCache {

    private final Map<ComponentKey<?>, Object> cache = new ConcurrentHashMap<>();

    @Override
    public <T> void put(ComponentKey<T> key, T instance) {
        if (this.cache.containsKey(key) && this.cache.get(key) != instance) {
            throw new IllegalModificationException("An instance is already stored for key '" + key + "'");
        }
        this.cache.put(key, instance);
    }

    @Override
    public <T> Option<T> get(ComponentKey<T> key) {
        return Option.of(key.type().cast(this.cache.get(key)));
    }

    @Override
    public <T> boolean contains(ComponentKey<T> key) {
        return this.cache.containsKey(key);
    }

}
