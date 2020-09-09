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
import org.dockbox.darwin.core.command.parse.AbstractTypeArgumentParser
import java.util.*
import kotlin.collections.HashMap

class MapArgumentParser : AbstractTypeArgumentParser<Map<String, String>>() {
    override fun parse(commandValue: CommandValue<String>): Optional<Map<String, String>> {
        val v = commandValue.value
        val map = HashMap<String, String>()
        for (s in v.split(',')) {
            if (s.contains('=')) {
                val parts = s.split('=')
                if (parts.size == 2) {
                    map[parts[0]] = parts[1]
                }
            }
        }
        return Optional.of(map)
    }
}
