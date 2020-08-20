package org.dockbox.darwin.sponge.listeners;

import org.dockbox.darwin.core.events.server.ServerEvent;
import org.dockbox.darwin.core.util.events.EventBus;
import org.dockbox.darwin.sponge.SpongeServer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;

public class SpongeEventListener {

    private final EventBus bus;

    public SpongeEventListener() {
        bus = SpongeServer.getInstance(EventBus.class);
    }

    @Listener
    public void onServerStarting(GameStartingServerEvent event) {
        bus.post(new ServerEvent.Starting());
    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        bus.post(new ServerEvent.Started());
    }

    @Listener
    public void onServerReload(GameReloadEvent event) {
        bus.post(new ServerEvent.Reload());
    }

}
