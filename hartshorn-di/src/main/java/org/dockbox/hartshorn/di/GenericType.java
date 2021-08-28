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

package org.dockbox.hartshorn.di;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Generic type reference, allowing for generic type reading. This is derived
 * from Jackson's TypeReference.
 *
 * @param <T>
 *         The generic type
 */
public abstract class GenericType<T> implements Comparable<GenericType<T>> {

    protected final Type _type;

    protected GenericType() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) {
            throw new IllegalArgumentException("Internal error: GenericType constructed without actual type information");
        }
        this._type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type type() {
        return this._type;
    }

    public Exceptional<Class<T>> asClass() {
        final Type type = this.type();
        if (type instanceof Class<?> clazz) //noinspection unchecked
            return Exceptional.of((Class<T>) clazz);
        return Exceptional.empty();
    }

    @Override
    public int compareTo(@NotNull GenericType<T> o) {
        return 0;
    }

}
