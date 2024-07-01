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

package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.inject.provider.Provider;
import org.dockbox.hartshorn.inject.scope.ScopeKey;
import org.dockbox.hartshorn.util.function.CheckedSupplier;

/**
 * A binding function is used to bind a provider to a specific {@link ComponentKey}. This should
 * be used by {@link Binder}s to provide a fluent API for binding providers.
 *
 * @param <T> The type of the component that is bound.
 *
 * @since 0.4.11
 *
 * @author Guus Lieben
 */
public interface BindingFunction<T> {

    /**
     * Binds the current function to the given scope.
     *
     * @param scope The scope to bind to
     * @return The binding function
     * @throws IllegalScopeException When the scope is not valid, or cannot be modified safely
     */
    BindingFunction<T> installTo(ScopeKey scope) throws IllegalScopeException;

    /**
     * Sets the priority of the binding. This will determine the order in which the binding is
     * processed. The higher the priority, the earlier the binding is processed. It remains up
     * to the {@link ComponentProvider} to determine how to process the bindings.
     *
     * @param priority The priority to set
     * @return The binding function
     */
    BindingFunction<T> priority(int priority);

    /**
     * Sets whether the result of the binding should be processed after initialization. This
     * will determine whether the result of the binding should be enhanced by
     * {@link org.dockbox.hartshorn.component.processing.ComponentProcessor}s.
     *
     * @param processAfterInitialization Whether to process after initialization
     * @return The binding function
     */
    BindingFunction<T> processAfterInitialization(boolean processAfterInitialization);

    /**
     * Binds to the given type, this will create a new instance of the given type
     * every time it is requested.
     *
     * @param type The type to bind to
     * @return The binder
     */
    Binder to(Class<? extends T> type);

    /**
     * Binds to the given supplier, this will call the supplier every time it is
     * requested.
     *
     * @param supplier The supplier to bind to
     * @return The binder
     */
    Binder to(CheckedSupplier<T> supplier);

    /**
     * Binds to the given provider, this will call the provider every time it is
     * requested.
     *
     * @param provider The provider to bind to
     * @return The binder
     */
    Binder to(Provider<T> provider);

    /**
     * Binds to the given instance, this will always return the same instance
     * every time it is requested. This may not enhance the instance before it
     * is returned.
     *
     * @param instance The instance to bind to
     * @return The binder
     */
    Binder singleton(T instance);

    /**
     * Binds to a supplier that will provide a lazy instance of the given type
     * every time it is requested. This will create the instance the first time
     * it is requested and then return the same instance every time it is
     * requested.
     *
     * @param type The type to bind to
     * @return The binder
     */
    Binder lazySingleton(Class<T> type);

    /**
     * Binds to a supplier that will provide a lazy instance of the given type
     * every time it is requested. This will request the given supplier the first
     * time it is requested and then return the same instance every time it is
     * requested.
     *
     * @param supplier The supplier to bind to
     * @return The binder
     */
    Binder lazySingleton(CheckedSupplier<T> supplier);

    /**
     * Transforms the current binding function into a {@link CollectorBindingFunction} which
     * allows for collecting multiple bindings into a single {@link ComponentCollection}.
     *
     * <p>This does not affect the current binding function, but provides a new binding function
     * that can be used to collect multiple bindings.
     *
     * @param collector The collector to use
     * @return The binder
     */
    Binder collect(Customizer<CollectorBindingFunction<T>> collector);
}
