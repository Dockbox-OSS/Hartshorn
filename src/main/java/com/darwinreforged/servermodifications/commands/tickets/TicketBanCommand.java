package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.objects.TicketPlayerData;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.translations.TicketMessages;
import com.darwinreforged.servermodifications.util.plugins.TicketUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.util.ArrayList;
import java.util.List;

public class TicketBanCommand implements CommandExecutor {

    private final TicketPlugin plugin;

    public TicketBanCommand(TicketPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        final User user = args.<Player>getOne("playername").get();
        final List<TicketPlayerData> playerData = new ArrayList<TicketPlayerData>(plugin.getDataStore().getPlayerData());

        if (!user.getPlayer().isPresent()) {
            throw new CommandException(TicketMessages.getErrorGen("Unable to get player"));
        } else {
            for (TicketPlayerData pData : playerData) {
                TicketUtil.checkPlayerData(plugin, user.getPlayer().get());
                if (pData.getPlayerUUID().equals(user.getUniqueId())) {
                    if (pData.getBannedStatus() == 1) {
                        throw new CommandException(TicketMessages.getErrorBannedAlready(user.getName()));
                    }
                    pData.setBannedStatus(1);
                    try {
                        plugin.getDataStore().updatePlayerData(pData);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new CommandException(TicketMessages.getErrorBanUser(user.getName()));
                    }
                    return CommandResult.success();
                }
            }
        }
        throw new CommandException(TicketMessages.getErrorUserNotExist(user.getName()));
    }
}
