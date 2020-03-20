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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static com.darwinreforged.servermodifications.objects.TicketStatus.Claimed;

public class TicketCommentCommand implements CommandExecutor {

    private final TicketPlugin plugin;

    public TicketCommentCommand(TicketPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        final int ticketID = args.<Integer>getOne("ticketID").get();
        final String comment = args.<String>getOne("comment").get();

        final List<TicketData> tickets =
                new ArrayList<TicketData>(plugin.getDataStore().getTicketData());
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if (src instanceof Player) {
            Player player = (Player) src;
            uuid = player.getUniqueId();
        }

        if (tickets.isEmpty()) {
            throw new CommandException(TicketMessages.getErrorGen("Tickets list is empty."));
        } else {
            for (TicketData ticket : tickets) {
                if (ticket.getTicketID() == ticketID) {
                    if (!ticket.getStaffUUID().equals(uuid)
                            && ticket.getStatus() == Claimed
                            && !src.hasPermission(TicketPermissions.CLAIMED_TICKET_BYPASS)) {
                        throw new CommandException(
                                TicketMessages.getErrorTicketClaim(
                                        ticket.getTicketID(),
                                        TicketUtil.getPlayerNameFromData(plugin, ticket.getStaffUUID())));
                    }
                    if (!ticket.getComment().isEmpty()) {
                        if (src.hasPermission(TicketPermissions.COMMAND_TICKET_EDIT_COMMENT)) {
                            Text.Builder action = Text.builder();
                            action.append(
                                    Text.builder()
                                            .append(plugin.fromLegacy(TicketMessages.getYesButton()))
                                            .onHover(
                                                    TextActions.showText(plugin.fromLegacy(TicketMessages.getYesButtonHover())))
                                            .onClick(
                                                    TextActions.executeCallback(
                                                            changeTicketComment(ticketID, comment, src.getName())))
                                            .build());
                            src.sendMessage(TicketMessages.getTicketCommentedit(ticketID));
                            src.sendMessage(action.build());
                            return CommandResult.success();
                        } else {
                            throw new CommandException(
                                    TicketMessages.getErrorGen("There is already a comment on this ticket."));
                        }
                    }
                    ticket.setComment(comment);

                    try {
                        plugin.getDataStore().updateTicketData(ticket);
                    } catch (Exception e) {
                        src.sendMessage(TicketMessages.getErrorGen("Unable to comment on ticket"));
                        e.printStackTrace();
                    }

                    Optional<Player> ticketPlayerOP = Sponge.getServer().getPlayer(ticket.getPlayerUUID());
                    if (ticketPlayerOP.isPresent()) {
                        Player ticketPlayer = ticketPlayerOP.get();
                        ticketPlayer.sendMessage(
                                TicketMessages.getTicketComment(ticket.getTicketID(), src.getName()));
                    }

                    src.sendMessage(TicketMessages.getTicketCommentUser(ticket.getTicketID()));
                    return CommandResult.success();
                }
            }
            throw new CommandException(TicketMessages.getTicketNotExist(ticketID));
        }
    }

    private Consumer<CommandSource> changeTicketComment(int ticketID, String comment, String name) {
        return consumer -> {
            final List<TicketData> tickets =
                    new ArrayList<TicketData>(plugin.getDataStore().getTicketData());
            for (TicketData ticket : tickets) {
                if (ticket.getTicketID() == ticketID) {
                    ticket.setComment(comment);

                    try {
                        plugin.getDataStore().updateTicketData(ticket);
                    } catch (Exception e) {
                        consumer.sendMessage(TicketMessages.getErrorGen("Unable to comment on ticket"));
                        e.printStackTrace();
                    }

                    Optional<Player> ticketPlayerOP = Sponge.getServer().getPlayer(ticket.getPlayerUUID());
                    if (ticketPlayerOP.isPresent()) {
                        Player ticketPlayer = ticketPlayerOP.get();
                        ticketPlayer.sendMessage(TicketMessages.getTicketComment(ticket.getTicketID(), name));
                    }

                    consumer.sendMessage(TicketMessages.getTicketCommentUser(ticket.getTicketID()));
                }
            }
        };
    }
}
