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

package org.dockbox.selene.core.command.parse

import org.dockbox.selene.core.command.context.CommandValue
import org.dockbox.selene.core.objects.optional.Exceptional

/**
 * Low-level class to perform [CommandValue] conversions into a given generic type [T]
 *
 * @param T The generic return type
 */
abstract class AbstractTypeArgumentParser<T> : AbstractParser() {

    /**
     * Parses a given [CommandValue] with generic type [String] into the given generic type [T]. Returns
     * an [Exceptional] to allow developers to return empty results if need be.
     *
     * @param commandValue The [CommandValue] holding the key and value of a command argument or flag
     * @return The return value wrapped in [Exceptional]
     */
    abstract fun parse(commandValue: CommandValue<String>): Exceptional<T>
}
