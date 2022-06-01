/*
 * Copyright 2019-2022 the original author or authors.
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

import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.function.Supplier;

public interface BindingFunction<T> {

    BindingFunction<T> priority(int priority);

    /**
     * Binds to the given type, this will create a new instance of the given type
     * every time it is requested.
     *
     * @param type The type to bind to
     * @return The binder
     */
    Binder to(Class<? extends T> type);

    /**
     * Binds to the given type, this will create a new instance of the given type
     * every time it is requested.
     *
     * @param type The type to bind to
     * @return The binder
     */
    Binder to(TypeContext<? extends T> type);

    /**
     * Binds to the given supplier, this will call the supplier every time it is
     * requested.
     *
     * @param supplier The supplier to bind to
     * @return The binder
     */
    Binder to(Supplier<T> supplier);

    /**
     * Binds to the given instance, this will always return the same instance
     * every time it is requested. This may not enhance the instance before it
     * is returned.
     *
     * @param t The instance to bind to
     * @return The binder
     */
    Binder singleton(T t);

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
     * every time it is requested. This will create the instance the first time
     * it is requested and then return the same instance every time it is
     * requested.
     *
     * @param type The type to bind to
     * @return The binder
     */
    Binder lazySingleton(TypeContext<T> type);

    /**
     * Binds to a supplier that will provide a lazy instance of the given type
     * every time it is requested. This will request the given supplier the first
     * time it is requested and then return the same instance every time it is
     * requested.
     *
     * @param supplier The supplier to bind to
     * @return The binder
     */
    Binder lazySingleton(Supplier<T> supplier);
}
