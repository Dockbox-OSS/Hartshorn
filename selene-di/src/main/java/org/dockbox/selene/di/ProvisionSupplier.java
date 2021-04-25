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

import org.dockbox.selene.di.properties.InjectorProperty;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class ProvisionSupplier {
    
    private final BiFunction<Class<?>, InjectorProperty<?>[], ?> function;
    private final BiPredicate<Class<?>, InjectorProperty<?>[]> predicate;

    public ProvisionSupplier(BiFunction<Class<?>, InjectorProperty<?>[], ?> function, BiPredicate<Class<?>, InjectorProperty<?>[]> predicate) {
        this.function = function;
        this.predicate = predicate;
    }

    public Object provide(Class<?> type, InjectorProperty<?>... properties) {
        return this.function.apply(type, properties);
    }

    public boolean validate(Class<?> type, InjectorProperty<?>... properties) {
        return this.predicate.test(type, properties);
    }
}
