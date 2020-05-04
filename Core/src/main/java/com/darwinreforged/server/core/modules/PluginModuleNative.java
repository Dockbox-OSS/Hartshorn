package com.darwinreforged.server.core.modules;

import com.darwinreforged.server.core.events.internal.ServerInitEvent;
import com.darwinreforged.server.core.events.internal.ServerReloadEvent;
import com.darwinreforged.server.core.events.internal.ServerStartedEvent;

public interface PluginModuleNative {
    void onServerFinishLoad(ServerInitEvent event);

    void onServerStart(ServerStartedEvent event);

    void onServerReload(ServerReloadEvent event);
}
