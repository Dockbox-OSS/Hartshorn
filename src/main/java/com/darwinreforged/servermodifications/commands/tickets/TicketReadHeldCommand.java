package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.translations.TicketMessages;
import com.darwinreforged.servermodifications.util.TicketUtil;
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

import static com.darwinreforged.servermodifications.objects.TicketStatus.Held;

public class TicketReadHeldCommand implements CommandExecutor {

    private final TicketPlugin plugin;

    public TicketReadHeldCommand(TicketPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        final List<TicketData> tickets = new ArrayList<TicketData>(plugin.getDataStore().getTicketData());

        if (src instanceof Player) {
            Player player = (Player) src;
        }

        if (tickets.isEmpty()) {
            throw new CommandException(TicketMessages.getErrorGen("Tickets list is empty."));
        } else {
            PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
            List<Text> contents = new ArrayList<>();
            for (TicketData ticket : tickets) {
                if (ticket.getStatus() == Held) {
                    String online = TicketUtil.isUserOnline(ticket.getPlayerUUID());
                    Text.Builder send = Text.builder();
                    send.append(plugin.fromLegacy("&3#" + ticket.getTicketID() + " " + TicketUtil.getTimeAgo(ticket.getTimestamp()) + " by " + online + TicketUtil.getPlayerNameFromData(plugin, ticket.getPlayerUUID()) + " &3on " + TicketUtil.checkTicketServer(ticket.getServer()) + " &3- &7" + TicketUtil.shortenMessage(ticket.getMessage())));
                    send.onClick(TextActions.runCommand("/ticket read " + ticket.getTicketID()));
                    send.onHover(TextActions.showText(plugin.fromLegacy("Click here to get more details for ticket #" + ticket.getTicketID())));
                    contents.add(send.build());

                }
            }

            if (contents.isEmpty()) {
                contents.add(TicketMessages.getTicketReadNoneHeld());
            }
            paginationService.builder()
                    .title(plugin.fromLegacy("&3Held Tickets"))
                    .contents(contents)
                    .padding(Text.of("-"))
                    .sendTo(src);
        }
        return CommandResult.success();
    }
}
