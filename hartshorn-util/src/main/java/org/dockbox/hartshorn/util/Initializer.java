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

package org.dockbox.hartshorn.util;

/**
 * A functional interface for initializing objects. This interface is similar to {@link java.util.function.Supplier} but
 * serves as the common interface for all initializers in Hartshorn.
 *
 * @param <T> The type of object to initialize.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
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
    static <T> Initializer<T> of(T object) {
        return () -> object;
    }
}
