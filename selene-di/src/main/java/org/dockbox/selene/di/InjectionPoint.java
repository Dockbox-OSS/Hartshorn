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
import org.dockbox.selene.util.Reflect;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class InjectionPoint<T> {

    private final Class<T> type;
    private final InjectFunction<T> point;

    private InjectionPoint(Class<T> type, InjectFunction<T> point) {
        this.type = type;
        this.point = point;
    }

    public static <T> InjectionPoint<T> of(Class<T> type, Function<T, T> point) {
        return new InjectionPoint<>(type, (instance, it, properties) -> point.apply(instance));
    }

    public static <T> InjectionPoint<T> of(Class<T> type, BiFunction<T, InjectorProperty<?>[], T> point) {
        return new InjectionPoint<>(type, (instance, it, properties) -> point.apply(instance, properties));
    }

    public static <T> InjectionPoint<T> of(Class<T> type, InjectFunction<T> point) {
        return new InjectionPoint<>(type, point);
    }

    public boolean accepts(Class<?> type) {
        return Reflect.assignableFrom(this.type, type);
    }

    public T apply(T instance, Class<T> type, InjectorProperty<?>... properties) {
        return this.point.apply(instance, type, properties);
    }

    public T apply(T instance) {
        //noinspection unchecked
        return this.apply(instance, (Class<T>) instance.getClass());
    }

    public T apply(T instance, InjectorProperty<?>... properties) {
        //noinspection unchecked
        return this.apply(instance, (Class<T>) instance.getClass(), properties);
    }
}
