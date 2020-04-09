package com.darwinreforged.server.core.modules;

import com.darwinreforged.server.core.events.ServerInitEvent;
import com.darwinreforged.server.core.events.ServerStartedEvent;

public interface PluginModuleNative {
    void onServerFinishLoad(ServerInitEvent event);

    void onServerStart(ServerStartedEvent event);


    class External
            extends PluginModule {
    }

    class Internal
            extends PluginModule {
    }
}
