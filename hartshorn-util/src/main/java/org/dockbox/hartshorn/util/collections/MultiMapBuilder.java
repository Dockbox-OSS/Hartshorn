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
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class MultiMapBuilder<K, V> {

    private Supplier<Map<K, Collection<V>>> mapSupplier;
    private Supplier<Collection<V>> collectionSupplier;
    private boolean makeSynchronized = false;
    private boolean makeConcurrent = false;

    public MultiMapBuilder<K, V> mapSupplier(final Supplier<Map<K, Collection<V>>> mapSupplier) {
        this.mapSupplier = mapSupplier;
        return this;
    }

    public MultiMapBuilder<K, V> collectionSupplier(final Supplier<Collection<V>> collectionSupplier) {
        this.collectionSupplier = collectionSupplier;
        return this;
    }

    public MultiMapBuilder<K, V> makeSynchronized(final boolean makeSynchronized) {
        this.makeSynchronized = makeSynchronized;
        return this;
    }

    public MultiMapBuilder<K, V> makeConcurrent(final boolean makeConcurrent) {
        this.makeConcurrent = makeConcurrent;
        return this;
    }

    public MultiMap<K, V> build() {
        if (this.mapSupplier == null) throw new IllegalArgumentException("Cannot build new MultiMap without configuring Map<K, V> supplier.");
        if (this.collectionSupplier == null) throw new IllegalArgumentException("Cannot build new MultiMap without configuring Collection<V> supplier.");
        if (this.makeSynchronized && this.makeConcurrent) throw new IllegalArgumentException("Cannot build new MultiMap with both synchronized and concurrent capabilities.");

        final Map<K, Collection<V>> baseMap = this.mapSupplier.get();
        if (this.makeSynchronized) {
            return new SynchronizedMultiMap<>() {
                @Override
                protected Collection<V> createEmptyCollection() {
                    return MultiMapBuilder.this.collectionSupplier.get();
                }

                @Override
                protected Map<K, Collection<V>> createEmptyMap() {
                    return baseMap;
                }
            };
        } else if (this.makeConcurrent) {
            if (!(baseMap instanceof ConcurrentMap)) {
                throw new IllegalArgumentException("Cannot build new ConcurrentMultiMap with base map that is not a ConcurrentMap.");
            }
            return new StandardMultiMap<>() {
                @Override
                protected Collection<V> createEmptyCollection() {
                    return MultiMapBuilder.this.collectionSupplier.get();
                }

                @Override
                protected Map<K, Collection<V>> createEmptyMap() {
                    return baseMap;
                }
            };
        } else {
            return new StandardMultiMap<>() {
                @Override
                protected Collection<V> createEmptyCollection() {
                    return MultiMapBuilder.this.collectionSupplier.get();
                }

                @Override
                protected Map<K, Collection<V>> createEmptyMap() {
                    return baseMap;
                }
            };
        }
    }
}
