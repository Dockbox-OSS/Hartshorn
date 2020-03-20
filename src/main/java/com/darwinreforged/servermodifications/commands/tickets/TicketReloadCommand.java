package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.translations.TicketMessages;
import com.darwinreforged.servermodifications.util.todo.config.TicketConfig;
import com.darwinreforged.servermodifications.util.todo.database.DataStoreManager;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.io.IOException;


public class TicketReloadCommand implements CommandExecutor {

    private final TicketPlugin plugin;

    public TicketReloadCommand(TicketPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        try {
            plugin.config = new TicketConfig(this.plugin);
            plugin.messages = new TicketMessages(this.plugin);
            plugin.setDataStoreManager(new DataStoreManager(this.plugin));
            plugin.loadDataStore();
        } catch (IOException | ObjectMappingException e) {
            e.printStackTrace();
            throw new CommandException(Translations.UNKNOWN_ERROR.ft("Unable to load data."));
        }
        src.sendMessage(plugin.fromLegacy("&bTicket and Player data reloaded."));
        return CommandResult.success();
    }
}
