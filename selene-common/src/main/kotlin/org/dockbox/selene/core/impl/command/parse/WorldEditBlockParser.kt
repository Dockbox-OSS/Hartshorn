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

import com.sk89q.worldedit.blocks.BaseBlock
import java.util.function.Function
import org.dockbox.selene.core.impl.command.convert.parser.TypeArgumentParsers

/**
 * Parses a list of block ID's, separated by ',' into a list of [BaseBlock] instances. If the block ID is in the format
 * 'id:data' it will use the data from the block ID, otherwise it defaults to zero (0).
 *
 * Delimiter is always ','
 *
 * Does not support named ID's like 'minecraft:stone'. Does not support patterns or masks.
 *
 * @constructor Create empty World edit block parser
 */
class WorldEditBlockParser : TypeArgumentParsers.ListParser<BaseBlock?>(Function {
    val idData = it.replace(" ", "").split(":")
    if (idData.isNotEmpty()) {
        var data = 0
        if (idData.size >= 2) data = Integer.parseInt(idData[1])

        return@Function BaseBlock(Integer.parseInt(idData[0]), data)
    }
    return@Function null
})
