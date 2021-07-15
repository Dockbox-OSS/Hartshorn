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

public interface Binder {

    <C, T extends C, A extends Annotation> void provide(Class<C> contract, Supplier<? extends T> supplier);
    <C, T extends C, A extends Annotation> void provide(Class<C> contract, Supplier<? extends T> supplier, Named meta);
    <C, T extends C> void bind(Class<C> contract, Class<? extends T> implementation);
    <C, T extends C> void bind(Class<C> contract, Class<? extends T> implementation, Named meta);
    <C, T extends C> void bind(Class<C> contract, T instance);
    <C, T extends C> void bind(Class<C> contract, T instance, Named meta);
    <C, T extends C> void manual(Class<C> contract, Class<? extends T> implementation);
    <C, T extends C> void manual(Class<C> contract, Class<? extends T> implementation, Named meta);

}
