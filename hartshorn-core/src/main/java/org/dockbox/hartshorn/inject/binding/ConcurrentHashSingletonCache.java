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

import org.dockbox.hartshorn.component.ComponentKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSingletonCache implements SingletonCache {

    private final Map<ComponentKey<?>, Object> cache = new ConcurrentHashMap<>();

    @Override
    public <T> void put(final ComponentKey<T> key, final T instance) {
        this.cache.put(key, instance);
    }

    @Override
    public <T> T get(final ComponentKey<T> key) {
        return key.type().cast(this.cache.get(key));
    }

    @Override
    public <T> void remove(final ComponentKey<T> key) {
        this.cache.remove(key);
    }

    @Override
    public <T> boolean contains(final ComponentKey<T> key) {
        return this.cache.containsKey(key);
    }

    @Override
    public <T> void clear() {
        this.cache.clear();
    }
}
