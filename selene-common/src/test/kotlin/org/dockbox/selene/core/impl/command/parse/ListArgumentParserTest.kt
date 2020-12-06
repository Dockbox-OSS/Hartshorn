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
import org.dockbox.selene.core.impl.command.convert.TypeArgumentParsers
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class ListArgumentParserTest {

    // TODO GuusLieben, rewrite in Java

    @Test
    fun parseSizeEquals() {
        val list = generateList()
        Assert.assertEquals(3, list.size)
    }

    @Test
    fun parseArgumentsMatch() {
        val list = generateList()
        Assert.assertEquals(1, list[0])
        Assert.assertEquals(2, list[1])
        Assert.assertEquals(3, list[2])
    }

    @Test
    fun parseDelimiterWorks() {
        val commandvalue: CommandValue<String> = CommandValue.Argument("1:2:3", "mock_arg")
        val parser: TypeArgumentParsers.ListParser<Int> = TypeArgumentParsers.ListParser(Integer::parseInt)
        parser.setDelimiter(':')

        val list = parser.parse(commandvalue).get()
        Assert.assertEquals(3, list.size)
    }

    private fun generateList(): List<Int> {
        val commandvalue: CommandValue<String> = CommandValue.Argument("1,2,3", "mock_arg")
        val parser: TypeArgumentParsers.ListParser<Int> = TypeArgumentParsers.ListParser(Integer::parseInt)

        return parser.parse(commandvalue).get()
    }
}
