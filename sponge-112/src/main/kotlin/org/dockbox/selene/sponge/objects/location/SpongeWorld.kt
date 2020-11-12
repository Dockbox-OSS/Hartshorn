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

import java.util.*
import org.dockbox.selene.core.objects.location.World
import org.dockbox.selene.core.objects.optional.Exceptional
import org.spongepowered.api.Sponge

class SpongeWorld(worldUniqueId: UUID, name: String) : World(worldUniqueId, name) {
    private val reference = ThreadLocal<Exceptional<org.spongepowered.api.world.World?>>()
    private fun refreshReference() {
        if (reference.get().isPresent) reference.set(Exceptional.of(Sponge.getServer().getWorld(worldUniqueId)))
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
}
