/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.command.context

import org.dockbox.selene.core.command.parse.AbstractTypeArgumentParser
import org.dockbox.selene.core.objects.optional.Exceptional

interface CommandContext {

    val alias: String
    val argumentCount: Int
    val flagCount: Int

    fun getArgument(key: String): Exceptional<CommandValue.Argument<String>>
    fun <T> getArgument(key: String, type: Class<T>): Exceptional<CommandValue.Argument<T>>
    fun <T> getArgumentAndParse(key: String, parser: AbstractTypeArgumentParser<T>): Exceptional<T>

    fun getFlag(key: String): Exceptional<CommandValue.Flag<String>>
    fun <T> getFlag(key: String, type: Class<T>): Exceptional<CommandValue.Flag<T>>
    fun <T> getFlagAndParse(key: String, parser: AbstractTypeArgumentParser<T>): Exceptional<T>

    fun hasArgument(key: String): Boolean
    fun hasFlag(key: String): Boolean

    fun <T> getValue(key: String, type: Class<T>, valType: CommandValue.Type): Exceptional<CommandValue<T>>
    fun <T> tryCreate(type: Class<T>): Exceptional<T>

}
