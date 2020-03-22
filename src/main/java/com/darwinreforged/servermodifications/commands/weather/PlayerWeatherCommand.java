package com.darwinreforged.servermodifications.commands.weather;

import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.plugins.PlayerWeatherCoreUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.UUID;

public class PlayerWeatherCommand implements CommandExecutor {
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (!(src instanceof Player)) {
      PlayerUtils.tell(src, Translations.UNKNOWN_ERROR.ft("This command can only be executed by players"));
      return CommandResult.success();
    }

    Player player = (Player) src;
    UUID uuid = player.getUniqueId();

    Optional<String> optionalWeather = args.getOne("weather");
    if (optionalWeather.isPresent()) {
      String commandWeather = optionalWeather.get();
      PlayerWeatherCoreUtil.Weather weather = PlayerWeatherCoreUtil.parseWeather(commandWeather);

      switch(weather)
      {
        case UNKNOWN:
          PlayerUtils.tell(player, Translations.UNKNOWN_WEATHER_TYPE.t());
          return CommandResult.success();

        case RESET:
          // set weather to clear
          PlayerWeatherCoreUtil.sendPlayerWeatherPacket(uuid, PlayerWeatherCoreUtil.Weather.RESET);
          if (PlayerWeatherCoreUtil.playerWeatherContains(uuid)) PlayerWeatherCoreUtil.removePlayerWeather(uuid);
          PlayerWeatherCoreUtil.removeLightningPlayer(uuid);

          break;

        case RAINING:
          PlayerWeatherCoreUtil.addToPlayerWeather(uuid, PlayerWeatherCoreUtil.Weather.RAINING);
          PlayerWeatherCoreUtil.sendPlayerWeatherPacket(uuid, PlayerWeatherCoreUtil.Weather.RAINING);

          break;

        case LIGHTNING:
          //Lightning bolts are sent to players in this arrayList
          PlayerWeatherCoreUtil.sendPlayerWeatherPacket(uuid, PlayerWeatherCoreUtil.Weather.RESET);
          PlayerWeatherCoreUtil.addToPlayerWeather(uuid, PlayerWeatherCoreUtil.Weather.LIGHTNING);

          break;

        case LIGHTNINGSTORM:
          PlayerWeatherCoreUtil.addToPlayerWeather(uuid, PlayerWeatherCoreUtil.Weather.LIGHTNINGSTORM);
          //Set player's weather to raining
          PlayerWeatherCoreUtil.sendPlayerWeatherPacket(uuid, PlayerWeatherCoreUtil.Weather.RAINING);
          break;
      }
      PlayerUtils.tell(player, Translations.PLOT_WEATHER_SET.ft(weather.getDisplayName()));
      return CommandResult.success();

    } else {
      PlayerUtils.tell(player, Translations.UNKNOWN_ERROR.ft("Valid weather types: rain, rainy, raining, snowing, snow, lightning, thunder, storm, lightningstorm, thunderstorm, reset, clear, sunny, undo"));
    }

    return CommandResult.empty();
  }
}
