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

package org.dockbox.hartshorn.core.inject;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.binding.BindingHierarchy;

import java.util.function.Supplier;

@FunctionalInterface
public interface DelegatedBinder extends Binder {

    @Override
    default <C> void bind(final Key<C> contract, final Supplier<C> supplier) {
        this.binder().bind(contract, supplier);
    }

    Binder binder();

    @Override
    default <C, T extends C> void bind(final Key<C> contract, final Class<? extends T> implementation) {
        this.binder().bind(contract, implementation);
    }

    @Override
    default <C, T extends C> void bind(final Key<C> contract, final T instance) {
        this.binder().bind(contract, instance);
    }

    @Override
    default <T> BindingHierarchy<T> hierarchy(final Key<T> key) {
        return this.binder().hierarchy(key);
    }
}
