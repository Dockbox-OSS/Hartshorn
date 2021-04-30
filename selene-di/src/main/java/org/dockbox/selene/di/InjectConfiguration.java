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

package org.dockbox.selene.di;

import org.dockbox.selene.di.annotations.BindingMeta;
import org.dockbox.selene.di.inject.Binder;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

public abstract class InjectConfiguration implements Binder {

    private Binder binder;

    public final void setBinder(Binder binder) {
        this.binder = binder;
    }

    public abstract void collect();

    public final Binder getBinder() {
        if (this.binder == null) throw new IllegalStateException("No binder provided!");
        return this.binder;
    }

    @Override
    public final <C, T extends C, A extends Annotation> void provide(Class<C> contract, Supplier<? extends T> supplier) {
        this.getBinder().provide(contract, supplier);
    }

    @Override
    public final <C, T extends C, A extends Annotation> void provide(Class<C> contract, Supplier<? extends T> supplier, BindingMeta meta) {
        this.getBinder().provide(contract, supplier, meta);
    }

    @Override
    public final <C, T extends C> void bind(Class<C> contract, Class<? extends T> implementation) {
        this.getBinder().bind(contract, implementation);
    }

    @Override
    public final <C, T extends C, A extends Annotation> void bind(Class<C> contract, Class<? extends T> implementation, BindingMeta meta) {
        this.getBinder().bind(contract, implementation, meta);
    }

    @Override
    public final <C, T extends C> void bind(Class<C> contract, T instance) {
        this.getBinder().bind(contract, instance);
    }

    @Override
    public final <C, T extends C> void bind(Class<C> contract, T instance, BindingMeta meta) {
        this.getBinder().bind(contract, instance, meta);
    }

    @Override
    public <C, T extends C> void wire(Class<C> contract, Class<? extends T> implementation) {
        this.getBinder().bind(contract, implementation);
    }
}
