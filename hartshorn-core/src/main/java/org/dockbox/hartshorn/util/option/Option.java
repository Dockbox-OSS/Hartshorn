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
import org.dockbox.hartshorn.util.option.none.None;
import org.dockbox.hartshorn.util.option.some.Some;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A container object which may or may not contain a non-null value. If a value is present, {@link
 * #present()} will return {@code true} and {@link #get()} will return the value. If no value is
 * present, {@link #absent()} will return {@code true}.
 *
 * <p>Additional methods that depend on the presence or absence of a contained value are provided,
 * such as {@link #orElse(Object)} (return a default value if value not present) and {@link
 * #peek(Consumer)} (execute a block of code if the value is present).
 *
 * @param <T> the type of the (potential) value wrapped by the {@link Option} instance.
 *
 * @author Guus Lieben
 *
 * @since 22.5
 */
public interface Option<T> {

    @NonNull
    static <T> Option<T> of(final T value) {
        if (value == null) return new None<>();
        return new Some<>(value);
    }

    @NonNull
    static <T> Option<T> of(final Optional<T> optional) {
        if (optional.isEmpty()) return new None<>();
        return new Some<>(optional.get());
    }

    @NonNull
    static <T> Option<T> of(final Callable<T> supplier) {
        try {
            final T value = supplier.call();
            if (value == null) return new None<>();
            return new Some<>(value);
        }
        catch (final Exception e) {
            return new None<>();
        }
    }

    @NonNull
    static <T> Option<T> empty() {
        return new None<>();
    }

    <E extends Throwable> FailableOption<T, E> failable(final Class<E> errorType);

    @NonNull
    Option<T> peek(Consumer<T> consumer);

    @NonNull
    Option<T> onEmpty(Runnable runnable);

    @NonNull
    T get();

    @Nullable
    T orNull();

    @Nullable
    T orElse(@Nullable T value);

    @NonNull
    T orElseGet(@NonNull Supplier<@NonNull T> supplier);

    @NonNull <E extends Throwable> T orElseThrow(@NonNull Supplier<@NonNull E> supplier) throws E;

    @NonNull
    Option<T> orCompute(@NonNull Supplier<@Nullable T> supplier);

    @NonNull
    Option<T> orComputeFlat(@NonNull Supplier<@NonNull Option<T>> supplier);

    boolean present();

    boolean absent();

    @NonNull
    Optional<T> optional();

    @NonNull
    <U> Option<U> map(@NonNull Function<@NonNull T, @Nullable U> function);

    @NonNull
    <U> Option<U> flatMap(@NonNull Function<@NonNull T, @NonNull Option<U>> function);

    @NonNull
    Option<T> filter(@NonNull Predicate<@NonNull T> predicate);

    @NonNull
    Stream<T> stream();

    boolean contains(@Nullable T value);

}
