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

package org.dockbox.selene.sponge.util.player

import org.dockbox.selene.core.objects.user.Player
import org.dockbox.selene.core.impl.util.player.DefaultPlayerStorageService
import org.dockbox.selene.sponge.objects.targets.SpongePlayer
import org.spongepowered.api.Sponge
import org.spongepowered.api.service.user.UserStorageService
import java.util.*
import java.util.stream.Collectors

class SpongePlayerStorageService : DefaultPlayerStorageService() {

    override fun getOnlinePlayers(): List<Player> {
        return Sponge.getServer().onlinePlayers.stream().map { SpongePlayer(it.uniqueId, it.name) }.collect(Collectors.toList())
    }

    override fun getPlayer(name: String): Optional<Player> {
        val osp = Sponge.getServer().getPlayer(name)
        return getPlayer(osp, name)
    }

    override fun getPlayer(uuid: UUID): Optional<Player> {
        val osp = Sponge.getServer().getPlayer(uuid)
        return getPlayer(osp, uuid)
    }

    private fun getPlayer(osp: Optional<org.spongepowered.api.entity.living.player.Player>, obj: Any): Optional<Player> {
        return if (osp.isPresent) {
            osp.map { SpongePlayer(it.uniqueId, it.name) }
        } else {
            var player = Optional.empty<Player>()
            val ouss = Sponge.getServiceManager().provide(UserStorageService::class.java)
            val ou =
                    if (obj is UUID) ouss.flatMap { it[obj] }
                    else ouss.flatMap { it[obj.toString()] }
            if (ou.isPresent) player = ou.map { SpongePlayer(it.uniqueId, it.name) }
            player
        }
    }
}
