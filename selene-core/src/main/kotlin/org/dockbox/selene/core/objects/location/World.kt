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

import java.util.*
import kotlin.collections.HashMap
import org.dockbox.selene.core.objects.tuple.Vector3D
import org.dockbox.selene.core.objects.user.Gamemode
import org.dockbox.selene.core.util.uuid.UUIDUtil

abstract class World(
        open var worldUniqueId: UUID,
        open var name: String,
        loadOnStartup: Boolean,
        spawnPosition: Vector3D,
        seed: Long,
        defaultGamemode: Gamemode,
        gamerules: MutableMap<String, String>
) : WorldProperties(loadOnStartup, spawnPosition, seed, defaultGamemode, gamerules) {

    abstract fun getPlayerCount(): Int
    abstract fun unload(): Boolean
    abstract fun load(): Boolean
    abstract fun isLoaded(): Boolean

    companion object {
        val empty: World = EmptyWorld()
    }

    private class EmptyWorld : World(
            UUIDUtil.empty,
            "EMPTY",
            false,
            Vector3D(0, 0, 0),
            -1,
            Gamemode.SURVIVAL,
            HashMap()
    ) {
        override fun getPlayerCount(): Int = 0
        override fun unload(): Boolean = true
        override fun load(): Boolean = true
        override fun isLoaded(): Boolean = true
    }
}
