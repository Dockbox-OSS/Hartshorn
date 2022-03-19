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

package org.dockbox.hartshorn.util;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.function.Supplier;

public class CustomMultiMap<K, V> extends MultiMap<K, V> {

    private final Supplier<Collection<V>> baseCollection;

    public CustomMultiMap(final Supplier<Collection<V>> baseCollection) {
        this.baseCollection = baseCollection;
    }

    public <T> CustomMultiMap(final Supplier<Collection<V>> baseCollection, final MultiMap<K, V> map) {
        this.baseCollection = baseCollection;
        for (final Entry<K, Collection<V>> collection : map.entrySet()) {
            this.putAll(collection.getKey(), collection.getValue());
        }
    }

    @Override
    protected Collection<V> baseCollection() {
        return this.baseCollection.get();
    }
}
