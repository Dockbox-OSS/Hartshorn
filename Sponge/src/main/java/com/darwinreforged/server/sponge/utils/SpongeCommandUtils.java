package com.darwinreforged.server.sponge.utils;

import com.darwinreforged.server.core.init.UtilityImplementation;
import com.darwinreforged.server.core.types.living.CommandSender;
import com.darwinreforged.server.core.types.living.Console;
import com.darwinreforged.server.core.types.living.DarwinPlayer;
import com.darwinreforged.server.core.types.location.DarwinLocation;
import com.darwinreforged.server.core.util.CommandUtils;
import com.darwinreforged.server.core.util.LocationUtils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;

/**
 * @author dags <dags@dags.me>
 */
@UtilityImplementation(CommandUtils.class)
public class SpongeCommandUtils extends CommandUtils<CommandSource> {

    @Override
    public void executeCommand(CommandSender sender, String command) {
        if (sender instanceof DarwinPlayer) {
            Sponge.getServer().getPlayer(sender.getUniqueId()).ifPresent(p -> Sponge.getCommandManager().process(p, command));
        } else if (sender instanceof Console) {
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
        }
    }

    @Override
    public boolean handleCommandSend(CommandSource source, String command) {
        boolean cancel = false;
        if (source instanceof Player) {
            DarwinPlayer player = new DarwinPlayer(((Player) source).getUniqueId(), source.getName());
            DarwinLocation loc = player.getLocation().orElseGet(LocationUtils::getEmptyWorld);
            cancel = getBus().process(command, player, loc);
        } else if (source instanceof ConsoleSource) {
            cancel = getBus().process(command, Console.instance, LocationUtils.getEmptyWorld());
        }
        return cancel;
    }
}
