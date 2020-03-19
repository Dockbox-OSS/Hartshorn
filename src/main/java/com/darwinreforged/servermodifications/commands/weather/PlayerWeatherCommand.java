package com.darwinreforged.servermodifications.commands.weather;

import com.darwinreforged.servermodifications.util.PlayerWeatherCoreUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.UUID;

public class PlayerWeatherCommand implements CommandExecutor {
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (!(src instanceof Player)) {
      src.sendMessage(Text.of("This command can only be executed by players"));
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
          PlayerWeatherCoreUtil.sendMessage(
                  player,
                  "That weather type is unknown. If you feel this is an error, "
                          + "feel free to let a staff member know");
          return CommandResult.success();

        case RESET:
          // set weather to clear
          PlayerWeatherCoreUtil.sendPlayerWeatherPacket(uuid, PlayerWeatherCoreUtil.Weather.RESET);
          if (PlayerWeatherCoreUtil.playerWeatherContains(uuid)) PlayerWeatherCoreUtil.removePlayerWeather(uuid);

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
      PlayerWeatherCoreUtil.sendMessage(player, "Personal Weather set to: " + weather.getDisplayName());
      return CommandResult.success();

    } else {
      PlayerWeatherCoreUtil.sendMessage(player, "Valid weather types: rain, rainy, raining, snowing, snow, lightning, thunder, storm, lightningstorm, thunderstorm, reset, clear, sunny, undo");
    }

    return CommandResult.empty();
  }
}
