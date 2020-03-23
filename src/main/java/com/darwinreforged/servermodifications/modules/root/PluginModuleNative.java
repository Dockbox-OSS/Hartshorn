package com.darwinreforged.servermodifications.modules.root;

import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;

public interface PluginModuleNative {
    void onServerFinishLoad(GameInitializationEvent event);

    void onServerStart(GameStartedServerEvent event);
}
