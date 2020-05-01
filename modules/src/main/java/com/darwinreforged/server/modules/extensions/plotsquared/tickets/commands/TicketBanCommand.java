package com.darwinreforged.server.modules.extensions.plotsquared.tickets.commands;

import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.modules.extensions.plotsquared.tickets.TicketModule;
import com.darwinreforged.server.modules.extensions.plotsquared.tickets.entities.TicketPlayerData;
import com.darwinreforged.server.modules.extensions.plotsquared.tickets.util.TicketUtil;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.util.ArrayList;
import java.util.List;

public class TicketBanCommand
        implements CommandExecutor {

    private final TicketModule plugin;

    public TicketBanCommand(TicketModule plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args)
            throws CommandException {
        final User user = args.<Player>getOne("playername").get();
        final List<TicketPlayerData> playerData = new ArrayList<TicketPlayerData>(plugin.getDataStore()
                .getPlayerData());

        if (!user.getPlayer().isPresent()) {
            throw new CommandException(Translations.UNKNOWN_ERROR.ft("Unable to get player"));
        } else {
            for (TicketPlayerData pData : playerData) {
                TicketUtil.checkPlayerData(plugin, user.getPlayer().get());
                if (pData.getPlayerUUID().equals(user.getUniqueId())) {
                    if (pData.getBannedStatus() == 1) {
                        throw new CommandException(Translations.TICKET_ERROR_BANNED_ALREADY.ft(user.getName()));
                    }
                    pData.setBannedStatus(1);
                    try {
                        plugin.getDataStore().updatePlayerData(pData);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new CommandException(Translations.TICKET_ERROR_BAN_USER.ft(user.getName()));
                    }
                    return CommandResult.success();
                }
            }
        }
        throw new CommandException(Translations.TICKET_ERROR_USER_NOT_EXIST.ft(user.getName()));
    }
}
