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
import java.util.Map;

public class ArrayListHashBiMultiMap<K, V> extends AbstractBiMultiMap<K, V> {

    public ArrayListHashBiMultiMap() {
    }

    public ArrayListHashBiMultiMap(MultiMap<K, V> map) {
        super(map);
    }

    @Override
    protected MultiMap<V, K> createEmptyInverseMap() {
        return new ArrayListMultiMap<>();
    }

    @Override
    protected Collection<V> createEmptyCollection() {
        return new ArrayList<>();
    }

    @Override
    protected Map<K, Collection<V>> createEmptyMap() {
        return new HashMap<>();
    }
}
