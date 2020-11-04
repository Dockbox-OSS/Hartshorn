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

package org.dockbox.selene.core.objects.optional;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("AssignmentToNull")
public final class Exceptional<T> {

    private static final Exceptional<?> EMPTY = new Exceptional<>();

    private final T value;
    private final Throwable throwable;

    private Exceptional() {
        this.value = null;
        this.throwable = null;
    }

    private Exceptional(T value) {
        this.value = Objects.requireNonNull(value);
        this.throwable = null;
    }

    private Exceptional(T value, Throwable throwable) {
        this.value = Objects.requireNonNull(value);
        this.throwable = Objects.requireNonNull(throwable);
    }

    private Exceptional(Throwable throwable) {
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
        return null == value || null == throwable ? empty() : of(value, throwable);
    }

    public static <T> Exceptional<T> ofNullable(T value) {
        return null == value ? empty() : of(value);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Exceptional<T> ofOptional(Optional<T> optional) {
        return optional.map(Exceptional::of).orElseGet(Exceptional::empty);
    }

    public static <T> Exceptional<T> ofSupplier(Supplier<T> supplier) {
        try {
            return ofNullable(supplier.get());
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
            return empty();
        else {
            return Exceptional.ofNullable(mapper.apply(this.value));
        }
    }

    public<U> Exceptional<U> flatMap(Function<? super T, Exceptional<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!this.isPresent())
            return empty();
        else {
            return Objects.requireNonNull(mapper.apply(this.value));
        }
    }

    public T orElse(T other) {
        return null != this.value ? this.value : other;
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

    public Optional<T> toOptional() {
        return Optional.ofNullable(this.value);
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
    
}
