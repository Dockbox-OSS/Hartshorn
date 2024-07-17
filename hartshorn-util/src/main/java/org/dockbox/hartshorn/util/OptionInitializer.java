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

@FunctionalInterface
public interface OptionInitializer<I, T> extends ContextualInitializer<I, Option<T>> {

    @Override
    Option<T> initialize(SingleElementContext<? extends I> input);

    default OptionInitializer<I, T> map(Function<T, T> mapper) {
        return input -> this.initialize(input).map(mapper);
    }

    default OptionInitializer<I, T> flatMap(Function<T, Option<T>> mapper) {
        return input -> this.initialize(input).flatMap(mapper);
    }

    default ContextualInitializer<I, T> orElseGet(Supplier<T> supplier) {
        return transform(option -> option.orElseGet(supplier));
    }

    default <R> ContextualInitializer<I, R> transform(Function<Option<T>, R> transformer) {
        return input -> transformer.apply(this.initialize(input));
    }
}
