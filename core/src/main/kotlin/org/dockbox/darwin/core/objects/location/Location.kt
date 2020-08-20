package org.dockbox.darwin.core.objects.location

import org.dockbox.darwin.core.objects.tuple.Vector3D

abstract class Location(open var vectorLoc: Vector3D, open var world: World) {

    fun getX(): Number {
        return vectorLoc.x
    }

    fun getY(): Number {
        return vectorLoc.y
    }

    fun getZ(): Number {
        return vectorLoc.z
    }

    fun setX(x: Number) {
        vectorLoc.x = x
    }

    fun setY(y: Number) {
        vectorLoc.y = y
    }

    fun setZ(z: Number) {
        vectorLoc.z = z
    }

    companion object {
        val EMPTY: Location = EmptyLocation()
    }

    private class EmptyLocation : Location(Vector3D(0, 0, 0), World.empty)
}
