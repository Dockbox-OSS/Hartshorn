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

package org.dockbox.hartshorn.util.option.some;

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
 * Represents a successful {@link Attempt} with a non-null value, and no error.
 *
 * @param <T> The type of the value
 * @param <E> The type of the exception
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class SuccessSome<T, E extends Throwable> extends Some<T> implements Attempt<T, E> {

    public SuccessSome(T value) {
        super(value);
    }

    @Override
    public @NonNull Attempt<T, E> peek(Consumer<T> consumer) {
        return (Attempt<T, E>) super.peek(consumer);
    }

    @Override
    public @NonNull Attempt<T, E> onEmpty(Runnable runnable) {
        return (Attempt<T, E>) super.onEmpty(runnable);
    }

    @Override
    public @NonNull Attempt<T, E> orCompute(@NonNull Supplier<@NonNull T> supplier) {
        return (Attempt<T, E>) super.orCompute(supplier);
    }

    @Override
    public @NonNull Attempt<T, E> orComputeFlat(@NonNull Supplier<@NonNull Option<T>> supplier) {
        return (Attempt<T, E>) super.orComputeFlat(supplier);
    }

    @Override
    public @NonNull <U> Attempt<U, E> map(@NonNull Function<@NonNull T, @Nullable U> function) {
        return Attempt.of(function.apply(this.get()));
    }

    @Override
    public @NonNull <U> Option<U> flatMap(@NonNull Function<@NonNull T, @NonNull Option<U>> function) {
        return function.apply(this.get());
    }

    @Override
    public @NonNull Attempt<T, E> filter(@NonNull Predicate<@NonNull T> predicate) {
        return predicate.test(this.get()) ? this : Attempt.empty();
    }

    @Override
    public @NonNull Option<T> option() {
        return Option.of(super.get());
    }

    @Override
    public @NonNull Option<E> errorOption() {
        return Option.empty();
    }

    @Override
    public @NonNull Attempt<T, E> peekError(Consumer<@NonNull E> consumer) {
        return this;
    }

    @Override
    public @NonNull <S extends E> Attempt<T, E> peekError(Class<S> errorType, Consumer<@NonNull S> consumer) {
        return this;
    }

    @Override
    public @NonNull Attempt<T, E> onEmptyError(Runnable runnable) {
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
    public E errorOrElse(E value) {
        return value;
    }

    @Override
    public E errorOrElseGet(@NonNull Supplier<E> supplier) {
        return supplier.get();
    }

    @Override
    public @NonNull Attempt<T, E> orComputeError(@NonNull Supplier<E> supplier) {
        E error = supplier.get();
        if (error != null) {
            return Attempt.of(error);
        }
        else {
            return this;
        }
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
    public @NonNull Optional<E> errorOptional() {
        return Optional.empty();
    }

    @Override
    public <U extends Throwable> @NonNull Attempt<T, U> mapError(@NonNull Function<E, U> mapper) {
        return new SuccessSome<>(super.get());
    }

    @Override
    public <U extends Throwable> @NonNull Attempt<T, U> flatMapError(@NonNull Function<E, Attempt<T, U>> mapper) {
        return new SuccessSome<>(super.get());
    }

    @Override
    public Attempt<T, E> filterError(Predicate<E> predicate) {
        return this;
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
        return "SuccessSome[" + super.get() + "]";
    }
}
