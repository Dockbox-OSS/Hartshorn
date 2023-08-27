package org.dockbox.hartshorn.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A functional interface for initializing objects. This interface is similar to {@link Initializer} but
 * allows for an input parameter. This is useful for initializers that require context to initialize.
 *
 * @param <I> The type of input to initialize with.
 * @param <T> The type of object to initialize.
 */
@FunctionalInterface
public interface LazyInitializer<I, T> {

    /**
     * Initializes the object. Implementations of this method may return the same object instance on each invocation,
     * or a new instance on each invocation.
     *
     * @param input The input to initialize with.
     * @return The initialized object.
     */
    T initialize(I input);

    /**
     * Returns an initializer that invokes the given initializer, ignoring the input value.
     * This is useful for initializers that do not require context to initialize, but need to be
     * provided to a configurer that requires a {@link LazyInitializer}.
     *
     * @param initializer The initializer to invoke.
     * @return An initializer that invokes the given initializer, ignoring the input value.
     * @param <I> The type of input to initialize with.
     * @param <T> The type of object to initialize.
     */
    static <I, T> LazyInitializer<I, T> of(final Initializer<T> initializer) {
        return input -> initializer.initialize();
    }

    /**
     * Returns an initializer that will always return the given object, ignoring the input value.
     * This is useful for initializing singletons, or otherwise pre-initialized objects that do not
     * require context to initialize.
     *
     * @param object The object to return.
     * @return An initializer that will always return the given object.
     * @param <I> The type of input to initialize with.
     * @param <T> The type of object to initialize.
     */
    static <I, T> LazyInitializer<I, T> of(final T object) {
        return input -> object;
    }

    /**
     * Returns an initializer that caches the result of this initializer. When the returned initializer is invoked,
     * the result of this initializer is cached and returned on subsequent invocations with the same input value.
     * This is useful for expensive initializers, or for easy initialization of singletons.
     *
     * @return An initializer that caches the result of this initializer.
     */
    default LazyInitializer<I, T> cached() {
        return new LazyInitializer<>() {

            private final Map<I, T> values = new ConcurrentHashMap<>();

            @Override
            public T initialize(final I input) {
                return this.values.computeIfAbsent(input, LazyInitializer.this::initialize);
            }
        };
    }
}
