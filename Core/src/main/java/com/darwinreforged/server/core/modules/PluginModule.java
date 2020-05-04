package com.darwinreforged.server.core.modules;

import com.darwinreforged.server.core.events.internal.ServerInitEvent;
import com.darwinreforged.server.core.events.internal.ServerReloadEvent;
import com.darwinreforged.server.core.events.internal.ServerStartedEvent;

public abstract class PluginModule
        implements PluginModuleNative {
    // Doesn't force modules to have these events implemented by default
    @Override
    public void onServerFinishLoad(ServerInitEvent event) {
    }

    @Override
    public void onServerStart(ServerStartedEvent event) {
    }

    @Override
    public void onServerReload(ServerReloadEvent event) {
    }
}

