package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.permissions.TicketPermissions;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.translations.TicketMessages;
import com.darwinreforged.servermodifications.util.plugins.TicketUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.darwinreforged.servermodifications.objects.TicketStatus.Claimed;
import static com.darwinreforged.servermodifications.objects.TicketStatus.Closed;

public class TicketAssignCommand implements CommandExecutor {
    private final TicketPlugin plugin;

    public TicketAssignCommand(TicketPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        final int ticketID = args.<Integer>getOne("ticketID").get();
        final User user = args.<Player>getOne("player").get();

        final List<TicketData> tickets = new ArrayList<TicketData>(plugin.getDataStore().getTicketData());

        if (tickets.isEmpty()) {
            throw new CommandException(TicketMessages.getErrorGen("Tickets list is empty."));
        } else {
            for (TicketData ticket : tickets) {
                if (ticket.getTicketID() == ticketID) {
                    if (ticket.getStatus() == Closed) {
                        src.sendMessage(TicketMessages.getErrorTicketAlreadyClosed());
                    }
                    if (ticket.getStatus() == Claimed && !src.hasPermission(TicketPermissions.CLAIMED_TICKET_BYPASS)) {
                        throw new CommandException(TicketMessages.getErrorTicketClaim(ticket.getTicketID(), TicketUtil.getPlayerNameFromData(plugin, ticket.getStaffUUID())));
                    }
                    ticket.setStatus(Claimed);
                    ticket.setStaffUUID(user.getUniqueId().toString());

                    try {
                        plugin.getDataStore().updateTicketData(ticket);
                    } catch (Exception e) {
                        src.sendMessage(TicketMessages.getErrorGen("Unable to assign " + user.getName() + " to ticket"));
                        e.printStackTrace();
                    }

                    TicketUtil.notifyOnlineStaff(TicketMessages.getTicketAssign(TicketUtil.getPlayerNameFromData(plugin, ticket.getStaffUUID()), ticket.getTicketID()));

                    Optional<Player> ticketPlayerOP = Sponge.getServer().getPlayer(ticket.getPlayerUUID());
                    if (ticketPlayerOP.isPresent()) {
                        Player ticketPlayer = ticketPlayerOP.get();
                        ticketPlayer.sendMessage(TicketMessages.getTicketAssignUser(ticket.getTicketID(), TicketUtil.getPlayerNameFromData(plugin, ticket.getStaffUUID())));
                    }
                    return CommandResult.success();
                }
            }
            throw new CommandException(TicketMessages.getTicketNotExist(ticketID));
        }
    }
}
