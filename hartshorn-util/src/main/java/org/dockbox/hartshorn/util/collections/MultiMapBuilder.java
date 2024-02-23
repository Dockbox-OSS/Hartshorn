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

/**
 * A builder for {@link MultiMap} instances. This builder allows for the configuration of the
 * {@link Map} and {@link Collection} implementations that are used by the {@link MultiMap} that is
 * built.
 *
 * <p>Additionally, this builder allows for the configuration of the concurrency and synchronization
 * capabilities of the {@link MultiMap} that is built. This expects the backing {@link Map} to be
 * compatible with the concurrency and synchronization capabilities that are configured.
 *
 * @param <K> the type of the keys
 * @param <V> the type of the values
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class MultiMapBuilder<K, V> {

    private Supplier<Map<K, Collection<V>>> mapSupplier;
    private Supplier<Collection<V>> collectionSupplier;
    private boolean makeSynchronized = false;
    private boolean makeConcurrent = false;

    /**
     * Configures the {@link Map} implementation that is used by the {@link MultiMap} that is built. If
     * the created {@link MultiMap} is first accessed, this supplier is invoked to create a new {@link
     * Map} instance. This supplier is expected to return a new, empty {@link Map} instance on each
     * invocation.
     *
     * @param mapSupplier the supplier of the {@link Map} implementation
     * @return this builder
     */
    public MultiMapBuilder<K, V> mapSupplier(Supplier<Map<K, Collection<V>>> mapSupplier) {
        this.mapSupplier = mapSupplier;
        return this;
    }

    /**
     * Configures the {@link Collection} implementation that is used by the {@link MultiMap} that is
     * built. When a key is first added or resolved, this supplier is invoked to create a new {@link
     * Collection} instance. This supplier is expected to return a new, empty {@link Collection}
     * instance on each invocation.
     *
     * @param collectionSupplier the supplier of the {@link Collection} implementation
     * @return this builder
     */
    public MultiMapBuilder<K, V> collectionSupplier(Supplier<Collection<V>> collectionSupplier) {
        this.collectionSupplier = collectionSupplier;
        return this;
    }

    /**
     * Configures the {@link MultiMap} that is built to be synchronized. This means that all access to
     * the {@link MultiMap} is synchronized. This is useful when the {@link MultiMap} is accessed
     * concurrently by multiple threads, and it is not possible to synchronize the access externally.
     *
     * <p>Note that the backing {@link Map} is expected to be compatible with synchronization.
     *
     * @param makeSynchronized {@code true} to make the {@link MultiMap} synchronized, else {@code false}
     * @return this builder
     */
    public MultiMapBuilder<K, V> makeSynchronized(boolean makeSynchronized) {
        this.makeSynchronized = makeSynchronized;
        return this;
    }

    /**
     * Indicates that the {@link MultiMap} that is built should be concurrent. This means that the backing
     * {@link Map} is expected to be a {@link ConcurrentMap}.
     *
     * @param makeConcurrent {@code true} to make the {@link MultiMap} concurrent, else {@code false}
     * @return this builder
     */
    public MultiMapBuilder<K, V> makeConcurrent(boolean makeConcurrent) {
        this.makeConcurrent = makeConcurrent;
        return this;
    }

    /**
     * Builds a new {@link MultiMap} instance based on the configuration of this builder.
     *
     * @return the new {@link MultiMap} instance
     */
    public MultiMap<K, V> build() {
        if (this.mapSupplier == null) {
            throw new IllegalArgumentException("Cannot build new MultiMap without configuring Map<K, V> supplier.");
        }
        if (this.collectionSupplier == null) {
            throw new IllegalArgumentException("Cannot build new MultiMap without configuring Collection<V> supplier.");
        }
        if (this.makeSynchronized && this.makeConcurrent) {
            throw new IllegalArgumentException("Cannot build new MultiMap with both synchronized and concurrent capabilities.");
        }

        Map<K, Collection<V>> baseMap = this.mapSupplier.get();
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
