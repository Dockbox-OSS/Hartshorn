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

package org.dockbox.hartshorn.util.option.none;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Represents a failed {@link Attempt} which contains neither a value nor an error.
 *
 * @param <T> The type of the value
 * @param <E> The type of the exception
 *
 * @author Guus Lieben
 * @since 22.5
 */
public class SuccessNone<T, E extends Throwable> extends None<T> implements Attempt<T, E> {

    @Override
    public @NonNull Option<T> option() {
        return Option.empty();
    }

    @Override
    public @NonNull Option<E> errorOption() {
        return Option.empty();
    }

    @Override
    public @NonNull Attempt<T, E> peekError(final Consumer<@NonNull E> consumer) {
        return this;
    }

    @Override
    public @NonNull <S extends E> Attempt<T, E> peekError(final Class<S> errorType, final Consumer<@NonNull S> consumer) {
        return this;
    }

    @Override
    public @NonNull Attempt<T, E> onEmptyError(final Runnable runnable) {
        runnable.run();
        return this;
    }

    @Override
    public @NonNull E error() {
        throw new NoSuchElementException("No value present");
    }

    @Override
    public E errorOrNull() {
        return null;
    }

    @Override
    public E errorOrElse(final E value) {
        return value;
    }

    @Override
    public E errorOrElseGet(final @NonNull Supplier<E> supplier) {
        return supplier.get();
    }

    @Override
    public @NonNull Attempt<T, E> orCompute(final @NonNull Supplier<@Nullable T> supplier) {
        return Attempt.of(supplier.get());
    }

    @Override
    public @NonNull Attempt<T, E> filter(final @NonNull Predicate<@NonNull T> predicate) {
        return this;
    }

    @Override
    public @NonNull Attempt<T, E> orComputeError(final @NonNull Supplier<E> supplier) {
        return Attempt.of(supplier.get());
    }

    @Override
    public boolean errorPresent() {
        return false;
    }

    @Override
    public boolean errorAbsent() {
        return true;
    }

    @Override
    public @NonNull Attempt<T, E> peek(final Consumer<T> consumer) {
        return this;
    }

    @Override
    public @NonNull Attempt<T, E> onEmpty(final Runnable runnable) {
        runnable.run();
        return this;
    }

    @Override
    public @NonNull Optional<E> errorOptional() {
        return Optional.empty();
    }

    @Override
    public <U extends Throwable> @NonNull Attempt<T, U> mapError(final @NonNull Function<E, U> mapper) {
        return Attempt.empty();
    }

    @Override
    public <U extends Throwable> @NonNull Attempt<T, U> flatMapError(final @NonNull Function<E, Attempt<T, U>> mapper) {
        return Attempt.empty();
    }

    @Override
    public Attempt<T, E> filterError(final Predicate<E> predicate) {
        return Attempt.empty();
    }

    @Override
    public @NonNull <U> Attempt<U, E> map(final @NonNull Function<@NonNull T, @Nullable U> function) {
        return Attempt.empty();
    }

    @Override
    public @NonNull <U> Attempt<U, E> flatMap(final @NonNull Function<@NonNull T, @NonNull Option<U>> function) {
        return Attempt.empty();
    }

    @Override
    public Stream<E> errorStream() {
        return Stream.empty();
    }

    @Override
    public Attempt<T, E> rethrow() throws E {
        return this;
    }

    @Override
    public @NonNull String toString() {
        return "SuccessNone";
    }
}
