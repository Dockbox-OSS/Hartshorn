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

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.option.FailableOption;
import org.dockbox.hartshorn.util.option.Option;

public class SuccessNone<T, E extends Throwable> extends None<T> implements FailableOption<T, E> {

    @Override
    public Option<T> option() {
        return Option.empty();
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
    public @NonNull FailableOption<T, E> orCompute(final @NonNull Supplier<@Nullable T> supplier) {
        return FailableOption.of(supplier.get());
    }

    @Override
    public @NonNull FailableOption<T, E> filter(final @NonNull Predicate<@NonNull T> predicate) {
        return this;
    }

    @Override
    public FailableOption<T, E> orComputeError(final Supplier<E> supplier) {
        return FailableOption.of(supplier.get());
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
    public @NonNull FailableOption<T, E> peek(final Consumer<T> consumer) {
        return this;
    }

    @Override
    public @NonNull FailableOption<T, E> onEmpty(final Runnable runnable) {
        runnable.run();
        return this;
    }

    @Override
    public Optional<E> errorOptional() {
        return Optional.empty();
    }

    @Override
    public <U extends Throwable> FailableOption<T, U> mapError(final Function<E, U> mapper) {
        return FailableOption.empty();
    }

    @Override
    public <U extends Throwable> FailableOption<T, U> flatMapError(final Function<E, FailableOption<T, U>> mapper) {
        return FailableOption.empty();
    }

    @Override
    public FailableOption<T, E> filterError(final Predicate<E> predicate) {
        return FailableOption.empty();
    }

    @Override
    public @NonNull <U> FailableOption<U, E> map(final @NonNull Function<@NonNull T, @Nullable U> function) {
        return FailableOption.empty();
    }

    @Override
    public @NonNull <U> FailableOption<U, E> flatMap(final @NonNull Function<@NonNull T, @NonNull Option<U>> function) {
        return FailableOption.empty();
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
