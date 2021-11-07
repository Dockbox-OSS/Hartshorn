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

package org.dockbox.hartshorn.core.properties;

import org.dockbox.hartshorn.core.exceptions.ApplicationException;

/**
 * @deprecated Use {@link Enableable} instead.
 */
@Deprecated(since = "4.2.3", forRemoval = true)
public interface AttributeHolder {

    /**
     * @deprecated Use {@link Enableable#enable()} instead.
     */
    @Deprecated(since = "4.2.3", forRemoval = false)
    default boolean canEnable() {
        return true;
    }

    @Deprecated(since = "4.2.3", forRemoval = true)
    default void apply(final Attribute<?> property) throws ApplicationException {
        // Optional implementation provided by inheritor
    }

    /**
     * @deprecated Use {@link Enableable#enable()} instead.
     */
    @Deprecated(since = "4.2.3", forRemoval = false)
    default void enable() throws ApplicationException {
        // Optional implementation provided by inheritor
    }
}
