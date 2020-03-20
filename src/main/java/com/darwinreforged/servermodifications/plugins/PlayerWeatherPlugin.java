package com.darwinreforged.servermodifications.plugins;

import com.darwinreforged.servermodifications.commands.weather.PlayerWeatherCommand;
import com.darwinreforged.servermodifications.commands.weather.PlotWeatherCommand;
import com.darwinreforged.servermodifications.listeners.WeatherPlayerActionListeners;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.plugins.PlayerWeatherLightningUtil;
import com.darwinreforged.servermodifications.util.plugins.PlayerWeatherCoreUtil;
import com.google.inject.Inject;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.UUID;

@Plugin(
    id = "weatherplugin",
    name = "Weatherplugin",
    description = "A plugin which sets personal and plot weather",
    dependencies = {@Dependency(id = "packetgate")},version = "1.1")
public class PlayerWeatherPlugin {

  @Inject private Logger logger;
  public static PlayerWeatherPlugin instance;

  private static PlayerWeatherPlugin plugin;

    public PlayerWeatherPlugin() {
    }

    public static PlayerWeatherPlugin getPlugin(){
      return plugin;
  }

  @Listener
  public void onServerStart(GameStartedServerEvent event) {
      PlayerWeatherPlugin.instance = this;
    Optional<PacketGate> packetGateOptional = Sponge.getServiceManager().provide(PacketGate.class);
    if (packetGateOptional.isPresent()) {
      logger.info("packetGate is present");

      initialiseCommand();
      Sponge.getEventManager().registerListeners(this, new WeatherPlayerActionListeners());

      logger.info("Weather Plugin successfull initialised");
    } else {
      logger.error(
          "PacketGate is not installed. This is required for Weather Plugin to function correctly");
    }

    plugin = this;
  }

  private void initialiseCommand() {
    CommandSpec plotWeatherCommand =
        CommandSpec.builder()
            .description(Text.of("Set the weather of your plot"))
            .permission("weatherplugin.command.plot")
            .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("weather"))))
            .executor(new PlotWeatherCommand())
            .build();

    CommandSpec playerWeatherCommand =
        CommandSpec.builder()
            .description(Text.of("Set your personal weather"))
            .permission("weatherplugin.command.set")
            .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("weather"))))
            .executor(new PlayerWeatherCommand())
            .build();

    CommandSpec toggleGlobalWeatherCommand =
        CommandSpec.builder()
            .description(Text.of("Toggles the weather of all players personal weather"))
            .permission("weatherplugin.command.globalweather")
            .arguments(GenericArguments.none())
            .executor(
                (src, args) -> {
                  if (!(src instanceof Player)) {
                    src.sendMessage(Text.of("This command can only be executed by a player"));
                    return CommandResult.success();
                  }

                  // If true, this prevents any packets from being intercepted.
                  Player player = (Player) src;
                  PlayerWeatherCoreUtil.globalWeatherOff = !PlayerWeatherCoreUtil.globalWeatherOff;
                  PlayerUtils.sendMessage(player, "Using global weather : " + (PlayerWeatherCoreUtil.globalWeatherOff ? "OFF" : "ON"));


                  for (UUID uuid : PlayerWeatherCoreUtil.getPlayerWeatherUUIDs())
                  {
                      if (PlayerWeatherCoreUtil.globalWeatherOff)
                      {
                          // Sends packet to all online players, setting their weather to server's weather
                          PlayerWeatherCoreUtil.sendPlayerWeatherPacket(uuid, PlayerWeatherCoreUtil.Weather.RESET);
                          PlayerWeatherLightningUtil.stopLightningScheduler();
                      }
                      else {
                          PlayerWeatherLightningUtil.startlightningScheduler();
                          //Only the Lightning weather type isn't raining
                          if (PlayerWeatherCoreUtil.getPlayersWeather(uuid) != PlayerWeatherCoreUtil.Weather.LIGHTNING)
                          {
                              PlayerWeatherCoreUtil.sendPlayerWeatherPacket(uuid, PlayerWeatherCoreUtil.Weather.RAINING);
                          }

                      }
                  }
                  return CommandResult.success();
                })
            .build();

    CommandSpec debugCommand =
            CommandSpec.builder()
                    .description(Text.of("Prints out relevant information about player"))
                    .permission("weatherplugin.command.debug")
                    .child(toggleGlobalWeatherCommand, "toggle")
                    .executor(
                        (src, args) -> {
                            if (!(src instanceof Player)) return CommandResult.success();
                            Player player = (Player) src;
                            PlayerUtils.sendMessage(player, "Current Weather Type: " +
                                    PlayerWeatherCoreUtil.getPlayersWeather(player.getUniqueId()));
                            PlayerUtils.sendMessage(player, "Is Lightning Player: " +
                                    PlayerWeatherCoreUtil.lightningPlayersContains(player.getUniqueId()));
                            PlayerUtils.sendMessage(player, PlayerWeatherLightningUtil.lightningSchedulerStatus());
                            return CommandResult.success();
                        }
                    ).build();

    Sponge.getCommandManager().register(this, debugCommand, "weatherdebug", "dweather");
    Sponge.getCommandManager().register(this, playerWeatherCommand, "weatherplugin", "pweather");
    Sponge.getCommandManager().register(this, plotWeatherCommand, "plotweather");
  }
}
