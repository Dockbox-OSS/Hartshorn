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
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Represents an empty {@link Option}.
 *
 * @param <T> The type of the value
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class None<T> extends DefaultContext implements Option<T> {

    @Override
    public <E extends Throwable> Attempt<T, E> attempt(Class<E> errorType) {
        return Attempt.empty();
    }

    @Override
    public @NonNull Option<T> peek(Consumer<T> consumer) {
        return this;
    }

    @Override
    public @NonNull Option<T> onEmpty(Runnable runnable) {
        runnable.run();
        return this;
    }

    @Override
    public @NonNull T get() {
        throw new NoSuchElementException("No value present");
    }

    @Override
    public @Nullable T orNull() {
        return null;
    }

    @Override
    public @Nullable T orElse(@Nullable T value) {
        return value;
    }

    @Override
    public @NonNull T orElseGet(@NonNull Supplier<@NonNull T> supplier) {
        return supplier.get();
    }

    @Override
    public <E extends Throwable> @NonNull T orElseThrow(@NonNull Supplier<@NonNull E> supplier) throws E {
        throw supplier.get();
    }

    @Override
    public @NonNull Option<T> orCompute(@NonNull Supplier<@Nullable T> supplier) {
        return Option.of(supplier::get);
    }

    @Override
    public @NonNull Option<T> orComputeFlat(@NonNull Supplier<@NonNull Option<T>> supplier) {
        return supplier.get();
    }

    @Override
    public boolean present() {
        return false;
    }

    @Override
    public boolean absent() {
        return true;
    }

    @Override
    public @NonNull Optional<T> optional() {
        return Optional.empty();
    }

    @Override
    public @NonNull <U> Option<U> map(@NonNull Function<@NonNull T, @Nullable U> function) {
        return Option.empty();
    }

    @Override
    public @NonNull <U> Option<U> flatMap(@NonNull Function<@NonNull T, @NonNull Option<U>> function) {
        return Option.empty();
    }

    @Override
    public @NonNull Option<T> filter(@NonNull Predicate<@NonNull T> predicate) {
        return Option.empty();
    }

    @Override
    public @NonNull Stream<T> stream() {
        return Stream.empty();
    }

    @Override
    public boolean contains(@Nullable T value) {
        return false;
    }

    @Override
    public @NonNull String toString() {
        return "None";
    }
}
