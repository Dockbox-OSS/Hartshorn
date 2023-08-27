package org.dockbox.hartshorn.util;

/**
 * A functional interface for initializing objects. This interface is similar to {@link java.util.function.Supplier} but
 * serves as the common interface for all initializers in Hartshorn.
 *
 * @param <T> The type of object to initialize.
 */
public interface Initializer<T> {

    /**
     * Initializes the object. Implementations of this method may return the same object instance on each invocation,
     * or a new instance on each invocation.
     *
     * @return The initialized object.
     */
    T initialize();

    /**
     * Returns an initializer that caches the result of this initializer. When the returned initializer is invoked,
     * the result of this initializer is cached and returned on subsequent invocations. This is useful for expensive
     * initializers, or for easy initialization of singletons.
     *
     * @return An initializer that caches the result of this initializer.
     */
    default Initializer<T> cached() {
        return new Initializer<>() {
            private T value;

            @Override
            public T initialize() {
                if (this.value == null) {
                    this.value = Initializer.this.initialize();
                }
                return this.value;
            }
        };
    }

    /**
     * Returns an initializer that will always return the given object. This is useful for initializing singletons,
     * or otherwise pre-initialized objects.
     *
     * @param object The object to return.
     * @return An initializer that will always return the given object.
     * @param <T> The type of object to initialize.
     */
    static <T> Initializer<T> of(final T object) {
        return () -> object;
    }
}
