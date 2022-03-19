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

package org.dockbox.hartshorn.data.registry;

import org.dockbox.hartshorn.util.Exceptional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

public class RegistryColumn<T> extends ArrayList<T> {

    public RegistryColumn() {
        super();
    }

    public RegistryColumn(final Collection<T> values) {
        this.addAll(values);
    }

    /**
     * Filters the RegistryColumn based on the provided filter.
     *
     * @param filter The filter accepts a value of type {@code T} or its parents and returns false to keep that value, true to remove it.
     *
     * @return Itself
     */
    public RegistryColumn<T> removeValueIf(final Predicate<? super T> filter) {
        this.removeIf(filter);
        return this;
    }

    /**
     * Maps this registryColumn to another type.
     *
     * @param mapper The mapper accepts a value of type {@code T} or its parents and returns a value of type {@code K}.
     * @param <K> The type of the new RegistryColumn.
     *
     * @return A new RegistryColumn which contains the mapped values of the previous RegistryColumn.
     */
    public <K> RegistryColumn<K> mapTo(final Function<? super T, K> mapper) {
        final RegistryColumn<K> result = new RegistryColumn<>();

        for (final T value : this) {
            result.add(mapper.apply(value));
        }
        return result;
    }

    /**
     * Maps this registryColumn to a collection and then adds all the collections into a new single
     * RegistryColumn
     *
     * @param mapper The mapper accepts a value of type {@code T} or its parents and returns a collection of type {@code K}.
     * @param <K> The type of the new RegistryColumn.
     *
     * @return A new RegistryColumn which contains all the values of the collections.
     */
    public <K> RegistryColumn<K> mapToSingleList(final Function<? super T, ? extends Collection<K>> mapper) {
        final RegistryColumn<K> result = new RegistryColumn<>();

        for (final T value : this) {
            result.addAll(mapper.apply(value));
        }
        return result;
    }

    /**
     * Attempts to cast the values to the specified type {@code K}. If the value is not an instance of
     * type {@code K} then it is not added to the resulting RegistryColumn.
     *
     * @param clazz The class of the type to convert to.
     * @param <K> The type of the new RegistryColumn
     *
     * @return A new RegistryColumn which contains all the values of the previous RegistryColumn that could be converted.
     */
    public <K extends T> RegistryColumn<K> convertTo(final Class<K> clazz) {
        final RegistryColumn<K> result = new RegistryColumn<>();

        for (final T value : this) {
            if (clazz.isInstance(value)) {
                final K convertedValue = (K) value;
                result.add(convertedValue);
            }
        }
        return result;
    }

    /**
     * Finds the first value which matches the provided predicate.
     *
     * @param predicate The predicate takes in a value of type {@code T} or its parents and returns true if that value is a match, otherwise it returns false.
     *
     * @return An {@link Exceptional} containing the value of the first match, if one is found.
     */
    public Exceptional<T> firstMatch(final Predicate<? super T> predicate) {
        for (final T value : this) {
            if (predicate.test(value)) return Exceptional.of(value);
        }
        return Exceptional.empty();
    }

    /**
     * Safely returns the first element in the RegistryColumn.
     *
     * @return An {@link Exceptional} containing the first element in the RegistryColumn, if one is found.
     */
    public Exceptional<T> first() {
        return this.safe(0);
    }

    /**
     * Safely get an element by wrapping it within an {@link Exceptional}.
     *
     * @param index The index of the element to retrieve.
     *
     * @return An {@link Exceptional} containing the element at the provided index in the RegistryColumn, if one is found.
     */
    public Exceptional<T> safe(final int index) {
        return Exceptional.of(() -> this.get(index));
    }
}
