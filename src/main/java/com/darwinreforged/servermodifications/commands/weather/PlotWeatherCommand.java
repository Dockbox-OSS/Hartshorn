package com.darwinreforged.servermodifications.commands.weather;

import com.darwinreforged.servermodifications.translations.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.plugins.PlayerWeatherCoreUtil;
import com.intellectualcrafters.plot.flag.Flags;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class PlotWeatherCommand implements CommandExecutor {

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (!(src instanceof Player)) {
      PlayerUtils.tell(src, Translations.PLAYER_ONLY_COMMAND.s());
      return CommandResult.success();
    }

    Player player = (Player) src;
    PlotPlayer plotplayer = PlotPlayer.wrap(player);
    Optional<String> optionalWeather = args.getOne("weather");

    if (optionalWeather.isPresent()) {
      String weather = optionalWeather.get();

      if (plotplayer.getCurrentPlot() != null) {
        Plot plot = plotplayer.getCurrentPlot();

        if (plot.isOwner(player.getUniqueId())) {
          PlayerWeatherCoreUtil.Weather weatherValue = PlayerWeatherCoreUtil.parseWeather(weather);
          boolean broadcast = true;

          switch (weatherValue) {
            case RESET:
              plot.setFlag(Flags.WEATHER, PlayerWeatherCoreUtil.Weather.RESET.getValue());
              break;

            case UNKNOWN:
              PlayerUtils.tell(
                  player,
                      Translations.UNKNOWN_WEATHER_TYPE.s());
              broadcast = false;
              break;

            case RAINING:
              plot.setFlag(Flags.WEATHER, PlayerWeatherCoreUtil.Weather.RAINING.getValue());
              break;

            case LIGHTNING:
              plot.setFlag(Flags.WEATHER, PlayerWeatherCoreUtil.Weather.LIGHTNING.getValue());
              break;

            case LIGHTNINGSTORM:
              plot.setFlag(Flags.WEATHER, PlayerWeatherCoreUtil.Weather.LIGHTNINGSTORM.getValue());
              break;
          }

          if (broadcast) PlayerUtils.tell(player, Translations.PLOT_WEATHER_SET.f(weatherValue.getDisplayName()));
        }
        else {
          PlayerUtils.tell(player, "You must be the owner of the plot to execute this command");
        }
      } else {
        PlayerUtils.tell(player, "You must be in a plot when executing this command");
      }

    } else {
      PlayerUtils.tell(player, "You must enter a weather type");
    }

    return CommandResult.success();
  }
}
