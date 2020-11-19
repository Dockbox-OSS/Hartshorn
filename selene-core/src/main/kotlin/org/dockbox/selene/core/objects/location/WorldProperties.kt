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

package org.dockbox.selene.core.objects.location

import org.dockbox.selene.core.objects.tuple.Vector3N
import org.dockbox.selene.core.objects.user.Gamemode

/**
 * Represents the properties or metadata of a world.
 *
 * @property loadOnStartup Whether or not the world should load when the server is starting
 * @property spawnPosition The position at which players should spawn when first joining the world, or when they die
 * @property seed The seed used when generating the world
 * @property defaultGamemode The default [Gamemode] of the world
 * @property gamerules The gamerules applied to the world, typically this is a snapshot of the gamerules of a world
 */
abstract class WorldProperties(
        open var loadOnStartup: Boolean,
        open var spawnPosition: Vector3N,
        open var seed: Long,
        open var defaultGamemode: Gamemode,
        val gamerules: MutableMap<String, String>) {

    fun setGamerule(key: String, value: String) {
        gamerules[key] = value
    }

}
