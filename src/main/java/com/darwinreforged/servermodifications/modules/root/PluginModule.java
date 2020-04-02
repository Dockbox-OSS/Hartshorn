package com.darwinreforged.servermodifications.modules.root;

import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;

public abstract class PluginModule
        implements PluginModuleNative {
    @Override
    public void onServerFinishLoad(GameInitializationEvent event) {
    }

    @Override
    public void onServerStart(GameStartedServerEvent event) {
    }
}

