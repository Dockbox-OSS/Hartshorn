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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A stream of {@link Map.Entry} elements. This class provides additional functionality for working with
 * {@link Map.Entry} elements in a stream, such as filtering, mapping, and sorting. This type is functionally
 * equivalent to a {@link Stream} of {@link Map.Entry} elements, but provides additional methods that are specific to
 * {@link Map.Entry} elements.
 *
 * <p>Entries are not required to be unique, and may contain duplicate keys or values. The order of entries in the
 * stream is not guaranteed, unless explicitly sorted using the provided sorting methods.
 *
 * @param <K> the key type
 * @param <V> the value type
 *
 * @see Stream
 * @see Map.Entry
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class EntryStream<K, V> extends AbstractDelegateStream<Entry<K, V>> {

    /**
     * Creates a new {@link EntryStream} from a single {@link Map.Entry}.
     *
     * @param entry the entry to create the stream from
     * @param <K> the type of the key of the entry
     * @param <V> the type of the value of the entry
     *
     * @return a new {@link EntryStream} containing the given entry
     */
    public static <K, V> EntryStream<K, V> of(Map.Entry<K, V> entry) {
        return new EntryStream<>(Stream.of(entry));
    }

    /**
     * Creates a new {@link EntryStream} from an array of {@link Map.Entry} elements.
     *
     * @param entries the entries to create the stream from
     * @param <K> the type of the key of the entries
     * @param <V> the type of the value of the entries
     *
     * @return a new {@link EntryStream} containing the given entries
     */
    @SafeVarargs
    public static <K, V> EntryStream<K, V> of(Map.Entry<K, V>... entries) {
        return new EntryStream<>(Stream.of(entries));
    }

    /**
     * Creates a new {@link EntryStream} from a collection of {@link Map.Entry} elements.
     *
     * @param entries the entries to create the stream from
     * @param <K> the type of the key of the entries
     * @param <V> the type of the value of the entries
     *
     * @return a new {@link EntryStream} containing the given entries
     */
    public static <K, V> EntryStream<K, V> of(Collection<Map.Entry<K, V>> entries) {
        return new EntryStream<>(entries.stream());
    }

    /**
     * Creates a new {@link EntryStream} from an iterable of {@link Map.Entry} elements.
     *
     * @param entries the entries to create the stream from
     * @param <K> the type of the key of the entries
     * @param <V> the type of the value of the entries
     *
     * @return a new {@link EntryStream} containing the given entries
     */
    public static <K, V> EntryStream<K, V> of(Iterable<Map.Entry<K, V>> entries) {
        return of(StreamSupport.stream(entries.spliterator(), false));
    }

    /**
     * Creates a new {@link EntryStream} from a {@link Map}.
     *
     * @param map the map to create the stream from
     * @param <K> the type of the key of the entries
     * @param <V> the type of the value of the entries
     *
     * @return a new {@link EntryStream} containing the entries of the given map
     */
    public static <K, V> EntryStream<K, V> of(Map<K, V> map) {
        return new EntryStream<>(map.entrySet().stream());
    }

    /**
     * Creates a new {@link EntryStream} from a {@link Stream} of {@link Map.Entry} elements.
     *
     * @param stream the stream to create the entry stream from
     * @param <K> the type of the key of the entries
     * @param <V> the type of the value of the entries
     *
     * @return a new {@link EntryStream} containing the entries from the given stream
     */
    public static <K, V> EntryStream<K, V> of(Stream<Map.Entry<K, V>> stream) {
        return new EntryStream<>(stream);
    }

    /**
     * Creates a new {@link EntryStream} from a {@link Stream} of keys, mapping each key to a value using the provided
     * key-value mapper function.
     *
     * @param keys the stream of keys
     * @param keyValueMapper the function to map each key to a value
     * @param <K> the type of the keys
     * @param <V> the type of the values
     *
     * @return a new {@link EntryStream} containing entries created from the keys and their corresponding values
     */
    public static <K, V> EntryStream<K, V> ofKeys(Stream<K> keys, Function<K, V> keyValueMapper) {
        return new EntryStream<>(keys.map(key -> Map.entry(key, keyValueMapper.apply(key))));
    }

    /**
     * Creates a new {@link EntryStream} from a {@link Stream} of values, mapping each value to a key using the
     * provided key mapper function.
     *
     * @param values the stream of values
     * @param keyMapper the function to map each value to a key
     * @param <K> the type of the keys
     * @param <V> the type of the values
     *
     * @return a new {@link EntryStream} containing entries created from the values and their corresponding keys
     */
    public static <K, V> EntryStream<K, V> ofValues(Stream<V> values, Function<V, K> keyMapper) {
        return new EntryStream<>(values.map(value -> Map.entry(keyMapper.apply(value), value)));
    }

    /**
     * Creates a new {@link EntryStream} from a {@link Stream} of elements, mapping each element to a key and value
     * using the provided key and value mapper functions.
     *
     * @param stream the stream of elements
     * @param keyMapper the function to map each element to a key
     * @param valueMapper the function to map each element to a value
     * @param <T> the type of the elements in the stream
     * @param <K> the type of the keys
     * @param <V> the type of the values
     *
     * @return a new {@link EntryStream} containing entries created from the elements and their corresponding keys and values
     */
    public static <T, K, V> EntryStream<K, V> of(Stream<T> stream, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return new EntryStream<>(stream.map(element -> Map.entry(keyMapper.apply(element), valueMapper.apply(element))));
    }

    /**
     * Creates an empty {@link EntryStream}.
     *
     * @param <K> the type of the keys
     * @param <V> the type of the values
     *
     * @return a new empty {@link EntryStream}
     */
    public static <K, V> EntryStream<K, V> empty() {
        return new EntryStream<>(Stream.empty());
    }

    protected EntryStream(Stream<Entry<K, V>> delegate) {
        super(delegate);
    }

    /**
     * Filters the entries in this stream based on the provided key filter function.
     *
     * @param filter the function to filter keys, returning true for keys that should be included in the stream
     * @return a new {@link EntryStream} containing only the entries with keys that match the filter
     */
    public EntryStream<K, V> filterKeys(Function<K, Boolean> filter) {
        return this.filter(entry -> filter.apply(entry.getKey()));
    }

    /**
     * Filters the entries in this stream based on the provided value filter function.
     *
     * @param filter the function to filter values, returning true for values that should be included in the stream
     * @return a new {@link EntryStream} containing only the entries with values that match the filter
     */
    public EntryStream<K, V> filterValues(Function<V, Boolean> filter) {
        return this.filter(entry -> filter.apply(entry.getValue()));
    }

    /**
     * Filters the entries in this stream based on the provided key-value filter function.
     *
     * @param filter the function to filter entries, returning true for entries that should be included in the stream
     * @return a new {@link EntryStream} containing only the entries that match the filter
     */
    public EntryStream<K, V> filter(BiPredicate<K, V> filter) {
        return this.filter(entry -> filter.test(entry.getKey(), entry.getValue()));
    }

    /**
     * Peeks at the keys of the entries in this stream, applying the provided action to each key.
     *
     * @param action the action to apply to each key
     * @return a new {@link EntryStream} with the same entries, but with the action applied to each key
     */
    public EntryStream<K, V> peekKeys(Consumer<K> action) {
        return this.peek(entry -> action.accept(entry.getKey()));
    }

    /**
     * Peeks at the values of the entries in this stream, applying the provided action to each value.
     *
     * @param action the action to apply to each value
     * @return a new {@link EntryStream} with the same entries, but with the action applied to each value
     */
    public EntryStream<K, V> peekValues(Consumer<V> action) {
        return this.peek(entry -> action.accept(entry.getValue()));
    }

    /**
     * Peeks at the entries in this stream, applying the provided action to each entry.
     *
     * @param action the action to apply to each entry
     * @return a new {@link EntryStream} with the same entries, but with the action applied to each entry
     */
    public EntryStream<K, V> peek(BiConsumer<K, V> action) {
        return this.peek(entry -> action.accept(entry.getKey(), entry.getValue()));
    }

    /**
     * Maps the keys of the entries in this stream to new keys using the provided key mapper function. The
     * resulting keys are not required to be unique.
     *
     * @param mapper the function to map each key to a new key
     * @param <R> the type of the resulting keys
     *
     * @return a new {@link EntryStream} with the keys mapped to new keys
     */
    public <R> EntryStream<R, V> mapKeys(Function<K, R> mapper) {
        return EntryStream.of(this.map(entry -> Map.entry(
                mapper.apply(entry.getKey()),
                entry.getValue()
        )));
    }

    /**
     * Maps the values of the entries in this stream to new values using the provided value mapper function. The
     * resulting values are not required to be unique.
     *
     * @param mapper the function to map each value to a new value
     * @param <R> the type of the resulting values
     *
     * @return a new {@link EntryStream} with the values mapped to new values
     */
    public <R> EntryStream<K, R> mapValues(Function<V, R> mapper) {
        return EntryStream.of(this.map(entry -> Map.entry(
                entry.getKey(),
                mapper.apply(entry.getValue())
        )));
    }

    /**
     * Maps the keys and values of the entries in this stream to new entries using the provided key and value
     * mapper functions. The resulting entries are not required to be unique.
     *
     * @param keyMapper the function to map each key to a new key
     * @param valueMapper the function to map each value to a new value
     * @param <RK> the type of the resulting keys
     * @param <RV> the type of the resulting values
     * @return a new {@link EntryStream} with the keys and values mapped to new keys and values
     */
    public <RK, RV> EntryStream<RK, RV> map(Function<K, RK> keyMapper, Function<V, RV> valueMapper) {
        return EntryStream.of(this.map(entry -> Map.entry(
                keyMapper.apply(entry.getKey()),
                valueMapper.apply(entry.getValue())
        )));
    }

    /**
     * Maps the entries in this stream to new objects using the provided key and value mapper function. The resulting
     * objects are not required to be unique.
     *
     * @param function the function to map each entry to a new entry
     * @param <R> the type of the resulting objects
     *
     * @return a new {@link EntryStream} with the entries mapped to new entries
     */
    public <R> Stream<R> map(BiFunction<K, V, R> function) {
        return this.map(entry -> function.apply(entry.getKey(), entry.getValue()));
    }

    /**
     * Flatmaps the keys of the entries in this stream to new keys using the provided key mapper function. The
     * resulting entries will keep their original values, but the keys may be transformed into multiple new keys.
     *
     * @param mapper the function to map each key to a stream of new keys
     * @param <R> the type of the resulting keys
     * @return a new {@link EntryStream} with the keys flatmapped to new keys
     */
    public <R> EntryStream<R, V> flatMapKeys(Function<K, Stream<R>> mapper) {
        return EntryStream.of(this.flatMap(entry -> mapper
                .apply(entry.getKey())
                .map(key -> Map.entry(key, entry.getValue()))
        ));
    }

    /**
     * Flatmaps the values of the entries in this stream to new values using the provided value mapper function. The
     * resulting entries will keep their original keys, but the values may be transformed into multiple new values.
     *
     * @param mapper the function to map each value to a stream of new values
     * @param <R> the type of the resulting values
     * @return a new {@link EntryStream} with the values flatmapped to new values
     */
    public <R> EntryStream<K, R> flatMapValues(Function<V, Stream<R>> mapper) {
        return EntryStream.of(this.flatMap(entry -> mapper
                .apply(entry.getValue())
                .map(value -> Map.entry(entry.getKey(), value))
        ));
    }

    /**
     * Sorts the entries in ascending order, using the hashcode of the keys.
     *
     * @return a new {@link EntryStream} with the entries sorted by their keys
     */
    public EntryStream<K, V> sortedKeys() {
        return EntryStream.of(this.sorted(Comparator.comparingInt(entry -> entry.getKey().hashCode())));
    }

    /**
     * Sorts the entries in ascending order, using the hashcode of the values.
     *
     * @return a new {@link EntryStream} with the entries sorted by their values
     */
    public EntryStream<K, V> sortedValues() {
        return EntryStream.of(this.sorted(Comparator.comparingInt(entry -> entry.getValue().hashCode())));
    }

    /**
     * Sorts the entries in ascending order, using the provided comparator for the keys.
     *
     * @param comparator the comparator to use for sorting the keys
     * @return a new {@link EntryStream} with the entries sorted by their keys using the provided comparator
     */
    public EntryStream<K, V> sortedKeys(Comparator<K> comparator) {
        return EntryStream.of(this.sorted((entry1, entry2) -> comparator.compare(entry1.getKey(), entry2.getKey())));
    }

    /**
     * Sorts the entries in ascending order, using the provided comparator for the values.
     *
     * @param comparator the comparator to use for sorting the values
     * @return a new {@link EntryStream} with the entries sorted by their values using the provided comparator
     */
    public EntryStream<K, V> sortedValues(Comparator<V> comparator) {
        return EntryStream.of(this.sorted((entry1, entry2) -> comparator.compare(entry1.getValue(), entry2.getValue())));
    }

    /**
     * Sorts the entries in ascending order, using the provided comparator for both keys and values.
     *
     * @param comparator the comparator to use for sorting the entries
     * @return a new {@link EntryStream} with the entries sorted by their keys and values using the provided comparator
     */
    public EntryStream<K, V> sorted(BiComparator<K, V> comparator) {
        return EntryStream.of(this.sorted((entry1, entry2) -> {
            int result = comparator.compare(entry1, entry2);
            if (result == 0) {
                return entry1.getKey().hashCode() - entry2.getKey().hashCode();
            }
            return result;
        }));
    }

    /**
     * Returns a stream of the keys of the entries in this stream.
     *
     * @return a stream of the keys of the entries
     */
    public Stream<K> keys() {
        return this.map(Entry::getKey);
    }

    /**
     * Returns a stream of the values of the entries in this stream.
     *
     * @return a stream of the values of the entries
     */
    public Stream<V> values() {
        return this.map(Entry::getValue);
    }

    /**
     * Performs the given action for each entry in this stream. Equivalent to {@link #forEach(Consumer)}, but
     * accepts a {@link BiConsumer} that takes both the key and value of each entry.
     *
     * @param action the action to perform for each entry
     */
    public void forEach(BiConsumer<K, V> action) {
        this.forEach(entry -> action.accept(entry.getKey(), entry.getValue()));
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
