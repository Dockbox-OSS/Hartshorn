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

package org.dockbox.hartshorn.util.option;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.function.ThrowingSupplier;
import org.dockbox.hartshorn.util.option.none.FailedNone;
import org.dockbox.hartshorn.util.option.none.SuccessNone;
import org.dockbox.hartshorn.util.option.some.FailedSome;
import org.dockbox.hartshorn.util.option.some.SuccessSome;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A container object which may or may not contain a non-null value, and which may or may not contain a non-null
 * exception. This expands on the {@link Option} interface by adding the ability to handle exceptions.
 *
 * @param <T> The type of the value
 * @param <E> The type of the exception
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 *
 * @deprecated Attempts potentially hide throwables, and are therefore not recommended. Use a direct {@link Option}
 *             instead, and consider handling exceptions immediately or re-throwing them in a checked manner.
 */
@Deprecated(since = "0.6.0", forRemoval = true)
public interface Attempt<T, E extends Throwable> extends Option<T> {

    /**
     * Creates an {@link Attempt} from the given {@link Callable}. If the {@link Callable} throws an exception, an
     * {@link Attempt} containing this exception will be returned. If the {@link Callable} does not throw an exception,
     * an {@link Attempt} containing the nullable result of the {@link Callable} will be returned.
     *
     * @param callable The {@link Callable} to attempt
     * @param <T> The type of the value
     *
     * @return The {@link Attempt} containing the result of the {@link Callable}
     */
    @NonNull
    static <T> Attempt<T, Exception> of(@NonNull Callable<@Nullable T> callable) {
        try {
            T result = callable.call();
            if (result == null) {
                return new SuccessNone<>();
            }
            else {
                return new SuccessSome<>(result);
            }
        }
        catch (Exception e) {
            return new FailedNone<>(e);
        }
    }

    /**
     * Creates an {@link Attempt} from the given {@link ThrowingSupplier}. If the {@link ThrowingSupplier} throws an
     * exception, the error will be checked against the given {@link Class} and an {@link Attempt} containing this
     * exception will be returned if the type matches. If the thrown exception does not match the given type, the
     * exception will be rethrown in an unchecked manner. If the {@link ThrowingSupplier} does not throw an exception,
     * an {@link Attempt} containing the nullable result of the {@link ThrowingSupplier} will be returned.
     *
     * @param supplier The {@link ThrowingSupplier} to attempt
     * @param errorType The {@link Class} of the error to catch
     * @param <T> The type of the value
     * @param <E> The type of the exception
     *
     * @return The {@link Attempt} containing the result of the {@link ThrowingSupplier}
     */
    @NonNull
    static <T, E extends Throwable> Attempt<T, E> of(@NonNull ThrowingSupplier<@Nullable T, E> supplier,
                                                     @NonNull Class<E> errorType) {
        try {
            T result = supplier.get();
            if (result == null) {
                return new SuccessNone<>();
            }
            return new SuccessSome<>(result);
        }
        catch (Throwable e) {
            if (errorType.isInstance(e)) {
                return new FailedNone<>(errorType.cast(e));
            }
            throw new IllegalArgumentException("The given error type does not match the thrown exception", e);
        }
    }

    /**
     * Creates a new {@link Attempt} which does not contain a value or exception. This is equivalent to
     * {@link Option#empty()}.
     *
     * @param <T> The type of the value
     * @param <E> The type of the exception
     *
     * @return The empty {@link Attempt}
     */
    @NonNull
    static <T, E extends Throwable> Attempt<T, E> empty() {
        return new SuccessNone<>();
    }

    /**
     * Creates a new {@link Attempt} which contains the given value. This is equivalent to {@link Option#of(Object)}.
     *
     * @param value The value to contain
     * @param <T> The type of the value
     * @param <E> The type of the exception
     *
     * @return The {@link Attempt} containing the given value
     */
    @NonNull
    static <T, E extends Throwable> Attempt<T, E> of(@Nullable T value) {
        if (value == null) {
            return new SuccessNone<>();
        }
        return new SuccessSome<>(value);
    }

    /**
     * Creates a new {@link Attempt} based on the given {@link Optional}. If the {@link Optional} is empty, an empty
     * {@link Attempt} will be returned. If the {@link Optional} contains a value, an {@link Attempt} containing this
     * value will be returned. This is equivalent to {@link Option#of(Optional)}.
     *
     * @param optional The {@link Optional} to base the {@link Attempt} on
     * @param <T> The type of the value
     * @param <E> The type of the exception
     *
     * @return The {@link Attempt} based on the given {@link Optional}
     */
    @NonNull
    static <T, E extends Throwable> Attempt<T, E> of(@NonNull Optional<T> optional) {
        if (optional.isEmpty()) {
            return new SuccessNone<>();
        }
        return new SuccessSome<>(optional.get());
    }

    /**
     * Creates a new {@link Attempt} which contains the given exception. If the given exception is null, an empty
     * {@link Attempt} will be returned. If the given exception is not null, an {@link Attempt} containing this
     * exception will be returned.
     *
     * <p>The given {@link Attempt}'s type signature returns the exception type, or a parent of the exception type.
     * This
     * is to allow for the creation of {@link Attempt} instances which contain exceptions which are more specific than
     * the {@link Attempt} type signature. For example, if an {@link Attempt} is created with a type signature of
     * {@link FileNotFoundException}, an {@link Attempt} containing an {@link IOException} can be returned.
     *
     * @param throwable The exception to contain
     * @param <T> The type of the value
     * @param <E> The type of the exception
     * @param <R> The type of the exception, or a parent of the exception type
     *
     * @return The {@link Attempt} containing the given exception
     */
    @NonNull
    static <T, E extends Throwable, R extends E> Attempt<T, E> of(@Nullable R throwable) {
        if (throwable == null) {
            return new SuccessNone<>();
        }
        return new FailedNone<>(throwable);
    }

    /**
     * Creates a new {@link Attempt} which contains the given value and exception. Based on which of the given value and
     * exception is null, the returned {@link Attempt} will either contain the value, the exception, both, or neither.
     *
     * <p>The given {@link Attempt}'s type signature returns the exception type, or a parent of the exception type.
     * This
     * is to allow for the creation of {@link Attempt} instances which contain exceptions which are more specific than
     * the {@link Attempt} type signature. For example, if an {@link Attempt} is created with a type signature of
     * {@link FileNotFoundException}, an {@link Attempt} containing an {@link IOException} can be returned.
     *
     * @param value The value to contain
     * @param throwable The exception to contain
     * @param <T> The type of the value
     * @param <E> The type of the exception
     * @param <R> The type of the exception, or a parent of the exception type
     *
     * @return The {@link Attempt} containing the given value and exception
     */
    @NonNull
    static <T, E extends Throwable, R extends E> Attempt<T, E> of(@Nullable T value,
                                                                  @Nullable R throwable) {
        if (value != null && throwable != null) {
            return new FailedSome<>(value, throwable);
        }
        if (value == null && throwable == null) {
            return new SuccessNone<>();
        }
        if (value != null) {
            return new SuccessSome<>(value);
        }
        return new FailedNone<>(throwable);
    }

    /**
     * Returns a {@link Option} instance based on the given {@link Attempt}. If the {@link Attempt} contains a value, an
     * {@link Option} containing this value will be returned. If the {@link Attempt} contains an exception, an empty
     * {@link Option} will be returned, and no exception will be thrown. If the {@link Attempt} does not contain a value
     * or exception, an empty {@link Option} will be returned.
     *
     * @return The {@link Option} based on the given {@link Attempt}
     */
    @NonNull
    Option<T> option();

    /**
     * Returns an {@link Option} instance based on the given {@link Attempt}. If the {@link Attempt} contains an
     * exception, a {@link Option} containing this exception will be returned. If the {@link Attempt} contains a value,
     * an empty {@link Option} will be returned, and no exception will be thrown. If the {@link Attempt} does not
     * contain a value or exception, an empty {@link Option} will be returned.
     *
     * @return The {@link Option} based on the given {@link Attempt}
     */
    @NonNull
    Option<E> errorOption();

    /**
     * If an exception is present in this {@link Attempt}, the given {@link Consumer} is executed with the exception as
     * argument. If no exception is present, the given {@link Consumer} is not executed.
     *
     * @param consumer the {@link Consumer} to execute.
     *
     * @return the current {@link Attempt} instance.
     */
    @NonNull
    Attempt<T, E> peekError(Consumer<@NonNull E> consumer);

    /**
     * If an exception is present in this {@link Attempt} which exactly matches the given exception type, the given
     * {@link Consumer} is executed with the exception as argument. If no exception is present, or the exception is not
     * of the given type, the given {@link Consumer} is not executed.
     *
     * @param errorType the type of the exception to match.
     * @param consumer the {@link Consumer} to execute.
     * @param <S> the type of the exception to match.
     *
     * @return the current {@link Attempt} instance.
     */
    @NonNull <S extends E> Attempt<T, E> peekError(Class<S> errorType, Consumer<@NonNull S> consumer);

    /**
     * If no exception is present in this {@link Attempt}, the given {@link Runnable} is executed. If an exception is
     * present, the given {@link Runnable} is not executed.
     *
     * @param runnable the {@link Runnable} to execute.
     *
     * @return the current {@link Attempt} instance.
     */
    @NonNull
    Attempt<T, E> onEmptyError(Runnable runnable);

    /**
     * If an exception is present in this {@link Attempt}, the exception is returned. If no exception is present, a
     * {@link NoSuchElementException} is thrown. This method is similar to {@link #get()}, but instead of returning the
     * value, it returns the exception. It is recommended to check if an exception is present using
     * {@link #errorPresent()}.
     *
     * @return the exception contained in this {@link Attempt}.
     */
    @NonNull
    E error();

    /**
     * If an exception is present in this {@link Attempt}, the exception is returned. If no exception is present,
     * {@code null} is returned. This method is similar to {@link #orNull()}, but instead of returning the value, it
     * returns the exception.
     *
     * @return the exception contained in this {@link Attempt}, or {@code null} if no exception is present.
     */
    @Nullable
    E errorOrNull();

    /**
     * If an exception is present in this {@link Attempt}, the exception is returned. If no exception is present, the
     * given default exception is returned. This method is similar to {@link #orElse(Object)}, but instead of returning
     * the value, it returns the exception.
     *
     * @param value the default exception to return.
     *
     * @return the exception contained in this {@link Attempt}, or the given default exception if no exception is
     *         present.
     */
    @Nullable
    E errorOrElse(@Nullable E value);

    /**
     * If an exception is present in this {@link Attempt}, the exception is returned. If no exception is present, the
     * value returned by the given {@link Supplier} is returned. This method is similar to {@link #orElseGet(Supplier)},
     * but instead of returning the value, it returns the exception.
     *
     * @param supplier the {@link Supplier} to return the default exception.
     *
     * @return the exception contained in this {@link Attempt}, or the value returned by the given {@link Supplier} if
     *         no
     */
    @Nullable
    E errorOrElseGet(@NonNull Supplier<@Nullable E> supplier);

    /**
     * If no exception is present in this {@link Attempt}, the given {@link Supplier} is executed. The result of the
     * {@link Supplier} is then wrapped in a new {@link Attempt} and returned. If an exception is present, the current
     * {@link Attempt} is returned.
     *
     * @param supplier the {@link Supplier} to execute.
     *
     * @return the current {@link Attempt} instance, or a new {@link Attempt} containing the result of the given
     */
    @NonNull
    Attempt<T, E> orComputeError(@NonNull Supplier<@Nullable E> supplier);

    /**
     * Returns {@code true} if there is an exception present, otherwise {@code false}.
     *
     * @return {@code true} if there is an exception present, otherwise {@code false}
     */
    boolean errorPresent();

    /**
     * Returns {@code true} if there is no exception present, otherwise {@code false}. This method is the inverse of
     * {@link #errorPresent()}.
     *
     * @return {@code true} if there is no exception present, otherwise {@code false}
     */
    boolean errorAbsent();

    /**
     * Returns a new {@link Optional} instance wrapping the exception contained in this {@link Attempt}. If no exception
     * is present, an empty {@link Optional} is returned.
     *
     * @return a new {@link Optional} instance wrapping the exception contained in this {@link Attempt}, or an empty
     *         {@link Optional} if no exception is present.
     */
    @NonNull
    Optional<E> errorOptional();

    /**
     * Transforms the exception contained in this {@link Attempt} using the given {@link Function}. The returned
     * {@link Attempt} will always contain the current nullable value. If no exception is present, the given
     * {@link Function} is not executed. If the given {@link Function} returns {@code null}, the returned
     * {@link Attempt} will not contain an exception, even if the current {@link Attempt} contains an exception.
     *
     * @param mapper the {@link Function} to transform the exception.
     * @param <U> the type of the transformed exception.
     *
     * @return a new {@link Attempt} instance containing the transformed exception.
     */
    @NonNull <U extends Throwable> Attempt<T, U> mapError(@NonNull Function<@NonNull E, @Nullable U> mapper);

    /**
     * Transforms the exception contained in this {@link Attempt} using the given {@link Function}. The returned
     * {@link Attempt} will not contain a value, unless the given {@link Function} returns an {@link Attempt} containing
     * a value. If no exception is present, the given {@link Function} is not executed. If the given {@link Function}
     * returns an empty {@link Attempt}, the returned {@link Attempt} will not contain an exception, even if the current
     * {@link Attempt} contains an exception.
     *
     * @param mapper the {@link Function} to transform the exception.
     * @param <U> the type of the transformed exception.
     *
     * @return a new {@link Attempt} instance containing the transformed exception.
     */
    @NonNull <U extends Throwable> Attempt<T, U> flatMapError(
            @NonNull Function<@NonNull E, @NonNull Attempt<T, U>> mapper);

    /**
     * If an exception is present in this {@link Attempt}, the exception is applied to the given {@link Predicate}. If
     * the {@link Predicate} returns {@code true}, the current {@link Attempt} is returned. If the {@link Predicate}
     * returns {@code false}, an {@link Attempt} will be returned with the value of the current {@link Attempt}, but
     * without an exception. If no exception is present, the current {@link Attempt} is returned.
     *
     * @param predicate the {@link Predicate} to apply to the exception.
     *
     * @return the current {@link Attempt} instance, or a new {@link Attempt} containing the value of the current
     *         {@link Attempt}, but without an exception.
     */
    Attempt<T, E> filterError(Predicate<E> predicate);

    /**
     * Returns a {@link Stream} containing the exception contained in this {@link Attempt}. If no exception is present,
     * an empty {@link Stream} is returned.
     *
     * @return a {@link Stream} containing the exception contained in this {@link Attempt}, or an empty {@link Stream}
     *         if no exception is present.
     */
    Stream<E> errorStream();

    /**
     * Throws the exception contained in this {@link Attempt}. If no exception is present, this method will only return
     * itself. This method will throw a checked exception, based on the type of the exception contained in this
     * {@link Attempt}. If the exception is a {@link RuntimeException}, it will be thrown as is, and will not be wrapped
     * in a checked exception.
     *
     * @return the current {@link Attempt} instance.
     * @throws E the exception contained in this {@link Attempt}, if present.
     */
    Attempt<T, E> rethrow() throws E;

    @Override
    @NonNull Attempt<T, E> peek(Consumer<T> consumer);

    @Override
    @NonNull Attempt<T, E> onEmpty(Runnable runnable);

    @Override
    @NonNull Attempt<T, E> orCompute(@NonNull Supplier<@Nullable T> supplier);

    @Override
    @NonNull <U> Attempt<U, E> map(@NonNull Function<@NonNull T, @Nullable U> function);

    @Override
    @NonNull Attempt<T, E> filter(@NonNull Predicate<@NonNull T> predicate);

    @Override
    default <U> Attempt<U, E> ofType(@NonNull Class<U> type) {
        return this.filter(type::isInstance).cast(type);
    }

    @Override
    default <U> Attempt<U, E> cast(@NonNull Class<U> type) {
        return this.map(type::cast);
    }

    @Override
    default <K extends T, A extends K> Attempt<A, E> adjust(@NonNull Class<K> type) {
        return this.ofType(type).map(value -> TypeUtils.adjustWildcards(value, type));
    }
}
