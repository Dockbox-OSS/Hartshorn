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

package org.dockbox.hartshorn.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class SynchronizedMultiMap<K, V> extends AbstractMultiMap<K, V> {

    private transient Map<K, Collection<V>> map;

    public SynchronizedMultiMap() {
    }

    public SynchronizedMultiMap(final MultiMap<K, V> map) {
        super(map);
    }

    @Override
    protected synchronized Map<K, Collection<V>> map() {
        synchronized (this) {
            if (this.map == null) {
                this.map = createEmptyMap();
            }
            return this.map;
        }
    }

    protected Map<K, Collection<V>> createEmptyMap() {
        return new HashMap<>();
    }

    public static class SynchronizedArrayListMultiMap<K, V> extends SynchronizedMultiMap<K, V> {

        public SynchronizedArrayListMultiMap() {
        }

        public SynchronizedArrayListMultiMap(final MultiMap<K, V> map) {
            super(map);
        }

        @Override
        protected Collection<V> createEmptyCollection() {
            return new ArrayList<>();
        }
    }

    public static class SynchronizedHashSetMultiMap<K, V> extends SynchronizedMultiMap<K, V> {

        public SynchronizedHashSetMultiMap() {
        }

        public SynchronizedHashSetMultiMap(final MultiMap<K, V> map) {
            super(map);
        }

        @Override
        protected Collection<V> createEmptyCollection() {
            return new HashSet<>();
        }
    }

}
