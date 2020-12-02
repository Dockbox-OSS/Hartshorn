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

package org.dockbox.selene.core.events.world

import java.util.*
import org.dockbox.selene.core.SeleneUtils
import org.dockbox.selene.core.events.AbstractCancellableEvent
import org.dockbox.selene.core.objects.location.World
import org.dockbox.selene.core.objects.location.WorldProperties
import org.dockbox.selene.core.objects.player.Gamemode
import org.dockbox.selene.core.objects.tuple.Vector3N

/**
 * The abstract type which can be used to listen to all world related events.
 */
abstract class WorldEvent : AbstractCancellableEvent() {

    /**
     * The event fired when a world is loaded.
     *
     * @param world The world which is loaded
     */
    class WorldLoadEvent(world: World) : WorldEvent()

    /**
     * The event fired when a world is unloaded.
     *
     * @param uniqueId The unique identifier of the world
     */
    class WorldUnloadEvent(uniqueId: UUID) : WorldEvent()

    /**
     * The event fired when a world is being saved.
     *
     * @param world The world which is being saved
     */
    class WorldSaveEvent(world: World) : WorldEvent()

    /**
     * The event fired when a new world is being created.
     *
     * @param properties The properties used to create the new world
     */
    class WorldCreatingEvent(properties: WorldCreatingProperties) : WorldEvent()

    /**
     * The available properties used when a world is being created or generated.
     *
     * @property name The name of the world
     * @property uniqueId The unique ID of the world
     *
     * @param loadOnStartup Whether or not the world should load when the server starts
     * @param spawnPosition The position at which players should spawn when they join the world
     * @param seed The seed used when generating the world
     * @param defaultGamemode The default gamemode of the world
     * @param gamerules The gamerules which are applied to the world
     */
    class WorldCreatingProperties(
            val name: String,
            val uniqueId: UUID,
            loadOnStartup: Boolean,
            spawnPosition: Vector3N,
            seed: Long,
            defaultGamemode: Gamemode,
            properties: Map<String, String>
    ) : WorldProperties(loadOnStartup, spawnPosition, seed, defaultGamemode) {

        private val rules: MutableMap<String, String> = SeleneUtils.emptyConcurrentMap();

        override fun setGamerule(key: String, value: String) {
            rules[key] = value
        }

        override fun getGamerules(): MutableMap<String, String> {
            return rules
        }

        init {
            properties.forEach { (k,v) -> setGamerule(k, v) }
        }
    }

}
