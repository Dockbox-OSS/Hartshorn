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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Represents a failed {@link Attempt} which contains an error, but no value.
 *
 * @param <T> The type of the value
 * @param <E> The type of the exception
 *
 * @author Guus Lieben
 * @since 0.4.13
 */
public class FailedNone<T, E extends Throwable> extends None<T> implements Attempt<T, E> {

    private final E error;

    public FailedNone(final E error) {
        this.error = Objects.requireNonNull(error);
    }

    @Override
    public @NonNull Option<T> option() {
        return Option.empty();
    }

    @Override
    public @NonNull Option<E> errorOption() {
        return Option.of(this.error);
    }

    @Override
    public @NonNull Attempt<T, E> peekError(final Consumer<@NonNull E> consumer) {
        consumer.accept(this.error);
        return this;
    }

    @Override
    public @NonNull <S extends E> Attempt<T, E> peekError(final Class<S> errorType, final Consumer<@NonNull S> consumer) {
        if (this.error.getClass().equals(errorType)) {
            consumer.accept(errorType.cast(this.error));
        }
        return this;
    }

    @Override
    public @NonNull Attempt<T, E> onEmptyError(final Runnable runnable) {
        return this;
    }

    @Override
    public @NonNull E error() {
        return this.error;
    }

    @Override
    public E errorOrNull() {
        return this.error;
    }

    @Override
    public E errorOrElse(final E value) {
        return this.error;
    }

    @Override
    public E errorOrElseGet(final @NonNull Supplier<E> supplier) {
        return this.error;
    }

    @Override
    public @NonNull Attempt<T, E> orComputeError(final @NonNull Supplier<E> supplier) {
        return this;
    }

    @Override
    public boolean errorPresent() {
        return true;
    }

    @Override
    public boolean errorAbsent() {
        return false;
    }

    @Override
    public @NonNull Optional<E> errorOptional() {
        return Optional.of(this.error);
    }

    @Override
    public <U extends Throwable> @NonNull Attempt<T, U> mapError(final @NonNull Function<E, U> mapper) {
        return new FailedNone<>(mapper.apply(this.error));
    }

    @Override
    public <U extends Throwable> @NonNull Attempt<T, U> flatMapError(final @NonNull Function<E, Attempt<T, U>> mapper) {
        return mapper.apply(this.error);
    }

    @Override
    public Attempt<T, E> filterError(final Predicate<E> predicate) {
        return predicate.test(this.error) ? this : new SuccessNone<>();
    }

    @Override
    public Stream<E> errorStream() {
        return Stream.of(this.error);
    }

    @Override
    public Attempt<T, E> rethrow() throws E {
        throw this.error;
    }

    @Override
    public <R extends Throwable> Attempt<T, R> attempt(final Class<R> errorType) {
        if (errorType.isInstance(this.error)) {
            return Attempt.of(errorType.cast(this.error));
        }
        throw new IllegalArgumentException("Cannot cast " + this.error.getClass().getName() + " to " + errorType.getName());
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
    public @NonNull Attempt<T, E> orCompute(final @NonNull Supplier<@Nullable T> supplier) {
        final T value = supplier.get();
        if (value == null) return Attempt.of(this.error);
        return Attempt.of(value, this.error);
    }

    @Override
    public @NonNull Attempt<T, E> orComputeFlat(@NonNull final Supplier<@NonNull Option<T>> supplier) {
        final Option<T> value = supplier.get();
        if (value.absent()) return Attempt.of(this.error);
        return Attempt.of(value.get(), this.error);
    }

    @Override
    public @NonNull <U> Attempt<U, E> map(final @NonNull Function<@NonNull T, @Nullable U> function) {
        return Attempt.of(this.error);
    }

    @Override
    public @NonNull <U> Attempt<U, E> flatMap(final @NonNull Function<@NonNull T, @NonNull Option<U>> function) {
        return Attempt.of(this.error);
    }

    @Override
    public @NonNull Attempt<T, E> filter(final @NonNull Predicate<@NonNull T> predicate) {
        return this;
    }

    @Override
    public @NonNull String toString() {
        return "FailedNone{" +
                "error=" + this.error +
                '}';
    }
}
