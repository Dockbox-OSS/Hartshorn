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

import com.boydti.fawe.`object`.FawePlayer
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.extension.input.ParserContext
import com.sk89q.worldedit.function.pattern.Pattern
import org.dockbox.selene.core.command.context.CommandValue
import org.dockbox.selene.core.command.parse.AbstractTypeArgumentParser
import org.dockbox.selene.core.objects.optional.Exceptional

class WorldEditPatternParser(val player: FawePlayer<*>) : AbstractTypeArgumentParser<Pattern>() {
    override fun parse(commandValue: CommandValue<String>): Exceptional<Pattern> {
        val v = commandValue.value
        val pctx = ParserContext()
        pctx.actor = player.player
        pctx.world = player.world
        pctx.session = player.session
        return Exceptional.ofNullable(WorldEdit.getInstance().patternFactory.parseFromInput(v, pctx))
    }
}
