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

package org.dockbox.selene.core.command.parse.impl

import org.dockbox.selene.core.command.context.CommandValue
import org.dockbox.selene.core.command.parse.AbstractTypeArgumentParser
import org.dockbox.selene.core.objects.location.World
import org.dockbox.selene.core.server.Selene
import org.dockbox.selene.core.util.world.WorldStorageService
import java.util.*

class WorldArgumentParser : AbstractTypeArgumentParser<World>() {
    override fun parse(commandValue: CommandValue<String>): Optional<World> {
        val v = commandValue.value
        val ws = Selene.getInstance(WorldStorageService::class.java)
        val ow = ws.getWorld(v)
        return if (ow.isPresent) ow
        else {
            try {
                val uuid = UUID.fromString(v)
                ws.getWorld(uuid)
            } catch (e: IllegalArgumentException) {
                Optional.empty()
            }
        }
    }
}
