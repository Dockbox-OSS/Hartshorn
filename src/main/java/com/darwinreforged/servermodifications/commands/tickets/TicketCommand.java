package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.permissions.TicketPermissions;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayList;
import java.util.List;

public class TicketCommand implements CommandExecutor {

    private final TicketPlugin plugin;

    public TicketCommand(TicketPlugin instance) {
        plugin = instance;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        showHelp(src);
        return CommandResult.success();
    }

    void showHelp(CommandSource sender) {
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();

        List<Text> contents = new ArrayList<>();
        if (sender.hasPermission(TicketPermissions.COMMAND_STAFFLIST))
            contents.add(formatHelp("/stafflist", "Display a list of online staff members."));
        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_OPEN))
            contents.add(formatHelp("/ticket", "open [reason for opening]", "Open a ticket."));
        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_CLOSE_ALL) || sender.hasPermission(TicketPermissions.COMMAND_TICKET_CLOSE_SELF))
            contents.add(formatHelp("/ticket", "close [ticketID] (comment)", "Close an open ticket."));
        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_ASSIGN))
            contents.add(formatHelp("/ticket", "assign [ticketID] [user]", "Assign an open ticket to a specified user."));
        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_HOLD))
            contents.add(formatHelp("/ticket", "hold [ticketID]", "Put an open ticket on hold."));
        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_READ_ALL) || sender.hasPermission(TicketPermissions.COMMAND_TICKET_READ_SELF))
            contents.add(formatHelp("/ticket", "check (ticketID)", "Display a list of open tickets / Give more detail of a ticketID."));
        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_REOPEN))
            contents.add(formatHelp("/ticket", "reopen", "Reopen's a closed ticket."));
        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_TELEPORT))
            contents.add(formatHelp("/ticket", "tp [ticketID]", "Teleport to where a ticket was created."));
        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_CLAIM))
            contents.add(formatHelp("/ticket", "claim [ticketID]", "Claim an open ticket to let people know you are working on it."));
        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_UNCLAIM))
            contents.add(formatHelp("/ticket", "unclaim [ticketID]", "Unclaim a claimed ticket"));
        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_BAN))
            contents.add(formatHelp("/ticket", "ban [playername]", "Ban a player from opening new tickets"));
        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_BAN))
            contents.add(formatHelp("/ticket", "unban [playername]", "Unban a player from opening new tickets"));
        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_COMMENT))
            contents.add(formatHelp("/ticket", "comment [ticketID] [comment]", "put a comment on a ticket"));
        if (sender.hasPermission(TicketPermissions.COMMAND_RELOAD))
            contents.add(formatHelp("/ticket", "reload", "Reload ticket and player data."));


        if (!contents.isEmpty()) {
            paginationService.builder()
                    .title(plugin.fromLegacy("&3MMCTickets Help"))
                    .contents(contents)
                    .header(plugin.fromLegacy("&3[] = required  () = optional"))
                    .padding(Text.of("="))
                    .sendTo(sender);
        } else {
            paginationService.builder()
                    .title(plugin.fromLegacy("&3MMCTickets Help"))
                    .contents(plugin.fromLegacy("&3You currently do not have any permissions for this plugin."))
                    .padding(Text.of("="))
                    .sendTo(sender);
        }
    }

    public Text formatHelp(String command, String comment) {
        return TextSerializers.FORMATTING_CODE.deserializeUnchecked("&3" + command + " &8- &7" + comment);
    }

    public Text formatHelp(String command, String args, String comment) {
        return TextSerializers.FORMATTING_CODE.deserializeUnchecked("&3" + command + " &b" + args + " &8- &7" + comment);
    }
}
