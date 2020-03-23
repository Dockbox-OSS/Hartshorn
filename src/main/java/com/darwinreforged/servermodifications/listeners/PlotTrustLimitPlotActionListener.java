package com.darwinreforged.servermodifications.listeners;

import com.darwinreforged.servermodifications.DarwinServer;
import com.darwinreforged.servermodifications.modules.PlotTrustLimitModule;
import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.plotsquared.sponge.events.PlayerPlotHelperEvent;
import com.plotsquared.sponge.events.PlayerPlotTrustedEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;

import java.util.Optional;
import java.util.UUID;

public class PlotTrustLimitPlotActionListener {

  @Listener
  public void onPlotAdded(PlayerPlotHelperEvent event) {
    if (event.wasAdded())
      onPlotTrusted(
          new PlayerPlotTrustedEvent(
              event.getInitiator(), event.getPlot(), event.getPlayer(), event.wasAdded()));
  }

  @Listener
  public void onPlotTrusted(PlayerPlotTrustedEvent event) {
    UUID userUUId = event.getPlayer();

    if (userUUId != null) {
      Optional<PlotTrustLimitModule> moduleOptional = DarwinServer.getModule(PlotTrustLimitModule.class);
      if (moduleOptional.isPresent()) {
        Optional<User> user = moduleOptional.get().getUserStorageService().get(userUUId);

        // Only check if the user exists, if it doesn't you can't add them anyway
        if (user.isPresent()
                // If the user doesn't have the ptl.unlocked node / Member role
                && !user.get().hasPermission("ptl.unlocked")
                // If the initiator isn't a staff member
                && !event.getInitiator().hasPermission("darwin.staff")
                // And only on Plots2/Plots500/P2 managed worlds
                && (event.getPlot().getWorldName().equals(Translations.PLOTS2_NAME.s())
                || event.getPlot().getWorldName().equals(Translations.PLOTS500_NAME.s())
                || event.getPlot().getWorldName().matches("[-]?[0-9]+,[-]?[0-9]+"))) {

          // Then remove the player and tell the owner they're stupid
          Sponge.getScheduler()
                  .createTaskBuilder()
                  .execute(
                          () -> {
                            event.getPlot().removeTrusted(userUUId);
                            PlayerUtils.tell(event.getInitiator(), Translations.TRUST_LIMIT_AUTO_CLEANED.ft(user.get().getName()));
                          })
                  .delayTicks(3)
                  .submit(Sponge.getPluginManager().getPlugin("p2trustlimit").get());
        }
      }
    }
  }
}
