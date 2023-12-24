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

package org.dockbox.hartshorn.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * A simple implementation of {@link MultiMap} that uses {@link ArrayList} as its backing collection,
 * and {@link HashMap} as its backing map. This implementation is not thread-safe.
 *
 * @param <K> the type of the keys
 * @param <V> the type of the values
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ArrayListMultiMap<K, V> extends HashMultiMap<K, V> {

    public ArrayListMultiMap() {
    }

    public ArrayListMultiMap(MultiMap<K, V> map) {
        super(map);
    }

    @Override
    protected Collection<V> createEmptyCollection() {
        return new ArrayList<>();
    }
}
