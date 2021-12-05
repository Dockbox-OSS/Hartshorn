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

/**
 * A type adapter that converts a string to a potential value of type {@code T}. If the conversion fails,
 * {@link Exceptional#empty()} is returned.
 *
 * @param <T> The type to convert to
 * @author Guus Lieben
 * @since 4.2.4
 */
public interface StringTypeAdapter<T> {
    /**
     * Converts a string to a potential value of type {@code T}. If the conversion fails, {@link Exceptional#empty()} is
     * returned.
     *
     * @param value The string to convert
     * @return The converted value
     */
    Exceptional<T> adapt(String value);

    /**
     * Gets the target type of the current adapter.
     *
     * @return The target type
     */
    Class<T> type();
}
