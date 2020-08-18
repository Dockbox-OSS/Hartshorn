package org.dockbox.darwin.core.objects.location

import org.dockbox.darwin.core.objects.tuple.Vector3D
import org.dockbox.darwin.core.util.uuid.UUIDUtil

abstract class Location(open var vectorLoc: Vector3D, open var world: World) {
    companion object {
        val EMPTY: Location = EmptyLocation()
    }

    private class EmptyLocation : Location(Vector3D(0, 0, 0), World.empty)
}