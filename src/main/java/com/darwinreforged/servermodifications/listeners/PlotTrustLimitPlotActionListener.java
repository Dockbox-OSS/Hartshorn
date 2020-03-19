package com.darwinreforged.servermodifications.listeners;

import com.darwinreforged.servermodifications.plugins.PlotTrustLimitPlugin;
import com.plotsquared.sponge.events.PlayerPlotHelperEvent;
import com.plotsquared.sponge.events.PlayerPlotTrustedEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

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
    System.out.println("UUID : " + userUUId);

    if (userUUId != null) {
      Optional<User> user = PlotTrustLimitPlugin.getUserStorageService().get(userUUId);

      System.out.println("World: " + event.getPlot().getWorldName());
      // Only check if the user exists, if it doesn't you can't add them anyway
      if (user.isPresent()
          // If the user doesn't have the ptl.unlocked node / Member role
          && !user.get().hasPermission("ptl.unlocked")
          // If the initiator isn't a staff member
          && !event.getInitiator().hasPermission("darwin.staff")
          // And only on Plots2/Plots500/P2 managed worlds
          && (event.getPlot().getWorldName().equals("Plots2")
              || event.getPlot().getWorldName().equals("Plots500")
              || event.getPlot().getWorldName().matches("[-]?[0-9]+,[-]?[0-9]+"))) {

        // Then remove the player and tell the owner they're stupid
        Sponge.getScheduler()
            .createTaskBuilder()
            .execute(
                () -> {
                  event.getPlot().removeTrusted(userUUId);
                  event
                      .getInitiator()
                      .sendMessage(
                          Text.of(
                              TextColors.GRAY,
                              "[] ",
                              TextColors.AQUA,
                              "Automatically removed ",
                              TextColors.DARK_AQUA,
                              user.get().getName(),
                              TextColors.AQUA,
                              " from this plot because their rank is too low."));
                })
            .delayTicks(3)
            .submit(Sponge.getPluginManager().getPlugin("p2trustlimit").get());
      }
    }
  }
}
