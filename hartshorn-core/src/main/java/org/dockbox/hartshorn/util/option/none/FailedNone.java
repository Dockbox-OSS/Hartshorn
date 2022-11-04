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

package org.dockbox.hartshorn.util.option.none;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.util.option.FailableOption;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FailedNone<T, E extends Throwable> extends None<T> implements FailableOption<T, E> {

    private final E error;

    public FailedNone(final E error) {
        this.error = Objects.requireNonNull(error);
    }

    @Override
    public Option<T> option() {
        return Option.empty();
    }

    @Override
    public Option<E> errorOption() {
        return Option.of(this.error);
    }

    @Override
    public FailableOption<T, E> peekError(final Consumer<E> consumer) {
        consumer.accept(this.error);
        return this;
    }

    @Override
    public FailableOption<T, E> onEmptyError(final Runnable runnable) {
        return this;
    }

    @Override
    public E error() {
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
    public E errorOrElseGet(final Supplier<E> supplier) {
        return this.error;
    }

    @Override
    public FailableOption<T, E> orComputeError(final Supplier<E> supplier) {
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
    public Optional<E> errorOptional() {
        return Optional.of(this.error);
    }

    @Override
    public <U extends Throwable> FailableOption<T, U> mapError(final Function<E, U> mapper) {
        return new FailedNone<>(mapper.apply(this.error));
    }

    @Override
    public <U extends Throwable> FailableOption<T, U> flatMapError(final Function<E, FailableOption<T, U>> mapper) {
        return mapper.apply(this.error);
    }

    @Override
    public FailableOption<T, E> filterError(final Predicate<E> predicate) {
        return predicate.test(this.error) ? this : new SuccessNone<>();
    }

    @Override
    public Stream<E> errorStream() {
        return Stream.of(this.error);
    }

    @Override
    public FailableOption<T, E> rethrow() throws E {
        throw this.error;
    }

    @Override
    public FailableOption<T, E> rethrowUnchecked() {
        if (this.error instanceof RuntimeException) throw (RuntimeException) this.error;
        else if (this.error instanceof Error) throw (Error) this.error;
        else return ExceptionHandler.unchecked(this.error);
    }

    @Override
    public <R extends Throwable> FailableOption<T, R> failable(final Class<R> errorType) {
        if (errorType.isInstance(this.error)) {
            return FailableOption.of(errorType.cast(this.error));
        }
        throw new IllegalArgumentException("Cannot cast " + this.error.getClass().getName() + " to " + errorType.getName());
    }

    @Override
    public @NonNull FailableOption<T, E> peek(final Consumer<T> consumer) {
        return this;
    }

    @Override
    public @NonNull FailableOption<T, E> onEmpty(final Runnable runnable) {
        runnable.run();
        return this;
    }

    @Override
    public @NonNull FailableOption<T, E> orCompute(final @NonNull Supplier<@Nullable T> supplier) {
        final T value = supplier.get();
        if (value == null) return FailableOption.of(this.error);
        return FailableOption.of(value, this.error);
    }

    @Override
    public @NonNull FailableOption<T, E> orComputeFlat(@NonNull final Supplier<@NonNull Option<T>> supplier) {
        final Option<T> value = supplier.get();
        if (value.absent()) return FailableOption.of(this.error);
        return FailableOption.of(value.get(), this.error);
    }

    @Override
    public @NonNull <U> FailableOption<U, E> map(final @NonNull Function<@NonNull T, @Nullable U> function) {
        return FailableOption.of(this.error);
    }

    @Override
    public @NonNull <U> FailableOption<U, E> flatMap(final @NonNull Function<@NonNull T, @NonNull Option<U>> function) {
        return FailableOption.of(this.error);
    }

    @Override
    public @NonNull FailableOption<T, E> filter(final @NonNull Predicate<@NonNull T> predicate) {
        return this;
    }
}
