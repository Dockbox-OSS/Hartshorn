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

package org.dockbox.darwin.core.command.parse.impl

import org.dockbox.darwin.core.command.context.CommandValue
import org.dockbox.darwin.core.command.parse.AbstractArgumentParser
import java.util.*

class EnumArgumentParser : AbstractArgumentParser() {

    override fun <A> parse(commandValue: CommandValue<String>, type: Class<A>?): Optional<A> {
        if (type!!.isEnum) {
            val enumConstants = type.enumConstants as Array<out Enum<*>>
            return Optional.ofNullable(enumConstants.first { it.name == commandValue.value }) as Optional<A>
        }
        return Optional.empty()
    }

}
