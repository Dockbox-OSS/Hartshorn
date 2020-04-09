package com.darwinreforged.server.core.modules;

import com.darwinreforged.server.core.events.ServerInitEvent;
import com.darwinreforged.server.core.events.ServerStartedEvent;

public abstract class PluginModule
        implements PluginModuleNative {
    @Override
    public void onServerFinishLoad(ServerInitEvent event) {

    }

    @Override
    public void onServerStart(ServerStartedEvent event) {

    }
}

