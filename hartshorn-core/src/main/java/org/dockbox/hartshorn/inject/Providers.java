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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.function.Supplier;

/**
 * A supplier of {@link TypeContext}s. These are the default values used by the framework.
 *
 * @author Guus Lieben
 * @since 21.4
 */
public final class Providers {

    private Providers() {}

    /**
     * A provider that uses a {@link TypeContext} to create instances.
     *
     * @param type The type the provider should provide.
     * @param <C> The type the provider should provide.
     * @return A provider that uses a {@link TypeContext} to create instances.
     * @see ContextDrivenProvider
     */
    public static <C> Provider<C> of(final TypeContext<? extends C> type) {
        return new ContextDrivenProvider<>(type);
    }

    /**
     * A provider that uses a {@link Class} to create instances.
     *
     * @param type The type the provider should provide.
     * @param <C> The type the provider should provide.
     * @return A provider that uses a {@link Class} to create instances.
     * @see ContextDrivenProvider
     */
    public static <C> Provider<C> of(final Class<? extends C> type) {
        return of(TypeContext.of(type));
    }

    /**
     * A provider that uses an existing instance to provide the instance.
     *
     * @param instance The instance to provide.
     * @param <C> The type the provider should provide.
     * @return A provider that uses an existing instance to provide the instance.
     * @see InstanceProvider
     */
    public static <C> Provider<C> of(final C instance) {
        return new InstanceProvider<>(instance);
    }

    /**
     * A provider that uses a {@link Supplier} to create instances.
     *
     * @param supplier The supplier to use.
     * @param <C> The type the provider should provide.
     * @return A provider that uses a {@link Supplier} to create instances.
     * @see SupplierProvider
     */
    public static <C> Provider<C> of(final Supplier<C> supplier) {
        return new SupplierProvider<>(supplier);
    }

}
