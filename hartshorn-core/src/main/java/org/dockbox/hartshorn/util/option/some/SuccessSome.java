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

package org.dockbox.hartshorn.util.option.some;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.option.FailableOption;
import org.dockbox.hartshorn.util.option.Option;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SuccessSome<T, E extends Throwable> extends Some<T> implements FailableOption<T, E> {

    public SuccessSome(final T value) {
        super(value);
    }

    @Override
    public @NonNull FailableOption<T, E> peek(final Consumer<T> consumer) {
        return (FailableOption<T, E>) super.peek(consumer);
    }

    @Override
    public @NonNull FailableOption<T, E> onEmpty(final Runnable runnable) {
        return (FailableOption<T, E>) super.onEmpty(runnable);
    }

    @Override
    public @NonNull FailableOption<T, E> orCompute(final @NonNull Supplier<@NonNull T> supplier) {
        return (FailableOption<T, E>) super.orCompute(supplier);
    }

    @Override
    public @NonNull FailableOption<T, E> orComputeFlat(final @NonNull Supplier<@NonNull Option<T>> supplier) {
        return (FailableOption<T, E>) super.orComputeFlat(supplier);
    }

    @Override
    public @NonNull <U> FailableOption<U, E> map(final @NonNull Function<@NonNull T, @Nullable U> function) {
        return FailableOption.of(function.apply(this.get()));
    }

    @Override
    public @NonNull <U> Option<U> flatMap(final @NonNull Function<@NonNull T, @NonNull Option<U>> function) {
        return function.apply(this.get());
    }

    @Override
    public @NonNull FailableOption<T, E> filter(final @NonNull Predicate<@NonNull T> predicate) {
        return predicate.test(this.get()) ? this : FailableOption.empty();
    }

    @Override
    public Option<T> option() {
        return Option.of(super.get());
    }

    @Override
    public Option<E> errorOption() {
        return Option.empty();
    }

    @Override
    public FailableOption<T, E> peekError(final Consumer<E> consumer) {
        return this;
    }

    @Override
    public FailableOption<T, E> onEmptyError(final Runnable runnable) {
        runnable.run();
        return this;
    }

    @Override
    public E error() {
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
    public E errorOrElseGet(final Supplier<E> supplier) {
        return supplier.get();
    }

    @Override
    public FailableOption<T, E> orComputeError(final Supplier<E> supplier) {
        final E error = supplier.get();
        if (error != null) return FailableOption.of(error);
        else return this;
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
    public Optional<E> errorOptional() {
        return Optional.empty();
    }

    @Override
    public <U extends Throwable> FailableOption<T, U> mapError(final Function<E, U> mapper) {
        return new SuccessSome<>(super.get());
    }

    @Override
    public <U extends Throwable> FailableOption<T, U> flatMapError(final Function<E, FailableOption<T, U>> mapper) {
        return new SuccessSome<>(super.get());
    }

    @Override
    public FailableOption<T, E> filterError(final Predicate<E> predicate) {
        return this;
    }

    @Override
    public Stream<E> errorStream() {
        return Stream.empty();
    }

    @Override
    public FailableOption<T, E> rethrow() throws E {
        return this;
    }

    @Override
    public FailableOption<T, E> rethrowUnchecked() {
        return this;
    }
}
