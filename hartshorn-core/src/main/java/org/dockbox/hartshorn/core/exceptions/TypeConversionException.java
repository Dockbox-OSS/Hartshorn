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

package org.dockbox.hartshorn.core.exceptions;

import org.dockbox.hartshorn.core.context.element.TypeContext;

public class TypeConversionException extends Exception {

    public TypeConversionException(final Class<?> type, final String value) {
        this(TypeContext.of(type), value);
    }

    public TypeConversionException(final TypeContext<?> type, final String value) {
        super("Could not convert '" + value + "' to type " + type.name());
    }

    public TypeConversionException(final TypeContext<?> type, final String value, final Throwable cause) {
        super("Could not convert '" + value + "' to type " + type.name(), cause);
    }
}
