package org.dockbox.darwin.core.events.player

import org.dockbox.darwin.core.events.AbstractTargetCancellableEvent
import org.dockbox.darwin.core.objects.location.Location
import org.dockbox.darwin.core.objects.user.Player

abstract class PlayerMoveEvent(target: Player) : AbstractTargetCancellableEvent(target) {

    class Teleport(target: Player, val oldLocation: Location, val newLocation: Location) : PlayerMoveEvent(target)
    class Walk(target: Player) : PlayerMoveEvent(target)
    class Fly(target: Player) : PlayerMoveEvent(target)
    class Crouch(target: Player) : PlayerMoveEvent(target)
    class Spawn(target: Player, val spawnLocation: Location) : PlayerMoveEvent(target)

}
