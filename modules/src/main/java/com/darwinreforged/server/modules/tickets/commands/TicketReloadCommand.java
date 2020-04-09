package com.darwinreforged.server.modules.tickets.commands;

import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.sponge.utils.PlayerUtils;
import com.darwinreforged.server.modules.tickets.TicketModule;
import com.darwinreforged.server.modules.tickets.config.TicketConfig;
import com.darwinreforged.server.modules.tickets.database.DataStoreManager;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.io.IOException;

import ninja.leaping.configurate.objectmapping.ObjectMappingException;


public class TicketReloadCommand
        implements CommandExecutor {

    private final TicketModule plugin;

    public TicketReloadCommand(TicketModule plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args)
            throws CommandException {
        try {
            plugin.config = new TicketConfig(this.plugin);
            plugin.setDataStoreManager(new DataStoreManager());
            plugin.loadDataStore();
        } catch (IOException | ObjectMappingException e) {
            e.printStackTrace();
            throw new CommandException(Translations.UNKNOWN_ERROR.ft("Unable to load data."));
        }
        PlayerUtils.tell(src, Translations.TICKET_RELOAD_SUCCESS.t());
        return CommandResult.success();
    }
}
