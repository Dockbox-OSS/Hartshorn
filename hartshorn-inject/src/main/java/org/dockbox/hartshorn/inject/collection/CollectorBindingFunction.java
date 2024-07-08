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

package org.dockbox.hartshorn.inject.collection;

import org.dockbox.hartshorn.inject.provider.PrototypeProvider;
import org.dockbox.hartshorn.inject.provider.Provider;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.util.function.CheckedSupplier;

/**
 * A specialized {@link BindingFunction} that can be used to bind multiple instances of the same key
 * to a {@link ComponentCollection}.
 *
 * @param <T> the type of the elements in the collection
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface CollectorBindingFunction<T> {

    /**
     * Binds the given provider, this will call the provider every time it is
     * requested.
     *
     * @param provider the provider to add
     * @return the binder
     */
    Binder provider(Provider<T> provider);

    /**
     * Binds the given supplier, this will call the supplier every time it is
     * requested.
     *
     * @param supplier The supplier to bind to
     * @return The binder
     */
    Binder supplier(CheckedSupplier<T> supplier);

    Binder supplier(PrototypeProvider<T> supplier);

    /**
     * Binds to the given instance, this will always return the same instance
     * every time it is requested. This may not enhance the instance before it
     * is returned.
     *
     * @param instance the instance to bind to
     * @return the binder
     */
    Binder singleton(T instance);

    /**
     * Binds to the given type, this will create a new instance of the given type
     * every time it is requested.
     *
     * @param type the type to bind to
     * @return the binder
     */
    Binder type(Class<? extends T> type);

    /**
     * Binds to a supplier that will provide a lazy instance of the given type
     * every time it is requested. This will create the instance the first time
     * it is requested and then return the same instance every time it is
     * requested.
     *
     * @param supplier the supplier to bind to
     * @return the binder
     */
    Binder lazySingleton(CheckedSupplier<T> supplier);
}
