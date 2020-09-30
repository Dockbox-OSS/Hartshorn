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
import org.dockbox.selene.core.command.context.CommandValue
import org.junit.Assert
import org.junit.jupiter.api.Test

class WorldEditBlockParserTest {

    @Test
    fun parseHasExpectedSize() {
        val blocks = generateBlockList()
        Assert.assertEquals(3, blocks.size)
    }

    @Test
    fun parseDoesNotSkipValidIds() {
        val blocks = generateBlockList()
        Assert.assertEquals(1, blocks[0]!!.id)
        Assert.assertEquals(2, blocks[1]!!.id)
        Assert.assertEquals(3, blocks[2]!!.id)
    }

    @Test
    fun parseDefaultsToZeroBlockData() {
        val blocks = generateBlockList()
        Assert.assertEquals(0, blocks[0]!!.data)
    }

    @Test
    fun parseKeepsBlockData() {
        val commandvalue: CommandValue<String> = CommandValue.Argument("1:2,2,3", "mock_arg")
        val parser = WorldEditBlockParser()
        val blocks = parser.parse(commandvalue).get()

        Assert.assertEquals(2, blocks[0]!!.data)
    }

    private fun generateBlockList(): List<BaseBlock?> {
        val commandvalue: CommandValue<String> = CommandValue.Argument("1,2,3", "mock_arg")
        val parser = WorldEditBlockParser()
        return parser.parse(commandvalue).get()
    }

}
