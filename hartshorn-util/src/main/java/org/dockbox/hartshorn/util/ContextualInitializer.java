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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A functional interface for initializing objects. This interface is similar to {@link Initializer} but
 * allows for an input parameter. This is useful for initializers that require context to initialize.
 *
 * @param <I> The type of input to initialize with.
 * @param <T> The type of object to initialize.
 */
@FunctionalInterface
public interface ContextualInitializer<I, T> {

    /**
     * Initializes the object. Implementations of this method may return the same object instance on each invocation,
     * or a new instance on each invocation.
     *
     * @param input The input to initialize with.
     * @return The initialized object.
     */
    T initialize(InitializerContext<? extends I> input);

    /**
     * Returns a map of child initializers. This is useful for initializers that require additional initializers
     * to initialize their object. This can be used to create a dependency graph of initializers, where each
     * initializer is invoked before the parent initializer, or simply to gather all initializers in a single
     * location.
     *
     * @return A map of child initializers.
     */
    default Map<Class<?>, Initializer<?>> children() {
        return Map.of();
    }

    /**
     * Returns an initializer that invokes the given initializer, ignoring the input value.
     * This is useful for initializers that do not require context to initialize, but need to be
     * provided to a configurer that requires a {@link ContextualInitializer}.
     *
     * @param initializer The initializer to invoke.
     * @return An initializer that invokes the given initializer, ignoring the input value.
     * @param <I> The type of input to initialize with.
     * @param <T> The type of object to initialize.
     */
    static <I, T> ContextualInitializer<I, T> of(Initializer<T> initializer) {
        return (input) -> initializer.initialize();
    }

    /**
     * Returns an initializer that invokes the given function using the contextual input, while ignoring
     * the remaining wrapper context. This is useful for initializers that do require the input, but do
     * not require the wrapper context to initialize.
     *
     * @param function The function to invoke.
     * @return An initializer that invokes the given function, ignoring the wrapper context.
     * @param <I> The type of input to initialize with.
     * @param <T> The type of object to initialize.
     */
    static <I, T> ContextualInitializer<I, T> of(Function<I, T> function) {
        return context -> function.apply(context.input());
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
    static <I, T> ContextualInitializer<I, T> of(T object) {
        return (input) -> object;
    }

    /**
     * Returns an initializer that caches the result of this initializer. When the returned initializer is invoked,
     * the result of this initializer is cached and returned on subsequent invocations with the same input value.
     * This is useful for expensive initializers, or for easy initialization of singletons.
     *
     * @return An initializer that caches the result of this initializer.
     */
    default ContextualInitializer<I, T> cached() {
        return new ContextualInitializer<>() {

            private final Map<I, T> values = new ConcurrentHashMap<>();

            @Override
            public T initialize(InitializerContext<? extends I> context) {
                return this.values.computeIfAbsent(context.input(), input -> ContextualInitializer.this.initialize(context));
            }
        };
    }

    /**
     * Returns an initializer that invokes this initializer, and then invokes the given consumer with the result.
     * This is useful for listening to the result of an initializer, without needing to invoke the initializer
     * directly.
     *
     * @param consumer The consumer to invoke with the result of this initializer.
     * @return An initializer that invokes this initializer, and then invokes the given consumer with the result.
     */
    default ContextualInitializer<I, T> subscribe(Consumer<T> consumer) {
        return (input) -> {
            T instance = ContextualInitializer.this.initialize(input);
            consumer.accept(instance);
            return instance;
        };
    }
}
