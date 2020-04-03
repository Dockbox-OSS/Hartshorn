package com.darwinreforged.server.modules.plottrustlimit;

import com.darwinreforged.server.api.DarwinServer;
import com.darwinreforged.server.api.modules.ModuleInfo;
import com.darwinreforged.server.api.modules.PluginModule;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.service.user.UserStorageService;

@ModuleInfo(
        id = "p2trustlimit",
        name = "PlotSquared Trust Limit",
        version = "0.1.10b",
        description =
                "Locks trusting on Plots2 and Plots500 to people with the ptl.unlocked permission node")
public class PlotTrustLimitModule extends PluginModule {

    private UserStorageService userStorageService;

    public UserStorageService getUserStorageService() {
        return userStorageService;
    }

    public PlotTrustLimitModule() {
    }

    @Listener
    public void onServerFinishLoad(GameInitializationEvent event) {
        userStorageService = Sponge.getServiceManager().provide(UserStorageService.class).get();
        DarwinServer.registerListener(new PlotTrustLimitPlotActionListener());
    }
}
