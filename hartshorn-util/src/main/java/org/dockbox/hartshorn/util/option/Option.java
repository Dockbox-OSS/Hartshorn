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

package org.dockbox.hartshorn.util.option;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.option.none.None;
import org.dockbox.hartshorn.util.option.some.Some;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.xml.transform.Result;

/**
 * A container object which may or may not contain a non-null value. If a value is present, {@link #present()} will
 * return {@code true} and {@link #get()} will return the value. If no value is present, {@link #absent()} will return
 * {@code true}.
 *
 * <p>Additional methods that depend on the presence or absence of a contained value are provided,
 * such as {@link #orElse(Object)} (return a default value if value not present) and {@link #peek(Consumer)} (execute a
 * block of code if the value is present).
 *
 * @param <T> the type of the (potential) value wrapped by the {@link Option} instance.
 *
 * @author Guus Lieben
 * @since 22.5
 */
public interface Option<T> {

    /**
     * Creates a new {@link Option} instance wrapping the given nullable value. If the value is {@code null}, an
     * {@link Option} instance representing an absent value is returned.
     *
     * @param value the value to wrap.
     * @param <T> the type of the (potential) value wrapped by the {@link Option} instance.
     *
     * @return a new {@link Option} instance wrapping the given nullable value.
     */
    @NonNull
    static <T> Option<T> of(final T value) {
        if (value == null) return new None<>();
        return new Some<>(value);
    }

    /**
     * Creates a new {@link Option} instance based on the given {@link Optional} instance. If the {@link Optional}
     * instance is empty, an {@link Option} instance representing an absent value is returned. If the {@link Optional}
     * instance is present, an {@link Option} instance representing the present value is returned.
     *
     * @param optional the {@link Optional} instance to wrap.
     * @param <T> the type of the (potential) value wrapped by the {@link Option} instance.
     *
     * @return a new {@link Option} instance wrapping the (potential) value of the given {@link Optional} instance.
     */
    @NonNull
    static <T> Option<T> of(final Optional<T> optional) {
        if (optional.isEmpty()) return new None<>();
        return new Some<>(optional.get());
    }

    /**
     * Creates a {@link Result} instance based on the provided {@link Callable}. If the callable throws any kind of
     * exception, an {@link Option} instance representing an absent value is returned. If the callable returns a
     * non-null value, an {@link Option} instance representing the present value is returned. If the callable returns
     * {@code null}, an {@link Option} instance representing an absent value is returned.
     *
     * @param supplier the {@link Callable} to execute.
     * @param <T> the type of the (potential) value wrapped by the {@link Option} instance.
     *
     * @return a new {@link Option} instance wrapping the (potential) value of the given {@link Callable} instance.
     */
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

    /**
     * Creates a new {@link Option} instance which contains no value.
     *
     * @param <T> the type of the (potential) value wrapped by the {@link Option} instance.
     *
     * @return a new {@link Option} instance which contains no value.
     */
    @NonNull
    static <T> Option<T> empty() {
        return new None<>();
    }

    /**
     * Returns a {@link Attempt} instance based on the current {@link Option} instance. If the current instance
     * is already a {@link Attempt}, it is returned. If the current instance is an {@link Option}, a new
     * {@link Attempt} instance is created.
     *
     * @param errorType the type of the error to wrap in the {@link Attempt} instance.
     * @param <E> the type of the error to wrap in the {@link Attempt} instance.
     *
     * @return a {@link Attempt} instance based on the current {@link Option} instance.
     */
    <E extends Throwable> Attempt<T, E> attempt(final Class<E> errorType);

    /**
     * If a value is present in this {@link Option}, the given {@link Consumer} is executed with the value as argument.
     * If no value is present, the given {@link Consumer} is not executed.
     *
     * @param consumer the {@link Consumer} to execute.
     *
     * @return the current {@link Option} instance.
     */
    @NonNull
    Option<T> peek(Consumer<T> consumer);

    /**
     * If no value is present in this {@link Option}, the given {@link Runnable} is executed. If a value is present, the
     * given {@link Runnable} is not executed.
     *
     * @param runnable the {@link Runnable} to execute.
     *
     * @return the current {@link Option} instance.
     */
    @NonNull
    Option<T> onEmpty(Runnable runnable);

    /**
     * If a value is present in this {@link Option}, the value is returned. If no value is present, a
     * {@link NoSuchElementException} is thrown. Before this method is called, it is recommended to check if a value is
     * present using {@link #present()}.
     *
     * @return the value wrapped by the {@link Option} instance.
     * @throws NoSuchElementException if no value is present.
     */
    @NonNull
    T get();

    /**
     * If a value is present in this {@link Option}, the value is returned. If no value is present, {@code null} is
     * returned.
     *
     * @return the value wrapped by the {@link Option} instance, or {@code null} if no value is present.
     */
    @Nullable
    T orNull();

    /**
     * If a value is present in this {@link Option}, the value is returned. If no value is present, the given default
     * value is returned.
     *
     * @param value the default value to return if no value is present.
     *
     * @return the value wrapped by the {@link Option} instance, or the given default value if no value is present.
     */
    @Nullable
    T orElse(@Nullable T value);

    /**
     * If a value is present in this {@link Option}, the value is returned. If no value is present, the value returned
     * by the given {@link Supplier} is returned. The {@link Supplier} is only executed if no value is present. If the
     * {@link Supplier} returns {@code null}, no exception is thrown.
     *
     * @param supplier the {@link Supplier} to execute if no value is present.
     *
     * @return the value wrapped by the {@link Option} instance, or the value returned by the given
     */
    @Nullable
    T orElseGet(@NonNull Supplier<@Nullable T> supplier);

    /**
     * If a value is present in this {@link Option}, the value is returned. If no value is present, the error returned
     * by the given {@link Supplier} is thrown. The {@link Supplier} is only executed if no value is present.
     *
     * @param supplier the {@link Supplier} to execute if no value is present.
     * @param <E> the type of the error to throw.
     *
     * @return the value wrapped by the {@link Option} instance.
     * @throws E if no value is present.
     */
    @NonNull <E extends Throwable> T orElseThrow(@NonNull Supplier<@NonNull E> supplier) throws E;

    /**
     * If no value is present in this {@link Option}, the given {@link Supplier} is executed. The result of the
     * {@link Supplier} is then returned wrapped in a new {@link Option} instance. If a value is present, the current
     * {@link Option} instance is returned.
     *
     * @param supplier the {@link Supplier} to execute if no value is present.
     *
     * @return a new {@link Option} instance wrapping the result of the given {@link Supplier}, or the current
     *         {@link Option} instance if a value is present.
     */
    @NonNull
    Option<T> orCompute(@NonNull Supplier<@Nullable T> supplier);

    /**
     * If no value is present in this {@link Option}, the given {@link Supplier} is executed. The {@link Option}
     * returned by the {@link Supplier} is then returned. If a value is present, the current {@link Option} instance is
     * returned.
     *
     * @param supplier the {@link Supplier} to execute if no value is present.
     *
     * @return the {@link Option} returned by the given {@link Supplier}, or the current {@link Option} instance if a
     *         value is present.
     */
    @NonNull
    Option<T> orComputeFlat(@NonNull Supplier<@NonNull Option<T>> supplier);

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    boolean present();

    /**
     * Return {@code true} if there is no value present, otherwise {@code false}. Acts as an inverse of
     * {@link Option#present()}.
     *
     * @return {@code true} if there is no value present, otherwise {@code false}
     */
    boolean absent();

    /**
     * Returns a new {@link Optional} instance wrapping the value wrapped by the current {@link Option} instance. If no
     * value is present, an empty {@link Optional} is returned.
     *
     * @return a new {@link Optional} instance wrapping the value wrapped by the current {@link Option} instance, or an
     *         empty {@link Optional} if no value is present.
     */
    @NonNull
    Optional<T> optional();

    /**
     * Transforms the value wrapped by the current {@link Option} instance using the given {@link Function}. If the
     * current {@link Option} instance does not contain a value, an empty {@link Option} is returned. If the given
     * {@link Function} returns {@code null}, no exception is thrown, and an empty {@link Option} is returned.
     *
     * @param <U> The type of the result of the mapping function
     * @param function A mapping function to apply to the value, if present
     *
     * @return an {@link Option} describing the result of applying a mapping function to the value of this
     *         {@link Option}, if a value is present, otherwise {@link Option#empty()}
     */
    @NonNull <U> Option<U> map(@NonNull Function<@NonNull T, @Nullable U> function);

    /**
     * Transforms the value wrapped by the current {@link Option} instance using the given {@link Function}. If the
     * current {@link Option} instance does not contain a value, an empty {@link Option} is returned. In all other
     * cases, the result of the given {@link Function} is returned.
     *
     * @param <U> The type of the result of the mapping function
     * @param function A mapping function to apply to the value, if present
     *
     * @return the result of applying a mapping function to the value of this {@link Option}, if a value is present,
     *         otherwise {@link Option#empty()}
     */
    @NonNull <U> Option<U> flatMap(@NonNull Function<@NonNull T, @NonNull Option<U>> function);

    /**
     * If a value is present in this {@link Option}, the value is applied to the given {@link Predicate}. If the
     * {@link Predicate} returns {@code true}, the current {@link Option} instance is returned. If the
     * {@link Predicate} returns {@code false}, an empty {@link Option} is returned. If no value is present, an empty
     * {@link Option} is returned.
     *
     * @param predicate the {@link Predicate} to apply to the value, if present.
     * @return the current {@link Option} instance if a value is present and the {@link Predicate} returns
     */
    @NonNull
    Option<T> filter(@NonNull Predicate<@NonNull T> predicate);

    /**
     * Returns a {@link Stream} containing the value wrapped by the current {@link Option} instance, if a value is
     * present. If no value is present, an empty {@link Stream} is returned.
     *
     * @return a {@link Stream} containing the value wrapped by the current {@link Option} instance, if a value is
     *         present, otherwise an empty {@link Stream}.
     */
    @NonNull
    Stream<T> stream();

    /**
     * Returns {@code true} if the value wrapped by the current {@link Option} instance is equal to the given value,
     * otherwise {@code false}. If no value is present, {@code false} is returned. This checks for equality, not for
     * identity.
     *
     * @param value the value to compare to the value wrapped by the current {@link Option} instance.
     * @return {@code true} if the value wrapped by the current {@link Option} instance is equal to the given value,
     *         otherwise {@code false}.
     */
    boolean contains(@Nullable T value);

}
