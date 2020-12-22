/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.objects;

import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.BiFunction;

/**
 * A container object which may or may not contain a non-null value. If a value is present, {@code isPresent()} will
 * return {@code true} and {@code get()} will return the value. If no value is present, {@code isAbsent()} will
 * return {@code false}.
 *
 * Additional methods that depend on the presence or absence of a contained value are provided, such as
 * {@link #orElse(java.lang.Object) orElse()} (return a default value if value not present) and
 * {@link #ifPresent(java.util.function.Consumer) ifPresent()} (execute a block of code if the value is present).
 *
 * This is a extended type of {@link Optional}, providing additional support for {@link Exception} checks and actions.
 * Additionally it allows for more abilities to construct the type from a {@link Callable}, {@link Optional} and to
 * create from a {@link Throwable} instance.
 */
@SuppressWarnings("AssignmentToNull")
public final class Exceptional<T> extends ConstructNotifier<Exceptional> {

    private static final Exceptional<?> EMPTY = new Exceptional<>();

    private final T value;
    private final Throwable throwable;

    private Exceptional() {
        super(Exceptional.class);
        this.value = null;
        this.throwable = null;
    }

    private Exceptional(T value) {
        super(Exceptional.class);
        this.value = Objects.requireNonNull(value);
        this.throwable = null;
    }

    private Exceptional(T value, Throwable throwable) {
        super(Exceptional.class);
        this.value = Objects.requireNonNull(value);
        this.throwable = Objects.requireNonNull(throwable);
    }

    private Exceptional(Throwable throwable) {
        super(Exceptional.class);
        this.value = null;
        this.throwable = Objects.requireNonNull(throwable);
    }

    public static<T> Exceptional<T> empty() {
        @SuppressWarnings("unchecked")
        Exceptional<T> t = (Exceptional<T>) EMPTY;
        return t;
    }

    public static <T> Exceptional<T> of(T value) {
        return new Exceptional<>(value);
    }

    public static <T> Exceptional<T> of(Throwable throwable) {
        return new Exceptional<>(throwable);
    }

    public static <T> Exceptional<T> of(T value, Throwable throwable) {
        return new Exceptional<>(value, throwable);
    }

    public static <T> Exceptional<T> ofNullable(T value, Throwable throwable) {
        if (null == value && null == throwable) return empty();
        if (null == value) return of(throwable);
        if (null == throwable) return of(value);
        else return of(value, throwable);
    }

    public static <T> Exceptional<T> ofNullable(T value) {
        return null == value ? empty() : of(value);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Exceptional<T> of(Optional<T> optional) {
        return optional.map(Exceptional::of).orElseGet(Exceptional::empty);
    }

    public static <T> Exceptional<T> of(Callable<T> supplier) {
        try {
            return ofNullable(supplier.call());
        } catch (Throwable t) {
            return of(t);
        }
    }

    public T get() {
        if (null == this.value) {
            throw new NoSuchElementException("No value present");
        }
        return this.value;
    }

    public boolean isPresent() {
        return null != this.value;
    }

    public boolean isAbsent() {
        return null == this.value;
    }

    public Exceptional<T> ifPresent(Consumer<? super T> consumer) {
        if (null != this.value)
            consumer.accept(this.value);
        return this;
    }

    public Exceptional<T> ifAbsent(Runnable runnable) {
        if (null == this.value)
            runnable.run();
        return this;
    }

    public Exceptional<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!this.isPresent())
            return this;
        else
            return predicate.test(this.value) ? this : empty();
    }

    public<U> Exceptional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!this.isPresent())
            return this.errorPresent() ? of(this.throwable) : empty();
        else {
            return ofNullable(mapper.apply(this.value), this.throwable);
        }
    }

    public<U> Exceptional<U> flatMap(Function<? super T, Exceptional<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!this.isPresent())
            return this.errorPresent() ? of(this.throwable) : empty();
        else {
            return Objects.requireNonNull(mapper.apply(this.value));
        }
    }

    public<U> Exceptional<U> flatMap(BiFunction<? super T, Throwable, Exceptional<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!this.isPresent())
            return this.errorPresent() ? of(this.throwable) : empty();
        else {
            return Objects.requireNonNull(mapper.apply(this.value, this.throwable));
        }
    }

    public Throwable orElseExcept(Throwable other) {
        return null != this.throwable ? this.throwable : other;
    }

    public T orElse(T other) {
        return null != this.value ? this.value : other;
    }

    public T orNull() {
        return orElse(null);
    }

    public T orElseGet(Supplier<? extends T> other) {
        return null != this.value ? this.value : other.get();
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (null != this.value) {
            return this.value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    public boolean errorPresent() {
        return null != this.throwable;
    }

    public boolean errorAbsent() { return null == this.throwable; }

    public Exceptional<T> ifErrorPresent(Consumer<? super Throwable> consumer) {
        if (null != this.throwable)
            consumer.accept(this.throwable);
        return this;
    }

    public Exceptional<T> ifErrorAbsent(Runnable runnable) {
        if (null == this.throwable)
            runnable.run();
        return this;
    }

    public Exceptional<T> rethrow() throws Throwable {
        if (null != this.throwable)
            throw this.throwable;
        return this;
    }

    public Throwable getError() {
        if (null == this.throwable) {
            throw new NoSuchElementException("No value present");
        }
        return this.throwable;
    }

    @Nullable
    public Class<?> getType() {
        return this.isPresent() ? this.value.getClass() : Void.class;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Exceptional)) {
            return false;
        }

        Exceptional<?> other = (Exceptional<?>) obj;
        return Objects.equals(this.value, other.value) && Objects.equals(this.throwable, other.throwable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, this.throwable);
    }

    @Override
    public String toString() {
        if (null != this.value && null != this.throwable) {
            return String.format("Exceptional[%s,%s]", this.value, this.throwable);
        } else if (null != this.value) {
            return String.format("Exceptional[%s,-]", this.value);
        } else if (null != this.throwable) {
            return String.format("Exceptional[-,%s]", this.throwable);
        }
        return "Exceptional.empty";
    }

    public Exceptional<T> orElseSupply(Supplier<T> defaultValue) {
        if (this.isAbsent()) {
            if (this.errorPresent()) {
                return of(defaultValue.get(), this.throwable);
            } else {
                return of(defaultValue.get());
            }
        }
        return this;
    }
}
