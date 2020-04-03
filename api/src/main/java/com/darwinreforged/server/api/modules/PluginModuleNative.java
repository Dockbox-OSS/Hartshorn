package com.darwinreforged.server.api.modules;

import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;

public interface PluginModuleNative {
    void onServerFinishLoad(GameInitializationEvent event);

    void onServerStart(GameStartedServerEvent event);


    class External
            extends PluginModule {
    }

    class Internal
            extends PluginModule {
    }
}
