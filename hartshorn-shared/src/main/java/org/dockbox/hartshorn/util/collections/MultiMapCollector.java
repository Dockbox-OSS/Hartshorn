/*
 * Copyright 2019-2025 the original author or authors.
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

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * A {@link Collector} that collects elements into a {@link MultiMap}. This collector does not
 * ensure that the resulting values of the {@link MultiMap} are ordered, unless the {@link MultiMap}
 * implementation itself does. Similarly, the resulting {@link MultiMap} may not be thread-safe,
 * unless the {@link MultiMap} implementation itself is.
 *
 * <p>All resulting {@link MultiMap} instances are wrapped in a {@link UnmodifiableMultiMap}, even
 * if a custom {@link MultiMap} implementation is used.
 *
 * @param supplier A supplier that creates a new {@link MultiMap} instance
 * @param keyMapper A function that maps elements to keys
 * @param valueMapper A function that maps elements to values
 * @param finisher A function that transforms the intermediate {@link MultiMap} into the final
 * resulting {@link MultiMap}
 * @param <T> The type of the elements to collect
 * @param <K> The type of the keys in the resulting {@link MultiMap}
 * @param <V> The type of the values in the resulting {@link MultiMap}
 * @param <M> The type of the intermediate {@link MultiMap} used during collection
 * @param <F> The type of the resulting {@link MultiMap}
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public record MultiMapCollector<T, K, V, M extends MultiMap<K, V>, F extends MultiMap<K, V>>(
    Supplier<M> supplier,
    Function<T, K> keyMapper,
    Function<T, V> valueMapper,
    Function<M, F> finisher
) implements Collector<T, M, F> {

    /**
     * A collector that collects entries into a {@link MultiMap}. The default implementation is an
     * {@link ArrayListMultiMap}.
     *
     * @param <K> the key type
     * @param <V> the value type
     *
     * @return a collector that collects entries into a {@link MultiMap}
     */
    public static <K, V> Collector<Map.Entry<K, V>, ?, MultiMap<K, V>> toMultiMap() {
        return toMultiMap(ArrayListMultiMap::new, Map.Entry::getKey, Map.Entry::getValue);
    }

    /**
     * {@link MultiMap} equivalent of {@link java.util.stream.Collectors#groupingBy(Function)}.
     *
     * @param keyMapper the classifier function to determine the key for each element
     * @param <T> the type of the elements to collect
     * @param <K> the type of the keys in the resulting {@link MultiMap}
     *
     * @return a {@link MultiMapCollector} that collects elements into a {@link MultiMap}
     */
    public static <T, K> MultiMapCollector<T, K, T, MultiMap<K, T>, MultiMap<K, T>> groupingBy(
        Function<T, K> keyMapper
    ) {
        return toMultiMap(keyMapper, Function.identity());
    }

    /**
     * {@link MultiMap} equivalent of {@link java.util.stream.Collectors#toMap(Function, Function)},
     * allowing for duplicate keys by collecting values into a {@link MultiMap}.
     *
     * @param keyMapper a mapper function to determine the key for each element
     * @param valueMapper a mapper function to determine the value for each element
     * @param <T> the type of the elements to collect
     * @param <K> the type of the keys in the resulting {@link MultiMap}
     * @param <V> the type of the values in the resulting {@link MultiMap}
     *
     * @return a {@link MultiMapCollector} that collects elements into a {@link MultiMap}
     */
    public static <T, K, V> MultiMapCollector<T, K, V, MultiMap<K, V>, MultiMap<K, V>> toMultiMap(
        Function<T, K> keyMapper,
        Function<T, V> valueMapper
    ) {
        return toMultiMap(
            ArrayListHashBiMultiMap::new,
            keyMapper,
            valueMapper,
            UnmodifiableMultiMap::new
        );
    }

    /**
     * Creates a {@link MultiMapCollector} that collects elements into a {@link MultiMap} using the
     * provided {@link MultiMap} supplier, key mapper, and value mapper.
     *
     * @param mapSupplier a supplier that creates a new {@link MultiMap} instance
     * @param keyMapper a function that maps elements to keys
     * @param valueMapper a function that maps elements to values
     * @param <T> the type of the elements to collect
     * @param <K> the type of the keys in the resulting {@link MultiMap}
     * @param <V> the type of the values in the resulting {@link MultiMap}
     *
     * @return a {@link MultiMapCollector} that collects elements into a {@link MultiMap}
     */
    public static <T, K, V, M extends MultiMap<K, V>>
    MultiMapCollector<T, K, V, M, M> toMultiMap(
        Supplier<M> mapSupplier,
        Function<T, K> keyMapper,
        Function<T, V> valueMapper
    ) {
        return toMultiMap(mapSupplier, keyMapper, valueMapper, Function.identity());
    }

    /**
     * Creates a {@link MultiMapCollector} that collects elements into a {@link MultiMap} using the
     * provided {@link MultiMap} supplier, key mapper, value mapper, and finisher function.
     *
     * @param mapSupplier a supplier that creates a new intermediate {@link MultiMap} instance
     * @param keyMapper a function that maps elements to keys
     * @param valueMapper a function that maps elements to values
     * @param finisher a function that transforms the intermediate {@link MultiMap} into the final
     * resulting {@link MultiMap}
     * @param <T> the type of the elements to collect
     * @param <K> the type of the keys in the resulting {@link MultiMap}
     * @param <V> the type of the values in the resulting {@link MultiMap}
     * @param <M> the type of the intermediate {@link MultiMap} used during collection
     * @param <F> the type of the resulting {@link MultiMap}
     *
     * @return a {@link MultiMapCollector} that collects elements into a {@link MultiMap}
     */
    public static <T, K, V, M extends MultiMap<K, V>, F extends MultiMap<K, V>>
    MultiMapCollector<T, K, V, M, F> toMultiMap(
        Supplier<M> mapSupplier,
        Function<T, K> keyMapper,
        Function<T, V> valueMapper,
        Function<M, F> finisher
    ) {
        return new MultiMapCollector<>(mapSupplier, keyMapper, valueMapper, finisher);
    }

    @Override
    public BiConsumer<M, T> accumulator() {
        return (map, element) -> map.put(
            this.keyMapper().apply(element),
            this.valueMapper().apply(element)
        );
    }

    @Override
    public BinaryOperator<M> combiner() {
        return (left, right) -> {
            left.putAll(right);
            return left;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED, Characteristics.CONCURRENT);
    }
}
