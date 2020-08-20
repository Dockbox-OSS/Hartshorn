package org.dockbox.darwin.sponge.listeners

import org.dockbox.darwin.core.events.server.ServerEvent
import org.dockbox.darwin.core.events.server.ServerEvent.Started
import org.dockbox.darwin.core.events.server.ServerEvent.Starting
import org.dockbox.darwin.core.util.events.EventBus
import org.dockbox.darwin.sponge.SpongeServer
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameStartedServerEvent
import org.spongepowered.api.event.game.state.GameStartingServerEvent

class SpongeEventListener {

    private val bus: EventBus = SpongeServer.getInstance(EventBus::class.java)

    @Listener
    fun onServerStarting(event: GameStartingServerEvent?) = bus.post(Starting())

    @Listener
    fun onServerStarted(event: GameStartedServerEvent?) = bus.post(Started())

    @Listener
    fun onServerReload(event: GameReloadEvent?) = bus.post(ServerEvent.Reload())

}
