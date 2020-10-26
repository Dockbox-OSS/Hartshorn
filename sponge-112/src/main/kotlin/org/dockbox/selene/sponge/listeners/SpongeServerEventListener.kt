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

package org.dockbox.selene.sponge.listeners

import org.dockbox.selene.core.events.server.ServerEvent
import org.dockbox.selene.core.events.server.ServerEvent.Started
import org.dockbox.selene.core.events.server.ServerEvent.Starting
import org.dockbox.selene.core.server.Selene
import org.dockbox.selene.core.util.events.EventBus
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameStartedServerEvent
import org.spongepowered.api.event.game.state.GameStartingServerEvent

class SpongeServerEventListener {

    private val bus: EventBus = Selene.getInstance(EventBus::class.java)

    @Listener
    fun onServerStarting(event: GameStartingServerEvent?) = bus.post(Starting())

    @Listener
    fun onServerStarted(event: GameStartedServerEvent?) = bus.post(Started())

    @Listener
    fun onServerReload(event: GameReloadEvent?) = bus.post(ServerEvent.Reload())

}
