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

package org.dockbox.hartshorn.commands.context;

import org.dockbox.hartshorn.di.context.element.ParameterContext;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Type used to store a {@link java.lang.reflect.Method}'s parameter and the index of said
 * parameter.
 */
@AllArgsConstructor
@Getter
public class CommandParameterContext {

    private final ParameterContext<?> parameter;
    private final int index;

    /**
     * Checks if the provided type is equal to, or a supertype of, the stored parameter's type.
     *
     * @param type
     *         The type to compare against
     *
     * @return <code>true</code> if the provided type is equal or a supertype, else <code>false</code>
     */
    public boolean is(final Class<?> type) {
        return this.parameter().type().childOf(type);
    }

}
