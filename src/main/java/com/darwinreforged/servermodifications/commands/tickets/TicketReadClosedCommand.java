package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.plugins.TicketUtil;
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

import static com.darwinreforged.servermodifications.objects.TicketStatus.Closed;

public class TicketReadClosedCommand implements CommandExecutor {

    private final TicketPlugin plugin;

    public TicketReadClosedCommand(TicketPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        final List<TicketData> tickets = new ArrayList<TicketData>(plugin.getDataStore().getTicketData());

        if (src instanceof Player) {
            Player player = (Player) src;
        }

        if (tickets.isEmpty()) {
            throw new CommandException(Translations.UNKNOWN_ERROR.ft("Tickets list is empty."));
        } else {
            PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
            List<Text> contents = new ArrayList<>();
            for (TicketData ticket : tickets) {
                if (ticket.getStatus() == Closed) {
                    Text.Builder send = Text.builder();
                    send.append(Translations.TICKET_ROW_SINGLE.ft(ticket.getTicketID(), TicketUtil.getTimeAgo(ticket.getTimestamp()), PlayerUtils.getPlayerOnlineDisplay(ticket.getPlayerUUID()), TicketUtil.getServerFormatted(ticket.getServer()), Translations.shorten(ticket.getMessage())));
                    send.onClick(TextActions.runCommand("/ticket read " + ticket.getTicketID()));
                    send.onHover(TextActions.showText(Translations.TICKET_MORE_INFO.ft(ticket.getTicketID())));

                    contents.add(send.build());
                }
            }

            if (contents.isEmpty()) {
                contents.add(Translations.TICKET_READ_NONE_CLOSED.t());
            }
            paginationService.builder()
                    .title(Translations.CLOSED_TICKETS_TITLE.t())
                    .contents(Lists.reverse(contents))
                    .padding(Translations.DEFAULT_PADDING.t())
                    .sendTo(src);
            return CommandResult.success();
        }
    }
}
