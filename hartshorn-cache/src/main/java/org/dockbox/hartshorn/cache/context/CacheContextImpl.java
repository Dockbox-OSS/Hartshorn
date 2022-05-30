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

package org.dockbox.hartshorn.cache.context;

import org.dockbox.hartshorn.cache.Cache;
import org.dockbox.hartshorn.cache.CacheManager;

import java.util.function.Supplier;

/**
 * Default implementation of {@link CacheContext}.
 *
 * @see CacheContext
 */
public class CacheContextImpl implements CacheContext {

    private final CacheManager manager;
    private final String name;
    private final Supplier<Cache<?>> supplier;

    public CacheContextImpl(final CacheManager manager, final Supplier<Cache<?>> supplier, final String name) {
        this.manager = manager;
        this.name = name;
        this.supplier = supplier;
    }

    public CacheManager manager() {
        return this.manager;
    }

    public String name() {
        return this.name;
    }

    @Override
    public <T> Cache<T> cache() {
        return (Cache<T>) this.supplier.get();
    }
}
