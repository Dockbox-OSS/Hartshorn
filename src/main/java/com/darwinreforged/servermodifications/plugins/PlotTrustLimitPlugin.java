package com.darwinreforged.servermodifications.plugins;

import com.darwinreforged.servermodifications.listeners.PlotTrustLimitPlotActionListener;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.user.UserStorageService;

@Plugin(
        id = "p2trustlimit",
        name = "PlotSquared Trust Limit",
        version = "0.1.10b",
        description =
                "Locks trusting on Plots2 and Plots500 to people with the ptl.unlocked permission node")
public class PlotTrustLimitPlugin {

    static UserStorageService userStorageService;

    public static UserStorageService getUserStorageService() {
        return userStorageService;
    }

    public PlotTrustLimitPlugin() {
    }

    @Listener
    public void onServerFinishLoad(GameInitializationEvent event) {
        userStorageService = Sponge.getServiceManager().provide(UserStorageService.class).get();
        Sponge.getEventManager().registerListeners(this, new PlotTrustLimitPlotActionListener());
    }
}
