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

package org.dockbox.selene.sponge.objects.location

import com.flowpowered.math.vector.Vector3i
import java.util.*
import org.dockbox.selene.core.objects.location.World
import org.dockbox.selene.core.objects.tuple.Vector3D
import org.dockbox.selene.core.objects.user.Gamemode
import org.dockbox.selene.sponge.util.SpongeConversionUtil
import org.spongepowered.api.Sponge

class SpongeWorld(
        worldUniqueId: UUID,
        name: String,
        loadOnStartup: Boolean,
        spawnPosition: Vector3D,
        seed: Long,
        defaultGamemode: Gamemode,
        gamerules: MutableMap<String, String>
) : World(worldUniqueId, name, loadOnStartup, spawnPosition, seed, defaultGamemode, gamerules) {

    private val reference = ThreadLocal<Optional<org.spongepowered.api.world.World?>>()
    private fun refreshReference() {
        if (reference.get().isPresent) reference.set(Sponge.getServer().getWorld(worldUniqueId))
    }

    fun getReference(): org.spongepowered.api.world.World? {
        return reference.get().orElse(null)
    }

    private fun referenceExists(): Boolean {
        refreshReference()
        return reference.get().isPresent
    }

    override fun getPlayerCount(): Int {
        return if (referenceExists()) getReference()!!.players.size else 0
    }

    override fun unload(): Boolean {
        return if (referenceExists()) {
            Sponge.getServer().unloadWorld(this.getReference()!!)
        } else false
    }

    override fun load(): Boolean {
        return if (referenceExists()) {
            Sponge.getServer().loadWorld(worldUniqueId).isPresent
        } else false
    }

    override fun isLoaded(): Boolean {
        return if (referenceExists()) {
            getReference()!!.isLoaded
        } else false
    }

    override var loadOnStartup: Boolean = loadOnStartup
        get() {
            return if (referenceExists()) getReference()!!.properties.loadOnStartup() else field
        }
        set(value) {
            if (referenceExists()) getReference()!!.properties.setLoadOnStartup(value)
            field = value
        }

    override var spawnPosition: Vector3D = spawnPosition
        get() {
            return if (referenceExists()) {
                val vector3i = getReference()!!.properties.spawnPosition
                Vector3D(vector3i.x, vector3i.y, vector3i.z)
            } else field
        }
        set(value) {
            if (referenceExists()) getReference()!!.properties.spawnPosition = Vector3i(value.x.toInt(), value.y.toInt(), value.z.toInt())
            field = value
        }

    override var seed: Long = seed
        get() {
            return if (referenceExists()) getReference()!!.properties.seed else field
        }
        set(value) {
            if (referenceExists()) getReference()!!.properties.seed = value
            field = value
        }

    override var defaultGamemode: Gamemode = defaultGamemode
        get() {
            return if (referenceExists()) SpongeConversionUtil.fromSponge(getReference()!!.properties.gameMode) else field
        }
        set(value) {
            if (referenceExists()) getReference()!!.properties.gameMode = SpongeConversionUtil.toSponge(value)
            field = value
        }
}
