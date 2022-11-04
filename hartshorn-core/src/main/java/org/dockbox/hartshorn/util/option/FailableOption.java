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

package org.dockbox.hartshorn.util.option;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.option.none.FailedNone;
import org.dockbox.hartshorn.util.option.none.SuccessNone;
import org.dockbox.hartshorn.util.option.some.FailedSome;
import org.dockbox.hartshorn.util.option.some.SuccessSome;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface FailableOption<T, E extends Throwable> extends Option<T> {

    static <T> FailableOption<T, Exception> of(final Callable<T> callable) {
        try {
            final T result = callable.call();
            if (result == null) return new SuccessNone<>();
            else return new SuccessSome<>(result);
        }
        catch (final Exception e) {
            return new FailedNone<>(e);
        }
    }

    static <T, E extends Throwable> FailableOption<T, E> of(final FailableSupplier<T, E> supplier, final Class<E> errorType) {
        try {
            final T result = supplier.get();
            if (result == null) return new SuccessNone<>();
            return new SuccessSome<>(result);
        } catch (final Throwable e) {
            if (errorType.isInstance(e)) {
                return new FailedNone<>(errorType.cast(e));
            }
            throw new RuntimeException(e);
        }
    }

    static <T, E extends Throwable> FailableOption<T, E> empty() {
        return new SuccessNone<>();
    }

    static <T, E extends Throwable> FailableOption<T, E> of(final T value) {
        if (value == null) return new SuccessNone<>();
        return new SuccessSome<>(value);
    }

    static <T, E extends Throwable> FailableOption<T, E> of(final Optional<T> optional) {
        if (optional.isEmpty()) return new SuccessNone<>();
        return new SuccessSome<>(optional.get());
    }

    static <T, E extends Throwable, R extends E> FailableOption<T, E> of(final R throwable) {
        if (throwable == null) return new SuccessNone<>();
        return new FailedNone<>(throwable);
    }

    static <T, E extends Throwable, R extends E> FailableOption<T, E> of(final T value, final R throwable) {
        if (value != null && throwable != null) return new FailedSome<>(value, throwable);
        if (value == null && throwable == null) return new SuccessNone<>();
        if (value != null) return new SuccessSome<>(value);
        return new FailedNone<>(throwable);
    }

    Option<T> option();

    Option<E> errorOption();

    FailableOption<T, E> peekError(Consumer<E> consumer);

    FailableOption<T, E> onEmptyError(Runnable runnable);

    E error();

    E errorOrNull();

    E errorOrElse(E value);

    E errorOrElseGet(Supplier<E> supplier);

    FailableOption<T, E> orComputeError(Supplier<E> supplier);

    boolean errorPresent();

    boolean errorAbsent();

    Optional<E> errorOptional();

    <U extends Throwable> FailableOption<T, U> mapError(Function<E, U> mapper);

    <U extends Throwable> FailableOption<T, U> flatMapError(Function<E, FailableOption<T, U>> mapper);

    FailableOption<T, E> filterError(Predicate<E> predicate);

    Stream<E> errorStream();

    FailableOption<T, E> rethrow() throws E;

    FailableOption<T, E> rethrowUnchecked();

    @Override
    @NonNull FailableOption<T, E> peek(Consumer<T> consumer);

    @Override
    @NonNull FailableOption<T, E> onEmpty(Runnable runnable);

    @Override
    @NonNull FailableOption<T, E> orCompute(@NonNull Supplier<@Nullable T> supplier);

    @Override
    @NonNull <U> FailableOption<U, E> map(@NonNull Function<@NonNull T, @Nullable U> function);

    @Override
    @NonNull FailableOption<T, E> filter(@NonNull Predicate<@NonNull T> predicate);
}
