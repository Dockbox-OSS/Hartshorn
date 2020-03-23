package com.darwinreforged.servermodifications.modules;

import com.darwinreforged.servermodifications.listeners.PlotTrustLimitPlotActionListener;
import com.darwinreforged.servermodifications.modules.root.ModuleInfo;
import com.darwinreforged.servermodifications.modules.root.PluginModule;
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
        Sponge.getEventManager().registerListeners(this, new PlotTrustLimitPlotActionListener());
    }
}
