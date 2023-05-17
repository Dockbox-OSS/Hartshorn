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

package org.dockbox.hartshorn.util.option.some;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Represents an {@link Option} containing a non-null value.
 *
 * @param <T> The type of the value.
 *
 * @author Guus Lieben
 * @since 22.5
 */
public class Some<T> implements Option<T> {

    private final T value;

    public Some(final T value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public <E extends Throwable> Attempt<T, E> attempt(final Class<E> errorType) {
        return Attempt.of(this.value);
    }

    @Override
    public @NonNull Option<T> peek(final Consumer<T> consumer) {
        consumer.accept(this.value);
        return this;
    }

    @Override
    public @NonNull Option<T> onEmpty(final Runnable runnable) {
        return this;
    }

    @Override
    public @NonNull T get() {
        return this.value;
    }

    @Override
    public @Nullable T orNull() {
        return this.value;
    }

    @Override
    public @Nullable T orElse(@Nullable final T value) {
        return this.value;
    }

    @Override
    public @NonNull T orElseGet(@NonNull final Supplier<@NonNull T> supplier) {
        return this.value;
    }

    @Override
    public <E extends Throwable> @NonNull T orElseThrow(@NonNull final Supplier<@NonNull E> supplier) throws E {
        return this.value;
    }

    @Override
    public @NonNull Option<T> orCompute(@NonNull final Supplier<@NonNull T> supplier) {
        return this;
    }

    @Override
    public @NonNull Option<T> orComputeFlat(@NonNull final Supplier<@NonNull Option<T>> supplier) {
        return this;
    }

    @Override
    public boolean present() {
        return true;
    }

    @Override
    public boolean absent() {
        return false;
    }

    @Override
    public @NonNull Optional<T> optional() {
        return Optional.of(this.value);
    }

    @Override
    public @NonNull <U> Option<U> map(@NonNull final Function<@NonNull T, @Nullable U> function) {
        return Option.of(function.apply(this.value));
    }

    @Override
    public @NonNull <U> Option<U> flatMap(@NonNull final Function<@NonNull T, @NonNull Option<U>> function) {
        return function.apply(this.value);
    }

    @Override
    public @NonNull Option<T> filter(@NonNull final Predicate<@NonNull T> predicate) {
        return predicate.test(this.value) ? this : Option.empty();
    }

    @Override
    public @NonNull Stream<T> stream() {
        return Stream.of(this.value);
    }

    @Override
    public boolean contains(@Nullable final T value) {
        return Objects.equals(this.value, value);
    }

    @Override
    public @NonNull String toString() {
        return "Some{" +
                "value=" + this.value +
                '}';
    }
}
