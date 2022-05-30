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

package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.inject.Key;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSingletonCache implements SingletonCache {

    private final Map<Key<?>, Object> cache = new ConcurrentHashMap<>();

    @Override
    public <T> void put(final Key<T> key, final T instance) {
        this.cache.put(key, instance);
    }

    @Override
    public <T> T get(final Key<T> key) {
        final Object object = this.cache.get(key);
        if (object == null) {
            return null;
        }
        return (T) object;
    }

    @Override
    public <T> void remove(final Key<T> key) {
        this.cache.remove(key);
    }

    @Override
    public <T> boolean contains(final Key<T> key) {
        return this.cache.containsKey(key);
    }

    @Override
    public <T> void clear() {
        this.cache.clear();
    }
}
