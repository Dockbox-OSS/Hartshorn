/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.binding;

import org.dockbox.hartshorn.core.context.element.TypeContext;

import java.util.function.Supplier;

/**
 * A supplier of {@link TypeContext}s. These are the default values used by the framework.
 *
 * @author Guus Lieben
 * @since 4.1.2
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
