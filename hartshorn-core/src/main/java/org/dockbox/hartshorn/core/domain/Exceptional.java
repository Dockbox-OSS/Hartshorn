/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.domain;

import org.dockbox.hartshorn.core.boot.ExceptionHandler;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.function.CheckedBiFunction;
import org.dockbox.hartshorn.core.function.CheckedFunction;
import org.dockbox.hartshorn.core.function.CheckedSupplier;
import org.jetbrains.annotations.Nullable;

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
 */
public final class Exceptional<T> {

    private static final Exceptional<?> EMPTY = new Exceptional<>();

    @Nullable
    private final T value;
    private final Throwable throwable;

    private Exceptional() {
        this.value = null;
        this.throwable = null;
    }

    private Exceptional(final T value) {
        this.value = Objects.requireNonNull(value);
        this.throwable = null;
    }

    private Exceptional(final T value, final Throwable throwable) {
        this.value = Objects.requireNonNull(value);
        this.throwable = Objects.requireNonNull(throwable);
    }

    private Exceptional(final Throwable throwable) {
        this.value = null;
        this.throwable = Objects.requireNonNull(throwable);
    }

    /**
     * Provides a {@link Exceptional} instance based on a provided {@link Optional} instance. If the
     * optional contains a value, it is unwrapped and wrapped in {@link Exceptional#of(Object)}. If
     * the optional doesn't contain a value, {@link Exceptional#empty()} is returned.
     *
     * @param <T> The type parameter of the potential value
     * @param optional The {@link Optional} instance to wrap
     *
     * @return The {@link Exceptional}
     */
    public static <T> Exceptional<T> of(final Optional<T> optional) {
        return optional.map(Exceptional::of).orElseGet(Exceptional::empty);
    }

    /**
     * Provides a {@link Exceptional} instance which can contain a value in {@link Exceptional#value}.
     * The value can be null. If the value is null, {@link Exceptional#empty()} is returned.
     *
     * @param <T> The type parameter of the potential value
     * @param value The potential value to wrap
     *
     * @return The {@link Exceptional}
     */
    public static <T> Exceptional<T> of(@Nullable final T value) {
        return null == value ? empty() : new Exceptional<>(value);
    }

    /**
     * Provides a {@link Exceptional} instance which contains no {@link Exceptional#value value} and
     * no {@link Exceptional#throwable throwable}. The returned instance is a cast copy of {@link
     * Exceptional#EMPTY}
     *
     * @param <T> The type parameter of the value which the instance is cast to
     *
     * @return The none {@link Exceptional}
     */
    public static <T> Exceptional<T> empty() {
        return (Exceptional<T>) EMPTY;
    }

    /**
     * Provides a {@link Exceptional} instance based on a provided {@link CheckedSupplier}. If the supplier
     * throws any type of {@link Throwable} {@link Exceptional#of(Throwable)} is returned, describing
     * the thrown throwable. If the supplier successfully provided a value, {@link
     * Exceptional#of(Object)} is returned. This also means suppliers can return nullable
     * values.
     *
     * @param <T> The type parameter of the potential value
     * @param supplier The {@link CheckedSupplier} instance, supplying the value
     *
     * @return The {@link Exceptional}
     */
    public static <T> Exceptional<T> of(final Callable<T> supplier) {
        try {
            return of(supplier.call());
        }
        catch (final Throwable t) {
            return of(t);
        }
    }

    /**
     * Provides a {@link Exceptional} instance based on a provided <code>condition</code>. If the condition
     * is met, the value of <code>ifTrue</code> is used. If the condition is not met the {@link Throwable}
     * provided by <code>ifFalseException</code> is used.
     *
     * @param condition The condition
     * @param ifTrue The value if the condition is met
     * @param ifFalseException The throwable if the condition is not met
     * @param <T> The type of the value
     *
     * @return The {@link Exceptional}
     */
    public static <T> Exceptional<T> of(final Callable<Boolean> condition, final Callable<T> ifTrue, final Supplier<Throwable> ifFalseException) {
        return of(condition, ifTrue, () -> null, ifFalseException);
    }

    /**
     * Provides a {@link Exceptional} instance based on a provided <code>condition</code>. If the condition
     * is met, the value of <code>ifTrue</code> is used. If the condition is not met the {@link Throwable}
     * provided by <code>ifFalseException</code> is used together with the value of <code>ifFalse</code>.
     *
     * @param condition The condition
     * @param ifTrue The value if the condition is met
     * @param ifFalse The value if the condition is not met
     * @param ifFalseException The throwable if the condition is not met
     * @param <T> The type of the value
     *
     * @return The {@link Exceptional}
     */
    public static <T> Exceptional<T> of(final Callable<Boolean> condition, final Callable<T> ifTrue, final Supplier<T> ifFalse, final Supplier<Throwable> ifFalseException) {
        try {
            if (condition.call()) return of(ifTrue);
            else return of(ifFalse.get(), ifFalseException.get());
        }
        catch (final Throwable e) {
            return of(e);
        }
    }

    /**
     * Return the value if present, otherwise invoke {@code other} and return the result of that
     * invocation.
     *
     * @param other A {@code Supplier} whose result is returned if no value is present
     *
     * @return The value if present otherwise the result of {@code other.get()}
     * @throws NullPointerException if value is not present and {@code other} is null
     */
    public T get(final Supplier<? extends T> other) {
        return null != this.value ? this.value : other.get();
    }

    /**
     * If a value is present, invoke the specified consumer with the value, otherwise do nothing.
     *
     * @param consumer Block to be executed if a value is present
     *
     * @return The {@link Exceptional}, for chaining
     * @throws NullPointerException If value is present and {@code consumer} is null
     */
    public Exceptional<T> present(final Consumer<? super T> consumer) {
        if (null != this.value) consumer.accept(this.value);
        return this;
    }

    /**
     * Return {@code true} if there is no value present, otherwise {@code false}. Acts as an inverse of
     * {@link Exceptional#present()}.
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
     * @return The {@link Exceptional}, for chaining
     * @throws NullPointerException If {@code runnable} is null
     */
    public Exceptional<T> absent(final Runnable runnable) {
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
     * @see Exceptional#orNull()
     */
    public T or(final T other) {
        return null != this.value ? this.value : other;
    }

    /**
     * Return the throwable if present, otherwise return {@code other}.
     *
     * @param other The value to be returned if there is no throwable present, may be null
     *
     * @return The throwable, if present, otherwise {@code other}
     */
    public Throwable or(final Throwable other) {
        return null != this.throwable ? this.throwable : other;
    }

    /**
     * If a value is present, apply the provided {@link Exceptional}-bearing mapping function to it,
     * return that result, otherwise return {@link Exceptional#empty()}. This method is similar to
     * {@link Exceptional#map(CheckedFunction)}, but the provided mapper is one whose result is already an
     * {@link Exceptional}, and if invoked, {@code then} does not wrap it with an additional {@code
     * Exceptional}.
     *
     * @param <U> The type parameter to the {@link Exceptional} returned
     * @param mapper A mapping function to apply to the value, if present
     *
     * @return The result of applying an {@link Exceptional}-bearing mapping function to the value of this {@link Exceptional}, if a value is present, otherwise {@link Exceptional#empty()}
     * @throws NullPointerException If the mapping function is null or returns a null result
     */
    public <U> Exceptional<U> flatMap(final CheckedFunction<? super T, Exceptional<U>> mapper) {
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
     * Provides a {@link Exceptional} instance which contains no value, but contains the given
     * throwable as {@link Exceptional#throwable}. This requires the provided throwable to be
     * non-null. If the throwable is null, a {@link NullPointerException} is thrown.
     *
     * @param <T> The type parameter of the empty value
     * @param throwable The throwable to wrap
     *
     * @return The {@link Exceptional}
     * @throws NullPointerException When the provided value is null
     */
    public static <T> Exceptional<T> of(final Throwable throwable) {
        return new Exceptional<>(throwable);
    }

    /**
     * If a value is present, apply the provided {@link Exceptional}-bearing mapping function to both
     * the value and throwable described by this {@link Exceptional}, return that result, otherwise
     * return {@link Exceptional#empty()}. This method is similar to {@link
     * Exceptional#flatMap(CheckedFunction)}, but the provided mapper is one whose input is both a {@code
     * Throwable} and a value of type {@code T}.
     *
     * @param <U> The type parameter to the {@link Exceptional} returned
     * @param mapper A mapping function to apply to the value and throwable, if present
     *
     * @return The result of applying an {@link Exceptional}-bearing mapping function to the value and throwable of this {@link Exceptional}, if a value is present, otherwise {@link Exceptional#empty()}
     * @throws NullPointerException If the mapping function is null or returns a null result
     */
    public <U> Exceptional<U> flatMap(final CheckedBiFunction<? super T, Throwable, Exceptional<U>> mapper) {
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
     * Return a {@link Exceptional} instance holding the value if present, otherwise invoke {@code
     * defaultValue} and return the result of that invocation, combined with a throwable if a
     * throwable is present. This method is similar to {@link Exceptional#get(Supplier)}, but
     * instead of returning the value, the result is wrapped in a {@link Exceptional}.
     *
     * @param defaultValue A {@code Supplier} whose result is wrapped if no value is present
     *
     * @return The {@link Exceptional}, for chaining
     * @throws NullPointerException If a value is present and {@code defaultValue} is null
     */
    public Exceptional<T> orElse(final CheckedSupplier<T> defaultValue) {
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
     * If a value is present, and the value matches the given predicate, return an {@link Exceptional}
     * describing value, otherwise return {@link Exceptional#empty()}.
     *
     * @param predicate A predicate to apply to the value, if present
     *
     * @return an {@link Exceptional} describing the value of this {@link Exceptional} if a value is present and the value matches the given predicate, otherwise {@link Exceptional#empty()}
     * @throws NullPointerException If the predicate is null
     */
    public Exceptional<T> filter(final Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!this.present()) return this;
        else return predicate.test(this.value) ? this : empty();
    }

    /**
     * If a value is present, apply the provided mapping function to it, and if the result is
     * non-null, return an {@link Exceptional} describing the result. Otherwise, return {@link
     * Exceptional#empty()}.
     *
     * <pre>{@code
     * Exceptional<TextChannel> textChannel = JDAUtils.getJDA()
     * .map(jda -> jda.getTextChannelById(channelId));
     * }</pre>
     *
     * <p>Here, {@code getJDA} returns an {@code Exceptional<JDA>}, and then {@code map} returns an
     * {@code Exceptional<TextChannel>} for the desired channel if one exists.
     *
     * @param <U> The type of the result of the mapping function
     * @param mapper A mapping function to apply to the value, if present
     *
     * @return an {@link Exceptional} describing the result of applying a mapping function to the value of this {@link Exceptional}, if a value is present, otherwise {@link Exceptional#empty()}
     * @throws NullPointerException If the mapping function is null
     */
    public <U> Exceptional<U> map(final CheckedFunction<? super T, ? extends U> mapper) {
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
     * Provides a {@link Exceptional} instance which can contain both a value in {@link
     * Exceptional#value} and a throwable in {@link Exceptional#throwable}. Both the value and
     * throwable can be null.
     *
     * <ul>
     *   <li>If the value is null and the throwable is not null, {@link Exceptional#of(Throwable)} is
     *       used to generate the instance.
     *   <li>If the value is not null and the throwable is null, {@link Exceptional#of(Object)} is
     *       used to generate the instance.
     *   <li>If both the value and the throwable are null, {@link Exceptional#empty()} is used to
     *       generate the new instance.
     * </ul>
     *
     * @param <T> The type parameter of the potential value
     * @param value The potential value to wrap
     * @param throwable The potential throwable to wrap
     *
     * @return The {@link Exceptional}
     */
    public static <T> Exceptional<T> of(@Nullable final T value, @Nullable final Throwable throwable) {
        if (null == value && null == throwable) return empty();
        else if (null == value) return of(throwable);
        else if (null == throwable) return of(value);
        else return new Exceptional<>(value, throwable);
    }

    /**
     * If a throwable is present, invoke the specified {@code consumer} with the throwable, otherwise
     * do nothing.
     *
     * @param consumer The block to be executed if a throwable is present
     *
     * @return The {@link Exceptional}, for chaining
     * @throws NullPointerException If throwable is present and {@code consumer} is null
     */
    public Exceptional<T> caught(final Consumer<? super Throwable> consumer) {
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
     * @throws NullPointerException If no value is present and {@code exceptionSupplier} is null
     */
    public <X extends Throwable> T orThrow(final Supplier<? extends X> exceptionSupplier) throws X {
        if (null != this.value) {
            return this.value;
        }
        else {
            final X exception = exceptionSupplier.get();
            if (exception != null)
                throw exception;
        }
        return null;
    }

    /**
     * Return the contained value, if present, otherwise throw the provided exception. The provided
     * exception is rethrown as unchecked, so it need not be declared in a throws clause.
     *
     * @param exceptionSupplier The supplier which will return the exception to be thrown.
     * @return The present value
     * @throws Throwable If there is no value present
     */
    public T orThrowUnchecked(final Supplier<Throwable> exceptionSupplier) {
        if (null != this.value) {
            return this.value;
        }
        else {
            final Throwable exception = exceptionSupplier.get();
            if (exception != null)
                ExceptionHandler.unchecked(exception);
        }
        return null;
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
     * @return The {@link Exceptional}, for chaining
     * @throws NullPointerException If {@code runnable} is null
     */
    public Exceptional<T> ifErrorAbsent(final Runnable runnable) {
        if (null == this.throwable) runnable.run();
        return this;
    }

    /**
     * If a throwable is present, wrap it in a new {@link RuntimeException} and throw the
     * wrapped exception, otherwise do nothing.
     *
     * @return The {@link Exceptional}, for chaining
     * @throws RuntimeException If {@code throwable} is not null and is rethrown
     */
    public Exceptional<T> rethrowUnchecked() {
        if (null != this.throwable) {
            if (this.throwable instanceof RuntimeException) throw (RuntimeException) this.throwable;
            else ExceptionHandler.unchecked(this.throwable);
        }
        return this;
    }

    /**
     * If a throwable is present, it will be thrown as an exception. Otherwise, the current instance
     * will be returned.
     *
     * @return The {@link Exceptional}, for chaining
     * @throws Throwable If {@code throwable} is not null
     */
    public Exceptional<T> rethrow() throws Throwable {
        if (null != this.throwable)
            throw this.throwable;
        return this;
    }

    /**
     * If a throwable is present in this {@link Exceptional}, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @return The non-null throwable held by this {@link Exceptional}
     * @throws NoSuchElementException If there is no throwable present
     * @see Exceptional#caught()
     */
    public Throwable error() {
        if (null == this.throwable) {
            throw new NoSuchElementException("No value present");
        }
        return this.throwable;
    }

    /**
     * If a throwable is present in this {@link Exceptional}, returns the value, otherwise returns
     * null.
     * @return The throwable
     */
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
    public boolean equal(final Object other) {
        return this.present() && this.get().equals(other);
    }

    /**
     * If a value is present in this {@link Exceptional}, returns the value, otherwise throws {@code
     * NoSuchElementException}.
     *
     * @return the non-null value held by this {@code Optional}
     * @throws NoSuchElementException If there is no value present
     * @see Exceptional#present()
     */
    public T get() {
        if (null == this.value) {
            throw new NoSuchElementException("No value present");
        }
        return this.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, this.throwable);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Exceptional<?> other)) {
            return false;
        }

        return Objects.equals(this.value, other.value)
                && Objects.equals(this.throwable, other.throwable);
    }

    @Override
    public String toString() {
        if (null != this.value && null != this.throwable) {
            return String.format("Exceptional[%s,%s]", this.value, this.throwable);
        }
        else if (null != this.value) {
            return String.format("Exceptional[%s,-]", this.value);
        }
        else if (null != this.throwable) {
            return String.format("Exceptional[-,%s]", this.throwable);
        }
        return "Exceptional.none";
    }
}
