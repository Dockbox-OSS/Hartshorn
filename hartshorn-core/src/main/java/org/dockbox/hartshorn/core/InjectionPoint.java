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

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.context.element.TypeContext;

import java.util.function.Function;

public final class InjectionPoint<T> {

    private final TypeContext<T> type;
    private final InjectFunction<T> point;

    private InjectionPoint(final TypeContext<T> type, final InjectFunction<T> point) {
        this.type = type;
        this.point = point;
    }

    public static <T> InjectionPoint<T> of(final TypeContext<T> type, final Function<T, T> point) {
        return new InjectionPoint<>(type, (instance, it) -> point.apply(instance));
    }

    public static <T> InjectionPoint<T> of(final TypeContext<T> type, final InjectFunction<T> point) {
        return new InjectionPoint<>(type, point);
    }

    public boolean accepts(final TypeContext<?> type) {
        return type.childOf(this.type);
    }

    public T apply(final T instance) {
        return this.apply(instance, TypeContext.of(instance));
    }

    public T apply(final T instance, final TypeContext<T> type) {
        return this.point.apply(instance, type);
    }
}
