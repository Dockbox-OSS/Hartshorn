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

package org.dockbox.hartshorn.di.inject;

import org.dockbox.hartshorn.di.annotations.inject.Named;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

public interface DelegatedBinder extends Binder {

    Binder getBinder();
    
    @Override
    default <C, T extends C, A extends Annotation> void provide(Class<C> contract, Supplier<? extends T> supplier) {
        this.getBinder().provide(contract, supplier);
    }

    @Override
    default <C, T extends C, A extends Annotation> void provide(Class<C> contract, Supplier<? extends T> supplier, Named meta) {
        this.getBinder().provide(contract, supplier, meta);
    }

    @Override
    default <C, T extends C> void bind(Class<C> contract, Class<? extends T> implementation) {
        this.getBinder().bind(contract, implementation);
    }

    @Override
    default <C, T extends C> void bind(Class<C> contract, Class<? extends T> implementation, Named meta) {
        this.getBinder().bind(contract, implementation, meta);
    }

    @Override
    default <C, T extends C> void bind(Class<C> contract, T instance) {
        this.getBinder().bind(contract, instance);
    }

    @Override
    default <C, T extends C> void bind(Class<C> contract, T instance, Named meta) {
        this.getBinder().bind(contract, instance, meta);
    }

    @Override
    default  <C, T extends C> void wire(Class<C> contract, Class<? extends T> implementation) {
        this.getBinder().wire(contract, implementation);
    }

    @Override
    default  <C, T extends C> void wire(Class<C> contract, Class<? extends T> implementation, Named meta) {
        this.getBinder().wire(contract, implementation, meta);
    }
    
}
