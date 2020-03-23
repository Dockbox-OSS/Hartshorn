package com.darwinreforged.servermodifications.modules;

import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;

public interface PluginModule {
    void onServerFinishLoad(GameInitializationEvent event);

    void onServerStart(GameStartedServerEvent event);
}
