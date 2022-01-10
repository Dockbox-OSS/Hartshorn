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

package org.dockbox.hartshorn.core.adapter;

import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.function.Function;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A {@link StringTypeAdapter} implementation. This implementation uses a {@link Function} to convert the {@link String}
 * to the desired type. This is useful when the conversion is not trivial, so that the converter can be defined in a
 * field instead of requiring it to be implemented it in a separate class.
 *
 * @param <T> The type to convert to.
 * @author Guus Lieben
 * @since 21.9
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringTypeAdapterImpl<T> implements StringTypeAdapter<T> {

    @Getter private final Class<T> type;
    private final Function<String, Exceptional<T>> function;

    @Override
    public Exceptional<T> adapt(final String value) {
        return this.function.apply(value);
    }

    /**
     * Creates a new {@link StringTypeAdapterImpl} instance.
     *
     * @param type The type to convert to.
     * @param function The function to use to convert the {@link String} to the desired type.
     * @param <T> The type to convert to.
     * @return The new {@link StringTypeAdapterImpl} instance.
     */
    public static <T> StringTypeAdapterImpl<T> of(final Class<T> type, final Function<String, Exceptional<T>> function) {
        return new StringTypeAdapterImpl<>(type, function);
    }
}
