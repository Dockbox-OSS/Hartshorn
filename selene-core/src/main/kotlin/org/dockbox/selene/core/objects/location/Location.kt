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

package org.dockbox.selene.core.objects.location

import org.dockbox.selene.core.objects.tuple.Vector3N

class Location(var vectorLoc: Vector3N, var world: World) {

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
        val EMPTY: Location = Location(Vector3N(0, 0, 0), World.empty)
    }
}
