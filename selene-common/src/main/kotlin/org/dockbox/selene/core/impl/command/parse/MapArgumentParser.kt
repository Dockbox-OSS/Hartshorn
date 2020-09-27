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

package org.dockbox.selene.core.impl.command.parse

import org.dockbox.selene.core.command.context.CommandValue
import org.dockbox.selene.core.command.parse.AbstractTypeArgumentParser
import org.dockbox.selene.core.server.Selene
import java.util.*
import kotlin.collections.HashMap

class MapArgumentParser : AbstractTypeArgumentParser<Map<String, String>>() {

    private var rowDelimiter: Char = ','
    private var valueDelimiter: Char = '='

    fun setRowDelimiter(delimiter: Char) {
        this.rowDelimiter = delimiter
    }

    fun setValueDelimiter(delimiter: Char) {
        this.valueDelimiter = delimiter
    }


    override fun parse(commandValue: CommandValue<String>): Optional<Map<String, String>> {
        if (rowDelimiter == valueDelimiter) {
            Selene.log().warn("Row and value delimiter cannot be equal!")
            return Optional.empty()
        }

        val v = commandValue.value
        val map = HashMap<String, String>()
        for (s in v.split(rowDelimiter)) {
            if (s.contains(valueDelimiter)) {
                val parts = s.split(valueDelimiter)
                if (parts.size == 2) {
                    map[parts[0]] = parts[1]
                }
            }
        }

        return Optional.of(map)
    }
}
