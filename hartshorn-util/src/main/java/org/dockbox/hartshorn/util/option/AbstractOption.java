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

package org.dockbox.hartshorn.util.option;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.util.TypeUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of {@link Option} that provides default implementations for most methods. The methods implemented
 * here are based on methods that require specific implementations. This aims to simplify the use of common {@link Option}
 * operations.
 *
 * @param <T> The type of the value
 */
public abstract class AbstractOption<T> implements Option<T> {

    @Override
    public <U> Stream<U> stream(@NonNull final Function<@NonNull T, @NonNull Stream<U>> mapper) {
        return this.stream().flatMap(mapper);
    }

    @Override
    public <U> Option<U> ofType(@NonNull final Class<U> type) {
        return this.filter(type::isInstance).cast(type);
    }

    @Override
    public <U> Option<U> cast(@NonNull final Class<U> type) {
        return this.map(type::cast);
    }

    @Override
    public <K extends T, A extends K> Option<A> adjust(@NonNull final Class<K> type) {
        return this.cast(type).map(e -> TypeUtils.adjustWildcards(e, type));
    }

    @Override
    public <K extends T, A extends K> A adjustAndGet(@NonNull final Class<K> type) {
        return this.<K, A>adjust(type).get();
    }

    @Override
    public <E> E collect(@NonNull final Collector<T, ?, E> collector) {
        return this.stream().collect(collector);
    }

    @Override
    public <E> E collect(@NonNull final Supplier<E> supplier, @NonNull final BiConsumer<E, T> accumulator, @NonNull final BiConsumer<E, E> combiner) {
        return this.stream().collect(supplier, accumulator, combiner);
    }

    @Override
    public List<T> toList() {
        return this.collect(Collectors.toList());
    }

    @Override
    public Set<T> toSet() {
        return this.collect(Collectors.toSet());
    }

    @Override
    public <K> Map<K, T> toMap(@NonNull final Function<T, K> keyMapper) {
        return this.collect(Collectors.toMap(keyMapper, Function.identity()));
    }

    @Override
    public <K, V> Map<K, V> toMap(@NonNull final Function<T, K> keyMapper, @NonNull final Function<T, V> valueMapper) {
        return this.collect(Collectors.toMap(keyMapper, valueMapper));
    }

    @Override
    public Spliterator<T> spliterator() {
        final int size = this.map(t -> 1).orElse(0);
        return Spliterators.spliterator(this.iterator(), size, 0);
    }

    @Override
    public Iterator<T> iterator() {
        return this.map(List::of)
                .map(List::iterator)
                .orElseGet(Collections::emptyIterator);
    }

    @Override
    @NonNull
    public abstract String toString(); // Force implementation of toString
}
