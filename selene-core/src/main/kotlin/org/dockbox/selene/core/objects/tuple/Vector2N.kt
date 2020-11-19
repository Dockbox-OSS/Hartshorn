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
 * Represents a 2D point on a x-, and z-axis. All axis points can be represented as any numeral value.
 *
 * @property x The x-axis point
 * @property z The z-axis point
 */
open class Vector2N(var x: Number, var z: Number) {

    fun getXd(): Double = x.toDouble()
    fun getXf(): Float = x.toFloat()
    fun getXi(): Int = x.toInt()
    fun getXl(): Long = x.toLong()

    fun getZd(): Double = z.toDouble()
    fun getZf(): Float = z.toFloat()
    fun getZi(): Int = z.toInt()
    fun getZl(): Long = z.toLong()

}
