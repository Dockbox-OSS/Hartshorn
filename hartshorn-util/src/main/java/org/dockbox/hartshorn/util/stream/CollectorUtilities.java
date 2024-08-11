package org.dockbox.hartshorn.util.stream;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.dockbox.hartshorn.util.collections.ArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;

public record CollectorUtilities<T, A, R>(
        Supplier<A> supplier,
        BiConsumer<A, T> accumulator,
        BinaryOperator<A> combiner,
        Function<A, R> finisher,
        Set<Characteristics> characteristics
) implements Collector<T, A, R> {

    CollectorUtilities(
            Supplier<A> supplier,
            BiConsumer<A, T> accumulator,
            BinaryOperator<A> combiner
    ) {
        this(
                supplier,
                accumulator,
                combiner,
                a -> (R) a,
                EnumSet.of(Characteristics.IDENTITY_FINISH)
        );
    }

    public static <K, V> Collector<Entry<K, V>, ?, MultiMap<K, V>> toMultiMap() {
        return toMultiMap(ArrayListMultiMap::new, Entry::getKey, Entry::getValue);
    }

    public static <T, K, V> Collector<T, ?, MultiMap<K, V>> toMultiMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper
    ) {
        return toMultiMap(ArrayListMultiMap::new, keyMapper, valueMapper);
    }

    public static <T, K, V, M extends MultiMap<K, V>> Collector<T, ?, M> toMultiMap(
            Supplier<M> supplier,
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper
    ) {
        return new CollectorUtilities<>(
                supplier,
                (map, next) -> map.put(keyMapper.apply(next), valueMapper.apply(next)),
                (left, right) -> {
                    left.putAll(right);
                    return left;
                });
    }

    // TODO: Documentation. Bit of a hack while we wait for JEP 473 (Stream gatherers) to be released.
    public static <K, V> Collector<Entry<K, V>, ?, EntryStream<K, V>> toEntryStream() {
        return toEntryStream(HashMap::new, Entry::getKey, Entry::getValue);
    }

    public static <T, K, V> Collector<T, ?, EntryStream<K, V>> toEntryStream(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper
    ) {
        return toEntryStream(HashMap::new, keyMapper, valueMapper);
    }

    public static <T, K, V> Collector<T, ?, EntryStream<K, V>> toEntryStream(
            Supplier<Map<K, V>> mapSupplier,
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper
    ) {
        return new CollectorUtilities<>(
                mapSupplier,
                (map, next) -> map.put(keyMapper.apply(next), valueMapper.apply(next)),
                (left, right) -> {
                    left.putAll(right);
                    return left;
                },
                EntryStream::of,
                EnumSet.of(Characteristics.IDENTITY_FINISH)
        );
    }
}
