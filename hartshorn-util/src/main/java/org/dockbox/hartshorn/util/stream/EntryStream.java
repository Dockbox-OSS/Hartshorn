package org.dockbox.hartshorn.util.stream;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class EntryStream<K, V> extends AbstractDelegateStream<Entry<K, V>> {

    public static <K, V> EntryStream<K, V> of(Map.Entry<K, V> entry) {
        return new EntryStream<>(Stream.of(entry));
    }

    @SafeVarargs
    public static <K, V> EntryStream<K, V> of(Map.Entry<K, V>... entries) {
        return new EntryStream<>(Stream.of(entries));
    }

    public static <K, V> EntryStream<K, V> of(Collection<Map.Entry<K, V>> entries) {
        return new EntryStream<>(entries.stream());
    }

    public static <K, V> EntryStream<K, V> of(Map<K, V> map) {
        return new EntryStream<>(map.entrySet().stream());
    }

    public static <K, V> EntryStream<K, V> of(Stream<Map.Entry<K, V>> stream) {
        return new EntryStream<>(stream);
    }

    public static <K, V> EntryStream<K, V> ofKeys(Stream<K> keys, Function<K, V> keyValueMapper) {
        return new EntryStream<>(keys.map(key -> Map.entry(key, keyValueMapper.apply(key))));
    }

    public static <K, V> EntryStream<K, V> ofValues(Stream<V> values, Function<V, K> keyMapper) {
        return new EntryStream<>(values.map(value -> Map.entry(keyMapper.apply(value), value)));
    }

    public static <T, K, V> EntryStream<K, V> of(Stream<T> stream, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return new EntryStream<>(stream.map(element -> Map.entry(keyMapper.apply(element), valueMapper.apply(element))));
    }

    public static <K, V> EntryStream<K, V> empty() {
        return new EntryStream<>(Stream.empty());
    }

    protected EntryStream(Stream<Entry<K, V>> delegate) {
        super(delegate);
    }

    public EntryStream<K, V> filterKeys(Function<K, Boolean> filter) {
        return this.filter(entry -> filter.apply(entry.getKey()));
    }

    public EntryStream<K, V> filterValues(Function<V, Boolean> filter) {
        return this.filter(entry -> filter.apply(entry.getValue()));
    }

    public EntryStream<K, V> filter(BiPredicate<K, V> filter) {
        return this.filter(entry -> filter.test(entry.getKey(), entry.getValue()));
    }

    public EntryStream<K, V> peekKeys(Consumer<K> action) {
        return this.peek(entry -> action.accept(entry.getKey()));
    }

    public EntryStream<K, V> peekValues(Consumer<V> action) {
        return this.peek(entry -> action.accept(entry.getValue()));
    }

    public EntryStream<K, V> peek(BiConsumer<K, V> action) {
        return this.peek(entry -> action.accept(entry.getKey(), entry.getValue()));
    }

    public <R> EntryStream<R, V> mapKeys(Function<K, R> mapper) {
        return EntryStream.of(this.map(entry -> Map.entry(
                mapper.apply(entry.getKey()),
                entry.getValue()
        )));
    }

    public <R> EntryStream<K, R> mapValues(Function<V, R> mapper) {
        return EntryStream.of(this.map(entry -> Map.entry(
                entry.getKey(),
                mapper.apply(entry.getValue())
        )));
    }

    public <RK, RV> EntryStream<RK, RV> map(Function<K, RK> keyMapper, Function<V, RV> valueMapper) {
        return EntryStream.of(this.map(entry -> Map.entry(
                keyMapper.apply(entry.getKey()),
                valueMapper.apply(entry.getValue())
        )));
    }

    public <R> Stream<R> map(BiFunction<K, V, R> function) {
        return this.map(entry -> function.apply(entry.getKey(), entry.getValue()));
    }

    public <R> EntryStream<R, V> flatMapKeys(Function<K, Stream<R>> mapper) {
        return EntryStream.of(this.flatMap(entry -> mapper
                .apply(entry.getKey())
                .map(key -> Map.entry(key, entry.getValue()))
        ));
    }

    public <R> EntryStream<K, R> flatMapValues(Function<V, Stream<R>> mapper) {
        return EntryStream.of(this.flatMap(entry -> mapper
                .apply(entry.getValue())
                .map(value -> Map.entry(entry.getKey(), value))
        ));
    }

    public EntryStream<K, V> sortedKeys() {
        return EntryStream.of(this.sorted(Comparator.comparingInt(entry -> entry.getKey().hashCode())));
    }

    public EntryStream<K, V> sortedValues() {
        return EntryStream.of(this.sorted(Comparator.comparingInt(entry -> entry.getValue().hashCode())));
    }

    public EntryStream<K, V> sortedKeys(Comparator<K> comparator) {
        return EntryStream.of(this.sorted((entry1, entry2) -> comparator.compare(entry1.getKey(), entry2.getKey())));
    }

    public EntryStream<K, V> sortedValues(Comparator<V> comparator) {
        return EntryStream.of(this.sorted((entry1, entry2) -> comparator.compare(entry1.getValue(), entry2.getValue())));
    }

    public EntryStream<K, V> sorted(BiComparator<K, V> comparator) {
        return EntryStream.of(this.sorted((entry1, entry2) -> {
            int result = comparator.compare(entry1.getKey(), entry1.getValue(), entry2.getKey(), entry2.getValue());
            if (result == 0) {
                return entry1.getKey().hashCode() - entry2.getKey().hashCode();
            }
            return result;
        }));
    }

    public Stream<K> keys() {
        return this.map(Entry::getKey);
    }

    public Stream<V> values() {
        return this.map(Entry::getValue);
    }

    @Override
    public EntryStream<K, V> filter(Predicate<? super Entry<K, V>> predicate) {
        return (EntryStream<K, V>) super.filter(predicate);
    }

    @Override
    public EntryStream<K, V> peek(Consumer<? super Entry<K, V>> action) {
        return (EntryStream<K, V>) super.peek(action);
    }

    @Override
    public EntryStream<K, V> sorted(Comparator<? super Entry<K, V>> comparator) {
        return (EntryStream<K, V>) super.sorted(comparator);
    }

    @Override
    public EntryStream<K, V> sorted() {
        return (EntryStream<K, V>) super.sorted();
    }

    @Override
    public EntryStream<K, V> distinct() {
        return (EntryStream<K, V>) super.distinct();
    }

    @Override
    public EntryStream<K, V> skip(long n) {
        return (EntryStream<K, V>) super.skip(n);
    }

    @Override
    public EntryStream<K, V> limit(long maxSize) {
        return (EntryStream<K, V>) super.limit(maxSize);
    }

    @Override
    public EntryStream<K, V> sequential() {
        return (EntryStream<K, V>) super.sequential();
    }

    @Override
    public EntryStream<K, V> onClose(Runnable closeHandler) {
        return (EntryStream<K, V>) super.onClose(closeHandler);
    }

    @Override
    public EntryStream<K, V> unordered() {
        return (EntryStream<K, V>) super.unordered();
    }

    @Override
    public EntryStream<K, V> parallel() {
        return (EntryStream<K, V>) super.parallel();
    }
}
