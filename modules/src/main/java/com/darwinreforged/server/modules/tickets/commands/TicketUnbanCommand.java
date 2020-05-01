package com.darwinreforged.server.modules.tickets.commands;

import com.darwinreforged.server.modules.tickets.TicketModule;
import com.darwinreforged.server.modules.tickets.entities.TicketPlayerData;
import com.darwinreforged.server.core.resources.Translations;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.util.ArrayList;
import java.util.List;

public class TicketUnbanCommand
        implements CommandExecutor {

    private final TicketModule plugin;

    public TicketUnbanCommand(TicketModule plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args)
            throws CommandException {
        final User user = args.<Player>getOne("playername").get();
        final List<TicketPlayerData> playerData = new ArrayList<TicketPlayerData>(plugin.getDataStore()
                .getPlayerData());

        for (TicketPlayerData pData : playerData) {
            if (pData.getPlayerUUID().equals(user.getUniqueId())) {
                if (pData.getBannedStatus() == 0) {
                    throw new CommandException(Translations.TICKET_ERROR_NOT_BANNED.ft(user.getName()));
                }
                pData.setBannedStatus(0);
                try {
                    plugin.getDataStore().updatePlayerData(pData);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CommandException(Translations.TICKET_ERROR_UNBAN_USER.ft(user.getName()));
                }
                return CommandResult.success();
            }
        }
        throw new CommandException(Translations.TICKET_ERROR_USER_NOT_EXIST.ft(user.getName()));
    }
}
