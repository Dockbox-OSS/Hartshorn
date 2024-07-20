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

package org.dockbox.hartshorn.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A functional interface for initializing objects. This interface is similar to {@link Initializer} but
 * allows for an input parameter. This is useful for initializers that require context to initialize.
 *
 * @param <I> The type of input to initialize with.
 * @param <T> The type of object to initialize.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
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
    T initialize(SingleElementContext<? extends I> input);

    /**
     * Returns an initializer that invokes the given initializer, ignoring the input value.
     * This is useful for initializers that do not require context to initialize, but need to be
     * provided to a configurer that requires a {@link ContextualInitializer}.
     *
     * @param initializer The initializer to invoke.
     * @param <I> The type of input to initialize with.
     * @param <T> The type of object to initialize.
     *
     * @return An initializer that invokes the given initializer, ignoring the input value.
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
     * @param <I> The type of input to initialize with.
     * @param <T> The type of object to initialize.
     *
     * @return An initializer that invokes the given function, ignoring the wrapper context.
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
     * @param <I> The type of input to initialize with.
     * @param <T> The type of object to initialize.
     *
     * @return An initializer that will always return the given object.
     */
    static <I, T> ContextualInitializer<I, T> of(T object) {
        return (input) -> object;
    }

    /**
     * Returns an initializer that defers to the given initializer. This is useful for creating initializers
     * that require context to initialize, but do not require the context to be known at the time of creation.
     *
     * @param initializer The initializer to delegate to.
     * @param <I1> The (weak) input type of the given initializer.
     * @param <I2> The (strong) input type of the returned initializer.
     * @param <T> The type of object to initialize.
     *
     * @return An initializer that delegates to the given initializer.
     */
    static <I1, I2 extends I1, T> ContextualInitializer<I2, T> defer(Supplier<ContextualInitializer<I1, T>> initializer) {
        return input -> initializer.get().initialize(input);
    }

    /**
     * Returns an initializer that caches the result of this initializer. When the returned initializer is invoked,
     * the result of this initializer is cached and returned on subsequent invocations with the same input value.
     * This is useful for expensive initializers, or for easy initialization of singletons.
     *
     * @return An initializer that caches the result of this initializer.
     */
    default ContextualInitializer<I, T> cached() {
        return new CachedContextualInitializer<>(this);
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

    /**
     * Returns an initializer that invokes this initializer, and then invokes the given consumer with the original
     * input and the result. This is useful for listening to the result of an initializer, without needing to invoke
     * the initializer directly.
     *
     * @param consumer The consumer to invoke with the original input and the result of this initializer.
     * @return An initializer that invokes this initializer, and then invokes the given consumer with the original
     *         input and the result.
     */
    default ContextualInitializer<I, T> subscribe(BiConsumer<I, T> consumer) {
        return (input) -> {
            T instance = ContextualInitializer.this.initialize(input);
            consumer.accept(input.input(), instance);
            return instance;
        };
    }
}
