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

package org.dockbox.selene.core.objects.tuple


/**
 * Represents a 3D point on a x-, y-, and z-axis. All axis points can be represented as any numeral value.
 *
 * @property y The y-axis point
 * @constructor
 *
 * @param x The x-axis point
 * @param z The z-axis point
 */
open class Vector3N(x: Number, var y: Number, z: Number): Vector2N(x, z) {

    fun getYd(): Double = y.toDouble()
    fun getYf(): Float = y.toFloat()
    fun getYi(): Int = y.toInt()
    fun getYl(): Long = y.toLong()

}
