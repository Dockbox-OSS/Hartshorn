package com.darwinreforged.servermodifications.commands.weather;

import com.darwinreforged.servermodifications.util.PlayerWeatherCoreUtil;
import com.intellectualcrafters.plot.flag.Flags;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class PlotWeatherCommand implements CommandExecutor {

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (!(src instanceof Player)) {
      src.sendMessage(Text.of("This command can only be executed by players"));
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
              PlayerWeatherCoreUtil.sendMessage(
                  player,
                  "That weather type is unknown. If you feel this is an error, "
                      + "feel free to let a staff member know");
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

          if (broadcast) PlayerWeatherCoreUtil.sendMessage(player, "Plot weather set to: " + weatherValue.getDisplayName());
        }
        else {
          PlayerWeatherCoreUtil.sendMessage(player, "You must be the owner of the plot to execute this command");
        }
      } else {
        PlayerWeatherCoreUtil.sendMessage(player, "You must be in a plot when executing this command");
      }

    } else {
      PlayerWeatherCoreUtil.sendMessage(player, "You must enter a weather type");
    }

    return CommandResult.success();
  }
}
