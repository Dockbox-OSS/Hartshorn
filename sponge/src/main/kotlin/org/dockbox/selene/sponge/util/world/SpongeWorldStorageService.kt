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

package org.dockbox.selene.sponge.util.world

import org.dockbox.selene.core.objects.location.Location
import org.dockbox.selene.core.objects.location.World
import org.dockbox.selene.core.objects.tuple.Vector3D
import org.dockbox.selene.core.util.world.WorldStorageService
import org.dockbox.selene.sponge.objects.location.SpongeLocation
import org.dockbox.selene.sponge.util.SpongeConversionUtil
import org.spongepowered.api.Sponge
import java.util.*

class SpongeWorldStorageService : WorldStorageService() {
    override fun getLoadedWorlds(): List<World> {
        return Sponge.getServer().worlds.map { SpongeConversionUtil.fromSponge(it) }
    }

    override fun getAllWorldUUIDs(): List<UUID> {
        return Sponge.getServer().allWorldProperties.map { it.uniqueId }
    }

    override fun getWorld(name: String): Optional<World> {
        return Sponge.getServer().loadWorld(name).map { SpongeConversionUtil.fromSponge(it) }
    }

    override fun getWorld(uuid: UUID): Optional<World> {
        return Sponge.getServer().loadWorld(uuid).map { SpongeConversionUtil.fromSponge(it) }
    }

    override fun createLocation(vector: Vector3D, world: World): Location {
        return SpongeLocation(vector, world)
    }
}
