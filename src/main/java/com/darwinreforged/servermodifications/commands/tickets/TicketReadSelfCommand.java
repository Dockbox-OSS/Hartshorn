package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.permissions.TicketPermissions;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.translations.TicketMessages;
import com.darwinreforged.servermodifications.util.TicketUtil;
import com.google.common.collect.Lists;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.ArrayList;
import java.util.List;

import static com.darwinreforged.servermodifications.objects.TicketStatus.*;

public class TicketReadSelfCommand implements CommandExecutor {

    private final TicketPlugin plugin;

    public TicketReadSelfCommand(TicketPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        final List<TicketData> tickets = new ArrayList<TicketData>(plugin.getDataStore().getTicketData());

        if (!src.hasPermission(TicketPermissions.COMMAND_TICKET_READ_SELF)) {
            throw new CommandException(TicketMessages.getErrorPermission(TicketPermissions.COMMAND_TICKET_READ_SELF));
        }
        if (!(src instanceof Player)) {
            throw new CommandException(TicketMessages.getErrorGen("Console users cannot use this command."));
        }
        Player player = (Player) src;

        if (tickets.isEmpty()) {
            throw new CommandException(TicketMessages.getErrorGen("Tickets list is empty."));
        } else {
            PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
            List<Text> contents = new ArrayList<>();
            for (TicketData ticket : tickets) {
                if (ticket.getPlayerUUID().equals(player.getUniqueId())) {
                    String online = TicketUtil.isUserOnline(ticket.getPlayerUUID());
                    Text.Builder send = Text.builder();
                    String status = "";
                    if (ticket.getStatus() == Open) status = "&bOpen &b- ";
                    if (ticket.getStatus() == Held) status = "&3Held &b- ";
                    if (ticket.getStatus() == Closed) status = "&bClosed &b- ";
                    send.append(plugin.fromLegacy(status + "&3#" + ticket.getTicketID() + " " + TicketUtil.getTimeAgo(ticket.getTimestamp()) + " by " + online + TicketUtil.getPlayerNameFromData(plugin, ticket.getPlayerUUID()) + " &3on " + TicketUtil.checkTicketServer(ticket.getServer()) + " &3- &7" + TicketUtil.shortenMessage(ticket.getMessage())));
                    send.onClick(TextActions.runCommand("/ticket read " + ticket.getTicketID()));
                    send.onHover(TextActions.showText(plugin.fromLegacy("Click here to get more details for ticket #" + ticket.getTicketID())));
                    contents.add(send.build());

                }
            }

            if (contents.isEmpty()) {
                contents.add(TicketMessages.getTicketReadNoneSelf());
            }
            paginationService.builder()
                    .title(plugin.fromLegacy("&3Your Tickets"))
                    .contents(Lists.reverse(contents))
                    .padding(Text.of("-"))
                    .sendTo(src);
        }
        return CommandResult.success();
    }
}
