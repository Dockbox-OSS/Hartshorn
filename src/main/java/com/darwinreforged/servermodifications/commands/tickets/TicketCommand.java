package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.permissions.TicketPermissions;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.resources.Translations;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;

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

    // TODO : Split command syntax to Commands in .resources
    void showHelp(CommandSource sender) {
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();

        List<Text> contents = new ArrayList<>();

        if (sender.hasPermission(TicketPermissions.COMMAND_STAFFLIST))
            contents.add(Translations.COMMAND_HELP_COMMENT.ft("/stafflist", Translations.TICKET_COMMAND_STAFFLIST.s()));

        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_OPEN))
            contents.add(Translations.COMMAND_HELP_COMMENT_ARGS.ft("/ticket", "open [reason for opening]", Translations.TICKET_COMMAND_OPEN.s()));

        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_CLOSE_ALL) || sender.hasPermission(TicketPermissions.COMMAND_TICKET_CLOSE_SELF))
            contents.add(Translations.COMMAND_HELP_COMMENT_ARGS.ft("/ticket", "close [ticketID] (comment)", Translations.TICKET_COMMAND_CLOSE.s()));

        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_ASSIGN))
            contents.add(Translations.COMMAND_HELP_COMMENT_ARGS.ft("/ticket", "assign [ticketID] [user]", Translations.TICKET_COMMAND_ASSIGN.s()));

        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_HOLD))
            contents.add(Translations.COMMAND_HELP_COMMENT_ARGS.ft("/ticket", "hold [ticketID]", Translations.TICKET_COMMAND_HOLD.s()));

        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_READ_ALL) || sender.hasPermission(TicketPermissions.COMMAND_TICKET_READ_SELF))
            contents.add(Translations.COMMAND_HELP_COMMENT_ARGS.ft("/ticket", "check (ticketID)", Translations.TICKET_COMMAND_CHECK.s()));

        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_REOPEN))
            contents.add(Translations.COMMAND_HELP_COMMENT_ARGS.ft("/ticket", "reopen", Translations.TICKET_COMMAND_REOPEN.s()));

        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_TELEPORT))
            contents.add(Translations.COMMAND_HELP_COMMENT_ARGS.ft("/ticket", "tp [ticketID]", Translations.TICKET_COMMAND_TP.s()));

        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_CLAIM))
            contents.add(Translations.COMMAND_HELP_COMMENT_ARGS.ft("/ticket", "claim [ticketID]", Translations.TICKET_COMMAND_CLAIM.s()));

        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_UNCLAIM))
            contents.add(Translations.COMMAND_HELP_COMMENT_ARGS.ft("/ticket", "unclaim [ticketID]", Translations.TICKET_COMMAND_UNCLAIM.s()));

        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_BAN))
            contents.add(Translations.COMMAND_HELP_COMMENT_ARGS.ft("/ticket", "ban [playername]", Translations.TICKET_COMMAND_BAN.s()));

        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_BAN))
            contents.add(Translations.COMMAND_HELP_COMMENT_ARGS.ft("/ticket", "unban [playername]", Translations.TICKET_COMMAND_UNBAN.s()));

        if (sender.hasPermission(TicketPermissions.COMMAND_TICKET_COMMENT))
            contents.add(Translations.COMMAND_HELP_COMMENT_ARGS.ft("/ticket", "comment [ticketID] [comment]", Translations.TICKET_COMMAND_COMMENT.s()));

        if (sender.hasPermission(TicketPermissions.COMMAND_RELOAD))
            contents.add(Translations.COMMAND_HELP_COMMENT_ARGS.ft("/ticket", "reload", Translations.TICKET_COMMAND_RELOAD.s()));


        if (!contents.isEmpty()) {
            paginationService.builder()
                    .title(Translations.TICKET_HELP_TITLE.t())
                    .contents(contents)
                    .header(Translations.TICKET_SYNTAX_HINT.t())
                    .padding(Translations.DEFAULT_PADDING.t())
                    .sendTo(sender);
        } else {
            paginationService.builder()
                    .title(Translations.TICKET_HELP_TITLE.t())
                    .contents(Translations.NOT_PERMITTED_CMD_USE.t())
                    .padding(Translations.DEFAULT_PADDING.t())
                    .sendTo(sender);
        }
    }
}
