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

package org.dockbox.hartshorn.util;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.util.function.CheckedBiFunction;
import org.dockbox.hartshorn.util.function.CheckedFunction;
import org.dockbox.hartshorn.util.function.CheckedSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A container object which may or may not contain a non-null value. If a value is present, {@code
 * present()} will return {@code true} and {@code get()} will return the value. If no value is
 * present, {@code absent()} will return {@code true}.
 *
 * <p>Additional methods that depend on the presence or absence of a contained value are provided,
 * such as {@link #or(java.lang.Object) or()} (return a default value if value not present)
 * and {@link #present(java.util.function.Consumer) present()} (execute a block of code if the
 * value is present).
 *
 * <p>This is an extended type of {@link Optional}, providing additional support for {@link
 * Exception} checks and actions. Additionally, it allows for more abilities to construct the type
 * from a {@link CheckedSupplier}, {@link Optional} and to create from a {@link Throwable} instance.
 *
 * @param <T> the type parameter
 *
 * @author Guus Lieben
 * @author Josh Jeffers
 *
 * @since 21.1
 */
public final class Result<T> {

    private static final Logger LOG = LoggerFactory.getLogger(Result.class);
    private static final Result<?> EMPTY = new Result<>();

    @Nullable private final T value;
    @Nullable private final Throwable throwable;

    private Result() {
        this.value = null;
        this.throwable = null;
    }

    private Result(@NonNull final T value) {
        this.value = Objects.requireNonNull(value);
        this.throwable = null;
    }

    private Result(@NonNull final T value, @NonNull final Throwable throwable) {
        this.value = Objects.requireNonNull(value);
        this.throwable = Objects.requireNonNull(throwable);
    }

    private Result(@NonNull final Throwable throwable) {
        this.value = null;
        this.throwable = Objects.requireNonNull(throwable);
    }

    /**
     * Provides a {@link Result} instance based on a provided {@link Optional} instance. If the
     * optional contains a value, it is unwrapped and wrapped in {@link Result#of(Object)}. If
     * the optional doesn't contain a value, {@link Result#empty()} is returned.
     *
     * @param <T> The type parameter of the potential value
     * @param optional The {@link Optional} instance to wrap
     *
     * @return The {@link Result}
     */
    @NonNull
    public static <T> Result<T> of(@NonNull final Optional<T> optional) {
        return optional.map(Result::of).orElseGet(Result::empty);
    }

    /**
     * Provides a {@link Result} instance which can contain a value in {@link Result#value}.
     * The value can be null. If the value is null, {@link Result#empty()} is returned.
     *
     * @param <T> The type parameter of the potential value
     * @param value The potential value to wrap
     *
     * @return The {@link Result}
     */
    @NonNull
    public static <T> Result<T> of(@Nullable final T value) {
        return null == value ? empty() : new Result<>(value);
    }

    /**
     * Provides a {@link Result} instance which contains no {@link Result#value value} and
     * no {@link Result#throwable throwable}. The returned instance is a cast copy of {@link
     * Result#EMPTY}
     *
     * @param <T> The type parameter of the value which the instance is cast to
     *
     * @return The none {@link Result}
     */
    @NonNull
    public static <T> Result<T> empty() {
        return (Result<T>) EMPTY;
    }

    /**
     * Provides a {@link Result} instance based on a provided {@link CheckedSupplier}. If the supplier
     * throws any type of {@link Throwable} {@link Result#of(Throwable)} is returned, describing
     * the thrown throwable. If the supplier successfully provided a value, {@link
     * Result#of(Object)} is returned. This also means suppliers can return nullable
     * values.
     *
     * @param <T> The type parameter of the potential value
     * @param supplier The {@link CheckedSupplier} instance, supplying the value
     *
     * @return The {@link Result}
     */
    @NonNull
    public static <T> Result<T> of(@NonNull final Callable<@Nullable T> supplier) {
        try {
            return of(supplier.call());
        }
        catch (final Throwable t) {
            return of(t);
        }
    }

    /**
     * Return the value if present, otherwise invoke {@code other} and return the result of that
     * invocation.
     *
     * @param other A {@code Supplier} whose result is returned if no value is present
     *
     * @return The value if present otherwise the result of {@code other.get()}
     */
    @NonNull
    public T get(@NonNull final Supplier<@NonNull ? extends T> other) {
        return null != this.value ? this.get() : other.get();
    }

    /**
     * If a value is present, invoke the specified consumer with the value, otherwise do nothing.
     *
     * @param consumer Block to be executed if a value is present
     *
     * @return The {@link Result}, for chaining
     */
    @NonNull
    public Result<T> present(@NonNull final Consumer<@NonNull ? super T> consumer) {
        if (null != this.value) consumer.accept(this.value);
        return this;
    }

    /**
     * Return {@code true} if there is no value present, otherwise {@code false}. Acts as an inverse of
     * {@link Result#present()}.
     *
     * @return {@code true} if there is no value present, otherwise {@code false}
     */
    public boolean absent() {
        return null == this.value;
    }

    /**
     * If a value is empty, invoke the specified runnable, otherwise do nothing.
     *
     * @param runnable Block to be executed if a value is empty
     *
     * @return The {@link Result}, for chaining
     */
    @NonNull
    public Result<T> absent(@NonNull final Runnable runnable) {
        if (null == this.value) runnable.run();
        return this;
    }

    /**
     * Return the value if present, otherwise return null.
     *
     * @return The value, if present, otherwise null
     */
    @Nullable
    public T orNull() {
        return this.value;
    }

    /**
     * Return the value if present, otherwise return {@code other}.
     *
     * @param other The value to be returned if there is no value present, may be null
     *
     * @return The value, if present, otherwise {@code other}
     * @see Result#orNull()
     */
    @Nullable
    public T or(@Nullable final T other) {
        return null != this.value ? this.value : other;
    }

    /**
     * Return the throwable if present, otherwise return {@code other}.
     *
     * @param other The value to be returned if there is no throwable present, may be null
     *
     * @return The throwable, if present, otherwise {@code other}
     */
    @Nullable
    public Throwable or(@Nullable final Throwable other) {
        return null != this.throwable ? this.throwable : other;
    }

    /**
     * If a value is present, apply the provided {@link Result}-bearing mapping function to it,
     * return that result, otherwise return {@link Result#empty()}. This method is similar to
     * {@link Result#map(CheckedFunction)}, but the provided mapper is one whose result is already an
     * {@link Result}, and if invoked, {@code then} does not wrap it with an additional {@link Result}.
     *
     * @param <U> The type parameter to the {@link Result} returned
     * @param mapper A mapping function to apply to the value, if present
     *
     * @return The result of applying an {@link Result}-bearing mapping function to the value of this {@link Result}, if a value is present, otherwise {@link Result#empty()}
     * @throws NullPointerException If the mapping function returns a null result
     */
    @NonNull
    public <U> Result<U> flatMap(@NonNull final CheckedFunction<@NonNull ? super T, @NonNull Result<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!this.present()) return this.caught() ? of(this.throwable) : empty();
        else {
            try {
                return Objects.requireNonNull(mapper.apply(this.value));
            }
            catch (final ApplicationException e) {
                return of(e);
            }
        }
    }

    public Result<T> orFlat(@NonNull final Callable<Result<T>> supplier) {
        Objects.requireNonNull(supplier);
        if (this.present()) return this;
        else return of(supplier).map(Result::orNull);
    }

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    public boolean present() {
        return null != this.value;
    }

    /**
     * Return {@code true} if there is a throwable present, otherwise {@code false}.
     *
     * @return {@code true} if there is a throwable present, otherwise {@code false}
     */
    public boolean caught() {
        return null != this.throwable;
    }

    /**
     * Provides a {@link Result} instance which contains no value, but contains the given
     * throwable as {@link Result#throwable}. This requires the provided throwable to be
     * non-null. If the throwable is null, a {@link NullPointerException} is thrown.
     *
     * @param <T> The type parameter of the empty value
     * @param throwable The throwable to wrap
     *
     * @return The {@link Result}
     * @throws NullPointerException When the provided value is null
     */
    @NonNull
    public static <T> Result<T> of(@NonNull final Throwable throwable) {
        return new Result<>(throwable);
    }

    /**
     * If a value is present, apply the provided {@link Result}-bearing mapping function to both
     * the value and throwable described by this {@link Result}, return that result, otherwise
     * return {@link Result#empty()}. This method is similar to {@link
     * Result#flatMap(CheckedFunction)}, but the provided mapper is one whose input is both a {@code
     * Throwable} and a value of type {@code T}.
     *
     * @param <U> The type parameter to the {@link Result} returned
     * @param mapper A mapping function to apply to the value and throwable, if present
     *
     * @return The result of applying an {@link Result}-bearing mapping function to the value and throwable of this {@link Result}, if a value is present, otherwise {@link Result#empty()}
     * @throws NullPointerException If the mapping function returns a null result
     */
    @NonNull
    public <U> Result<U> flatMap(@NonNull final CheckedBiFunction<@NonNull ? super T, @Nullable Throwable, @NonNull Result<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!this.present()) return this.caught() ? of(this.throwable) : empty();
        else {
            try {
                return Objects.requireNonNull(mapper.apply(this.value, this.throwable));
            }
            catch (final ApplicationException e) {
                return of(e);
            }
        }
    }

    /**
     * Return a {@link Result} instance holding the value if present, otherwise invoke {@code
     * defaultValue} and return the result of that invocation, combined with a throwable if a
     * throwable is present. This method is similar to {@link Result#get(Supplier)}, but
     * instead of returning the value, the result is wrapped in a {@link Result}.
     *
     * @param defaultValue A {@code Supplier} whose result is wrapped if no value is present
     *
     * @return The {@link Result}, for chaining
     */
    @NonNull
    public Result<T> orElse(@NonNull final CheckedSupplier<@Nullable T> defaultValue) {
        if (this.absent()) {
            try {
                if (this.caught()) {
                    return of(defaultValue.get(), this.throwable);
                }
                else {
                    return of(defaultValue.get());
                }
            }
            catch (final Throwable e) {
                return of(e);
            }
        }
        return this;
    }

    /**
     * If a value is present, and the value matches the given predicate, return an {@link Result}
     * describing value, otherwise return {@link Result#empty()}.
     *
     * @param predicate A predicate to apply to the value, if present
     *
     * @return an {@link Result} describing the value of this {@link Result} if a value is present and the value matches the given predicate, otherwise {@link Result#empty()}
     */
    @NonNull
    public Result<T> filter(@NonNull final Predicate<@NonNull ? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!this.present()) return this;
        else return predicate.test(this.value) ? this : empty();
    }

    /**
     * If a value is present, apply the provided mapping function to it, and if the result is
     * non-null, return an {@link Result} describing the result. Otherwise, return {@link
     * Result#empty()}.
     *
     * <pre>{@code
     * Result<TextChannel> textChannel = JDAUtils.getJDA()
     * .map(jda -> jda.getTextChannelById(channelId));
     * }</pre>
     *
     * <p>Here, {@code getJDA} returns an {@code Result<JDA>}, and then {@code map} returns an
     * {@code Result<TextChannel>} for the desired channel if one exists.
     *
     * @param <U> The type of the result of the mapping function
     * @param mapper A mapping function to apply to the value, if present
     *
     * @return an {@link Result} describing the result of applying a mapping function to the value of this {@link Result}, if a value is present, otherwise {@link Result#empty()}
     */
    @NonNull
    public <U> Result<U> map(@NonNull final CheckedFunction<@NonNull ? super T, @Nullable ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!this.present()) return this.caught() ? of(this.throwable) : empty();
        else {
            try {
                return of(mapper.apply(this.value), this.throwable);
            }
            catch (final ApplicationException e) {
                return of(e);
            }
        }
    }

    /**
     * Provides a {@link Result} instance which can contain both a value in {@link
     * Result#value} and a throwable in {@link Result#throwable}. Both the value and
     * throwable can be null.
     *
     * <ul>
     *   <li>If the value is null and the throwable is not null, {@link Result#of(Throwable)} is
     *       used to generate the instance.
     *   <li>If the value is not null and the throwable is null, {@link Result#of(Object)} is
     *       used to generate the instance.
     *   <li>If both the value and the throwable are null, {@link Result#empty()} is used to
     *       generate the new instance.
     * </ul>
     *
     * @param <T> The type parameter of the potential value
     * @param value The potential value to wrap
     * @param throwable The potential throwable to wrap
     *
     * @return The {@link Result}
     */
    @NonNull
    public static <T> Result<T> of(@Nullable final T value, @Nullable final Throwable throwable) {
        if (null == value && null == throwable) return empty();
        else if (null == value) return of(throwable);
        else if (null == throwable) return of(value);
        else return new Result<>(value, throwable);
    }

    /**
     * If a throwable is present, invoke the specified {@code consumer} with the throwable, otherwise
     * do nothing.
     *
     * @param consumer The block to be executed if a throwable is present
     *
     * @return The {@link Result}, for chaining
     */
    @NonNull
    public Result<T> caught(@NonNull final Consumer<@NonNull ? super Throwable> consumer) {
        if (null != this.throwable) consumer.accept(this.throwable);
        return this;
    }

    /**
     * Return the contained value, if present, otherwise throw an exception to be created by the
     * provided supplier.
     *
     * @param <X> Type of the exception to be thrown
     * @param exceptionSupplier The supplier which will return the exception to be thrown
     *
     * @return The present value
     * @throws X If there is no value present
     * @throws NoSuchElementException If the value and exception are both not present
     */
    @NonNull
    public <X extends Throwable> T orThrow(@NonNull final Supplier<@Nullable ? extends X> exceptionSupplier) throws X {
        if (null != this.value) {
            return this.value;
        }
        else {
            final X exception = exceptionSupplier.get();
            if (exception != null)
                throw exception;
        }
        // If we get here, both the exception and value are null. This will act as a shorthand
        // for the NoSuchElementException that is thrown when calling get() on an empty Result.
        return this.get();
    }

    /**
     * Return the contained value, if present, otherwise throw the provided exception. The provided
     * exception is rethrown as unchecked, so it need not be declared in a {@code throws} clause.
     *
     * @param exceptionSupplier The supplier which will return the exception to be thrown.
     * @return The present value
     * @throws Throwable If there is no value present
     * @throws NoSuchElementException If the value and exception are both not present
     */
    public T orThrowUnchecked(final Supplier<@Nullable Throwable> exceptionSupplier) {
        if (null != this.value) {
            return this.value;
        }
        else {
            final Throwable exception = exceptionSupplier.get();
            if (exception != null)
                ExceptionHandler.unchecked(exception);
        }
        // If we get here, both the exception and value are null. This will act as a shorthand
        // for the NoSuchElementException that is thrown when calling get() on an empty Result.
        return this.get();
    }

    /**
     * Return {@code true} if there is no throwable present, otherwise {@code false}.
     *
     * @return {@code true} if there is no throwable present, otherwise {@code false}
     */
    public boolean errorAbsent() {
        return null == this.throwable;
    }

    /**
     * If a throwable is none, invoke the specified runnable, otherwise do nothing.
     *
     * @param runnable Block to be executed if no throwable is present
     *
     * @return The {@link Result}, for chaining
     */
    @NonNull
    public Result<T> ifErrorAbsent(@NonNull final Runnable runnable) {
        if (null == this.throwable) runnable.run();
        return this;
    }

    /**
     * If a throwable is present, wrap it in a new {@link RuntimeException} and throw the
     * wrapped exception, otherwise do nothing.
     *
     * @return The {@link Result}, for chaining
     * @throws Throwable If {@code throwable} is not null and is rethrown
     */
    @NonNull
    public Result<T> rethrowUnchecked() {
        if (null != this.throwable) {
            ExceptionHandler.unchecked(this.throwable);
        }
        return this;
    }

    /**
     * If a throwable is present, it will be thrown as an exception. Otherwise, the current instance
     * will be returned.
     *
     * @return The {@link Result}, for chaining
     * @throws Throwable If {@code throwable} is not null
     */
    @NonNull
    public Result<T> rethrow() throws Throwable {
        if (null != this.throwable)
            throw this.throwable;
        return this;
    }

    /**
     * If a throwable is present in this {@link Result}, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @return The non-null throwable held by this {@link Result}
     * @throws NoSuchElementException If there is no throwable present
     * @see Result#caught()
     */
    @NonNull
    public Throwable error() {
        if (null == this.throwable) {
            throw new NoSuchElementException("No value present");
        }
        return this.throwable;
    }

    /**
     * If a throwable is present in this {@link Result}, returns the value, otherwise returns
     * null.
     * @return The throwable
     */
    @Nullable
    public Throwable unsafeError() {
        return this.throwable;
    }

    /**
     * Returns the type of the value, if it is present. Otherwise, returns {@code null}.
     *
     * @return The type of the value, or {@code null}
     */
    @Nullable
    public Class<?> type() {
        return this.present() ? this.value.getClass() : null;
    }

    /**
     * Checks if the value is present, and if it is, checks if it is equal to the given value.
     *
     * @param other The value to check for equality
     * @return {@code true} if the value is present and equal to {@code other}, otherwise {@code false}
     */
    public boolean equal(@Nullable final Object other) {
        return Objects.equals(this.value, other);
    }

    /**
     * If a value is present in this {@link Result}, returns the value, otherwise throws {@code
     * NoSuchElementException}.
     *
     * @return the non-null value held by this {@link Result}
     * @throws NoSuchElementException If there is no value present
     * @see Result#present()
     */
    @NonNull
    public T get() {
        if (null != this.throwable) {
            this.rethrowUnchecked();
        }
        if (null == this.value) {
            throw new NoSuchElementException("No value present");
        }
        return this.value;
    }

    /**
     * Returns a new {@link Result} containing the value if it is present, otherwise returns an
     * empty {@link Result}. If an error is present in this {@link Result}, it is discarded.
     *
     * @return A new {@link Result} containing the value if present, otherwise an empty {@link Result}.
     */
    public Result<T> discardError() {
        return Result.of(this.value);
    }

    public <R> Result<R> adjustWildcards() {
        return TypeUtils.adjustWildcards(this, Result.class);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, this.throwable);
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Result<?> other)) {
            return false;
        }

        return Objects.equals(this.value, other.value)
                && Objects.equals(this.throwable, other.throwable);
    }

    @NonNull
    @Override
    public String toString() {
        if (null != this.value && null != this.throwable) {
            return String.format("Result[%s,%s]", this.value, this.throwable);
        }
        else if (null != this.value) {
            return String.format("Result[%s,-]", this.value);
        }
        else if (null != this.throwable) {
            return String.format("Result[-,%s]", this.throwable);
        }
        return "Result.empty";
    }
}
