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

package org.dockbox.hartshorn.commands.definition;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the definition of a single command or collection of {@link CommandElement elements}.
 */
@AllArgsConstructor
@Getter
public class CommandDefinition {

    /**
     * Indicates whether the definition is optional. Only applies when the
     * definition represents a collection of elements.
     */
    private final boolean optional;

    /**
     * Gets all elements contained in this definition.
     */
    private final List<CommandElement<?>> elements;

    /**
     * Gets all flags contained in this definition.
     */
    private final List<CommandFlag> flags;

}
