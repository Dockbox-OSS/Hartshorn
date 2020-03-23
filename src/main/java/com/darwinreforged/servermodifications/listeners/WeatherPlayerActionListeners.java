package com.darwinreforged.servermodifications.listeners;

import com.darwinreforged.servermodifications.DarwinServer;
import com.darwinreforged.servermodifications.util.plugins.PlayerWeatherCoreUtil;
import com.intellectualcrafters.plot.flag.Flags;
import com.intellectualcrafters.plot.object.Plot;
import com.plotsquared.sponge.events.PlayerEnterPlotEvent;
import com.plotsquared.sponge.events.PlayerLeavePlotEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent.Join;
import org.spongepowered.api.scheduler.Task;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class WeatherPlayerActionListeners {

  @Listener(order = Order.LATE)
  public void onPlotEnter(PlayerEnterPlotEvent event) {
    if (PlayerWeatherCoreUtil.globalWeatherOff) return;

    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();
    if (PlayerWeatherCoreUtil.toggledPlayersContains(uuid)) return;

    Plot plot = event.getPlot();

    int weatherType = plot.getFlag(Flags.WEATHER, 0);
    // Plot has no weather
    if (weatherType <= 0) return;

    PlayerWeatherCoreUtil.Weather plotWeather = PlayerWeatherCoreUtil.Weather.of(weatherType);
    PlayerWeatherCoreUtil.Weather playersWeather = PlayerWeatherCoreUtil.getPlayersWeather(uuid);

    if (plot.getWorldName().replaceAll(",", ";").equals(plot.getId().toString())) {
      Task.builder()
              .delay(500, TimeUnit.MILLISECONDS)
              .execute(() -> determinePacketToSend(uuid, playersWeather, plotWeather))
              .name("WeatherTask")
              .submit(DarwinServer.getServer());
    }
    else
    {
      determinePacketToSend(uuid, playersWeather, plotWeather);
    }
  }

  @Listener
  public void onPlotExit(PlayerLeavePlotEvent event) {
    if (PlayerWeatherCoreUtil.globalWeatherOff) return;

    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();
    Plot plot = event.getPlot();

    int weatherType = plot.getFlag(Flags.WEATHER, 0);
    // Plot has no weather
    if (weatherType <= 0) return;

    PlayerWeatherCoreUtil.Weather plotWeather = PlayerWeatherCoreUtil.Weather.of(weatherType);
    PlayerWeatherCoreUtil.Weather playersWeather = PlayerWeatherCoreUtil.getPlayersWeather(uuid);

    determinePacketToSend(uuid, plotWeather, playersWeather);
  }

  @Listener
  public void onPlayerJoin(Join event, @First Player player) {
    if (PlayerWeatherCoreUtil.globalWeatherOff || !PlayerWeatherCoreUtil.playerWeatherContains(player.getUniqueId())) return;
    UUID uuid = player.getUniqueId();

    switch (PlayerWeatherCoreUtil.getPlayersWeather(uuid)) {
      case RAINING:
        PlayerWeatherCoreUtil.sendPlayerWeatherPacket(uuid,PlayerWeatherCoreUtil.Weather.RAINING);
        break;
      case LIGHTNING:
        PlayerWeatherCoreUtil.addLightningPlayer(uuid);
        break;
      case LIGHTNINGSTORM:
        PlayerWeatherCoreUtil.sendPlayerWeatherPacket(uuid,PlayerWeatherCoreUtil.Weather.RAINING);
        PlayerWeatherCoreUtil.addLightningPlayer(uuid);
        break;
    }
  }

  @Listener
  public void onPlayerLeave(ClientConnectionEvent.Disconnect event, @First Player player) {
    PlayerWeatherCoreUtil.removeLightningPlayer(player.getUniqueId());
  }

  private void determinePacketToSend(UUID uuid, PlayerWeatherCoreUtil.Weather from, PlayerWeatherCoreUtil.Weather  to) {
    // No need to send any weather packets
    if (from == to) return;

    switch (to) {
      case RESET:
        PlayerWeatherCoreUtil.sendPlayerWeatherPacket(uuid,PlayerWeatherCoreUtil.Weather.RESET);
        PlayerWeatherCoreUtil.removeLightningPlayer(uuid);
        break;

      case RAINING:
        if (from !=PlayerWeatherCoreUtil.Weather.LIGHTNINGSTORM) {
          PlayerWeatherCoreUtil.sendPlayerWeatherPacket(uuid,PlayerWeatherCoreUtil.Weather.RAINING);
        }
        PlayerWeatherCoreUtil.removeLightningPlayer(uuid);

        break;

      case LIGHTNING:
        // You only need to send a clear packet if its raining
        if (from !=PlayerWeatherCoreUtil.Weather.RESET) {
          PlayerWeatherCoreUtil.sendPlayerWeatherPacket(uuid,PlayerWeatherCoreUtil.Weather.RESET);
        }
        PlayerWeatherCoreUtil.addLightningPlayer(uuid);
        break;

      case LIGHTNINGSTORM:
        // You only need to send a raining packet if its clear
        if (from !=PlayerWeatherCoreUtil.Weather.RAINING) {
          PlayerWeatherCoreUtil.sendPlayerWeatherPacket(uuid,PlayerWeatherCoreUtil.Weather.RAINING);
        }
        PlayerWeatherCoreUtil.addLightningPlayer(uuid);
        break;
    }
  }
}
