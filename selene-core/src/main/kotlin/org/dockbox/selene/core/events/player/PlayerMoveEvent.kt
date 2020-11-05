/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.events.player

import org.dockbox.selene.core.events.AbstractTargetCancellableEvent
import org.dockbox.selene.core.objects.location.Location
import org.dockbox.selene.core.objects.location.World
import org.dockbox.selene.core.objects.user.Player

abstract class PlayerMoveEvent(target: Player) : AbstractTargetCancellableEvent(target) {

    class PlayerTeleportEvent(target: Player, val oldLocation: Location, val newLocation: Location) : PlayerMoveEvent(target)
    class PlayerSpawnEvent(target: Player, val spawnLocation: Location) : PlayerMoveEvent(target)
    class PlayerWarpEvent(target: Player, val warp: org.dockbox.selene.core.objects.location.Warp) : PlayerMoveEvent(target)
    class PlayerSwitchWorldEvent(target: Player, val oldWorld: World, val newWorld: World) : PlayerMoveEvent(target)

}
