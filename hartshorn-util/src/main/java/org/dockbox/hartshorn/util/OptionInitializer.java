/*
 * Copyright 2019-2024 the original author or authors.
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

import java.util.function.Function;
import java.util.function.Supplier;

import org.dockbox.hartshorn.util.option.Option;

/**
 * A functional interface for initializing an {@link Option} based on an input. This interface is
 * primarily a utility class to lazily transform a potential {@link Option} value into other forms.
 *
 * @param <I> the input type
 * @param <T> the type of the value in the option
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface OptionInitializer<I, T> extends ContextualInitializer<I, Option<T>> {

    @Override
    Option<T> initialize(SingleElementContext<? extends I> input);

    /**
     * Returns a new {@link OptionInitializer} that will apply the given mapper to the value of the
     * option if it is present.
     *
     * @param mapper the mapper to apply to the value of the option
     * @return a new {@link OptionInitializer} that will apply the given mapper to the value
     *
     * @see Option#map(Function)
     */
    default OptionInitializer<I, T> map(Function<T, T> mapper) {
        return input -> this.initialize(input).map(mapper);
    }

    /**
     * Returns a new {@link OptionInitializer} that will apply the given mapper to {@link Option}.
     *
     * @param mapper the mapper to apply to the option
     * @return a new {@link OptionInitializer} that will apply the given mapper to the option
     *
     * @see Option#flatMap(Function)
     */
    default OptionInitializer<I, T> flatMap(Function<T, Option<T>> mapper) {
        return input -> this.initialize(input).flatMap(mapper);
    }

    /**
     * Returns a new {@link ContextualInitializer} that will either return the value of the {@link
     * Option}, or the given default value if the option is empty.
     *
     * @param supplier the supplier to provide the default value
     * @return a new {@link ContextualInitializer} that will return either the wrapped value, or
     * the given value
     *
     * @see Option#orElse(Object)
     */
    default ContextualInitializer<I, T> orElseGet(Supplier<T> supplier) {
        return this.transform(option -> option.orElseGet(supplier));
    }

    /**
     * Returns a new {@link ContextualInitializer} that will transform the {@link Option} into a
     * different type using the given transformer.
     *
     * @param transformer the transformer to apply to the option
     * @param <R> the type of the transformed value
     * 
     * @return a new {@link ContextualInitializer} that will transform the option into a different type
     */
    default <R> ContextualInitializer<I, R> transform(Function<Option<T>, R> transformer) {
        return input -> transformer.apply(this.initialize(input));
    }
}
