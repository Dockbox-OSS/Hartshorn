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

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

public abstract class AbstractBiMultiMap<K, V> extends StandardMultiMap<K, V> implements BiMultiMap<K, V> {

    protected AbstractBiMultiMap() {
    }

    protected AbstractBiMultiMap(final MultiMap<K, V> map) {
        super(map);
    }

    @Override
    public MultiMap<V, K> inverse() {
        final Set<Entry<K, Collection<V>>> entries = this.entrySet();
        final MultiMap<V, K> inverseMap = this.createEmptyInverseMap();
        for (final Entry<K, Collection<V>> entry : entries) {
            final K key = entry.getKey();
            final Collection<V> values = entry.getValue();
            for (final V value : values) {
                inverseMap.put(value, key);
            }
        }
        return inverseMap;
    }

    protected abstract MultiMap<V, K> createEmptyInverseMap();
}
