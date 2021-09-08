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

package org.dockbox.hartshorn.di.binding;

import org.dockbox.hartshorn.di.context.element.TypeContext;

import java.util.function.Supplier;

public class Providers {

    public static <C> Provider<C> bound(final TypeContext<? extends C> type) {
        return new BoundFactoryProvider<>(type);
    }

    public static <C> Provider<C> bound(final Class<? extends C> type) {
        return bound(TypeContext.of(type));
    }

    public static <C> Provider<C> of(final TypeContext<? extends C> type) {
        return new ContextDrivenProvider<>(type);
    }

    public static <C> Provider<C> of(final Class<? extends C> type) {
        return of(TypeContext.of(type));
    }

    public static <C> Provider<C> of(final C instance) {
        return new InstanceProvider<>(instance);
    }

    public static <C> Provider<C> of(final Supplier<C> supplier) {
        return new SupplierProvider<>(supplier);
    }

}
