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
import org.dockbox.selene.core.command.parse.rules.Rule
import java.util.*

class ListArgumentParser : AbstractTypeArgumentParser<List<String>>() {

    private var delimiter: Char = ','
    private var minMax: Rule.MinMax? = null

    fun setDelimiter(delimiter: Char) {
        this.delimiter = delimiter
    }

    fun setMinMax(minMax: Rule.MinMax) {
        this.minMax = minMax
    }

    override fun parse(commandValue: CommandValue<String>): Optional<List<String>> {
        val v = commandValue.value
        var list = v.split(this.delimiter)

        if (this.minMax != null) {
            val min = if (this.minMax!!.min >= 0) this.minMax!!.min else 0
            val max = if (this.minMax!!.max <= list.size) this.minMax!!.max else (list.size)

            list = list.subList(min, max)
        }
        return Optional.of(list)
    }
}
