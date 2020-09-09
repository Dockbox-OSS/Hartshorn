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
import org.dockbox.darwin.core.objects.location.Location
import org.dockbox.darwin.core.objects.tuple.Vector3D
import org.dockbox.darwin.core.server.Server
import org.dockbox.darwin.core.util.world.WorldStorageService
import java.util.*

class LocationArgumentParser : AbstractTypeArgumentParser<Location>() {
    override fun parse(commandValue: CommandValue<String>): Optional<Location> {
        val xyzw = commandValue.value.split(',')
        if (xyzw.size != 4) return Optional.empty()
        val x = xyzw[0].toIntOrNull() ?: return Optional.empty()
        val y = xyzw[1].toIntOrNull() ?: return Optional.empty()
        val z = xyzw[2].toIntOrNull() ?: return Optional.empty()
        val vector = Vector3D(x, y, z)

        val w = xyzw[3]
        val ws = Server.getInstance(WorldStorageService::class.java)
        val op = ws.getWorld(w)
        val world = if (op.isPresent) op
        else {
            try {
                val uuid = UUID.fromString(w)
                ws.getWorld(uuid)
            } catch (e: IllegalArgumentException) {
                Optional.empty()
            }
        }

        return if (!world.isPresent) Optional.empty()
        else {
            Optional.of(ws.createLocation(vector, world.get()))
        }
    }
}
