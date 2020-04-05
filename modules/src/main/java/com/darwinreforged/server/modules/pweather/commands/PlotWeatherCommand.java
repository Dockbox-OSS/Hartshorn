package com.darwinreforged.server.modules.pweather.commands;

import com.darwinreforged.server.api.resources.Translations;
import com.darwinreforged.server.api.utils.PlayerUtils;
import com.darwinreforged.server.modules.pweather.utils.PlayerWeatherCoreUtil;
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

public class PlotWeatherCommand
        implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args)
            throws CommandException {
        if (!(src instanceof Player)) {
            PlayerUtils.tell(src, Translations.UNKNOWN_ERROR.ft("This command can only be executed by players"));
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
                            PlayerWeatherCoreUtil
                                    .sendPlayerWeatherPacket(player.getUniqueId(), PlayerWeatherCoreUtil.Weather.RESET);
                            if (PlayerWeatherCoreUtil.playerWeatherContains(player.getUniqueId()))
                                PlayerWeatherCoreUtil.removePlayerWeather(player.getUniqueId());
                            PlayerWeatherCoreUtil.removeLightningPlayer(player.getUniqueId());
                            break;

                        case UNKNOWN:
                            PlayerUtils.tell(player, Translations.UNKNOWN_WEATHER_TYPE.t());

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
                    if (broadcast)
                        PlayerUtils.tell(player, Translations.PLOT_WEATHER_SET.ft(weatherValue.getDisplayName()));
                } else {
                    PlayerUtils.tell(player, Translations.WEATHER_ERROR_NO_OWNER.t());
                }
            } else {
                PlayerUtils.tell(player, Translations.OUTSIDE_PLOT.t());
            }

        } else {
            PlayerUtils.tell(player, Translations.WEATHER_ERROR_NO_WEATHER_TYPE.t());
        }

        return CommandResult.success();
    }
}
