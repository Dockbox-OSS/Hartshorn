/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.util.collections;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@link MultiMap} implementation that uses {@link ConcurrentHashMap} as its backing map, and
 * {@link ConcurrentHashMap#newKeySet()} as the factory for new collections. This implementation
 * is thread-safe.
 *
 * @param <K> the type of the keys
 * @param <V> the type of the values
 *
 * @see ConcurrentHashMap
 * @see MultiMap
 * @see StandardMultiMap
 * @see ConcurrentMultiMap
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ConcurrentSetMultiMap<K, V> extends ConcurrentMultiMap<K, V> {

    public ConcurrentSetMultiMap() {
    }

    public ConcurrentSetMultiMap(MultiMap<K, V> map) {
        super(map);
    }

    @Override
    protected Collection<V> createEmptyCollection() {
        return ConcurrentHashMap.newKeySet();
    }
}
