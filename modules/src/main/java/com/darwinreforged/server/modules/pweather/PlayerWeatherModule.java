package com.darwinreforged.server.modules.pweather;

import com.darwinreforged.server.api.DarwinServer;
import com.darwinreforged.server.api.modules.ModuleInfo;
import com.darwinreforged.server.api.modules.PluginModule;
import com.darwinreforged.server.api.resources.Permissions;
import com.darwinreforged.server.api.resources.Translations;
import com.darwinreforged.server.api.utils.PlayerUtils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.UUID;

import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;

@ModuleInfo(
        id = "weatherplugin",
        name = "Weatherplugin",
        description = "A plugin which sets personal and plot weather",
        dependencies = {@Dependency(id = "packetgate")}, version = "1.1")
public class PlayerWeatherModule extends PluginModule {

    public PlayerWeatherModule() {
    }

    @Override
    public void onServerFinishLoad(GameInitializationEvent event) {
        Optional<PacketGate> packetGateOptional = Sponge.getServiceManager().provide(PacketGate.class);
        if (packetGateOptional.isPresent()) {
            DarwinServer.getLogger().info("packetGate is present");

            initialiseCommand();
            DarwinServer.registerListener(new WeatherPlayerActionListeners());

            DarwinServer.getLogger().info("Weather Plugin successfull initialised");
        } else {
            DarwinServer.getLogger().error(
                    "PacketGate is not installed. This is required for Weather Plugin to function correctly");
        }
    }

  private void initialiseCommand() {
    CommandSpec plotWeatherCommand =
            CommandSpec.builder()
                    .description(Text.of("Set the weather of your plot"))
                    .permission(Permissions.WEATHER_PLOT.p())
            .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("weather"))))
            .executor(new PlotWeatherCommand())
            .build();

    CommandSpec playerWeatherCommand =
            CommandSpec.builder()
                    .description(Text.of("Set your personal weather"))
                    .permission(Permissions.WEATHER_SET.p())
            .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("weather"))))
            .executor(new PlayerWeatherCommand())
            .build();

    CommandSpec toggleGlobalWeatherCommand =
            CommandSpec.builder()
                    .description(Text.of("Toggles the weather of all players personal weather"))
                    .permission(Permissions.WEATHER_GLOBAL.p())
            .arguments(GenericArguments.none())
            .executor(
                (src, args) -> {
                  if (!(src instanceof Player)) {
                      PlayerUtils.tell(src, Translations.PLAYER_ONLY_COMMAND.t());
                    return CommandResult.success();
                  }

                  // If true, this prevents any packets from being intercepted.
                  Player player = (Player) src;
                  PlayerWeatherCoreUtil.globalWeatherOff = !PlayerWeatherCoreUtil.globalWeatherOff;
                  PlayerUtils.tell(player, Translations.WEATHER_USING_GLOBAL.ft(PlayerWeatherCoreUtil.globalWeatherOff ? Translations.DEFAULT_OFF.s() : Translations.DEFAULT_ON.s()));


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
                    .permission(Permissions.WEATHER_DEBUG.p())
                    .child(toggleGlobalWeatherCommand, "toggle")
                    .executor(
                        (src, args) -> {
                            if (!(src instanceof Player)) return CommandResult.success();
                            Player player = (Player) src;
                            PlayerUtils.tell(player, Translations.WEATHER_DEBUG.ft(
                                    PlayerWeatherCoreUtil.getPlayersWeather(player.getUniqueId()),
                                    PlayerWeatherCoreUtil.lightningPlayersContains(player.getUniqueId()),
                                    PlayerWeatherLightningUtil.lightningSchedulerStatus()
                            ));
                            return CommandResult.success();
                        }
                    ).build();

    DarwinServer.registerCommand(debugCommand, "weatherdebug", "dweather");
    DarwinServer.registerCommand(playerWeatherCommand, "weatherplugin", "pweather");
    DarwinServer.registerCommand(plotWeatherCommand, "plotweather");
  }
}
