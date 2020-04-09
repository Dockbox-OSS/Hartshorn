package com.darwinreforged.server.modules.tickets.commands;

import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.sponge.utils.PlayerUtils;
import com.darwinreforged.server.modules.tickets.TicketModule;
import com.darwinreforged.server.modules.tickets.entities.TicketData;

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

import static com.darwinreforged.server.modules.tickets.entities.TicketStatus.Claimed;

public class TicketCommentCommand
        implements CommandExecutor {

    private final TicketModule plugin;

    public TicketCommentCommand(TicketModule plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args)
            throws CommandException {
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
            throw new CommandException(Translations.UNKNOWN_ERROR.ft("Tickets list is empty."));
        } else {
            for (TicketData ticket : tickets) {
                if (ticket.getTicketID() == ticketID) {
                    if (!ticket.getStaffUUID().equals(uuid)
                            && ticket.getStatus() == Claimed
                            && !src.hasPermission(Permissions.CLAIMED_TICKET_BYPASS.p())) {
                        throw new CommandException(
                                Translations.TICKET_ERROR_CLAIM.ft(ticket.getTicketID(), PlayerUtils
                                        .getSafely(PlayerUtils
                                                .getNameFromUUID(ticket.getStaffUUID()))));
                    }
                    if (!ticket.getComment().isEmpty()) {
                        if (src.hasPermission(Permissions.COMMAND_TICKET_EDIT_COMMENT.p())) {
                            Text.Builder action = Text.builder();
                            action.append(
                                    Text.builder()
                                            .append(Translations.TICKET_YES_BUTTON.t())
                                            .onHover(
                                                    TextActions.showText(Translations.TICKET_YES_BUTTON_HOVER.t()))
                                            .onClick(
                                                    TextActions.executeCallback(
                                                            changeTicketComment(ticketID, comment, src.getName())))
                                            .build());
                            PlayerUtils.tell(src, Translations.TICKET_COMMENT_EDIT.ft(ticketID));
                            PlayerUtils.tell(src, action.build());
                            return CommandResult.success();
                        } else {
                            throw new CommandException(
                                    Translations.UNKNOWN_ERROR.ft("There is already a comment on this ticket."));
                        }
                    }
                    ticket.setComment(comment);

                    try {
                        plugin.getDataStore().updateTicketData(ticket);
                    } catch (Exception e) {
                        PlayerUtils.tell(src, Translations.UNKNOWN_ERROR.ft("Unable to comment on ticket"));
                        e.printStackTrace();
                    }

                    Optional<Player> ticketPlayerOP = Sponge.getServer().getPlayer(ticket.getPlayerUUID());
                    if (ticketPlayerOP.isPresent()) {
                        Player ticketPlayer = ticketPlayerOP.get();
                        PlayerUtils.tell(ticketPlayer, Translations.TICKET_COMMENT
                                .ft(ticket.getTicketID(), src.getName()));
                    }

                    PlayerUtils.tell(src, Translations.TICKET_COMMENT_USER.ft(ticket.getTicketID()));
                    return CommandResult.success();
                }
            }
            throw new CommandException(Translations.TICKET_NOT_EXIST.ft(ticketID));
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
                        PlayerUtils.tell(consumer, Translations.UNKNOWN_ERROR.ft("Unable to comment on ticket"));
                        e.printStackTrace();
                    }

                    Optional<Player> ticketPlayerOP = Sponge.getServer().getPlayer(ticket.getPlayerUUID());
                    if (ticketPlayerOP.isPresent()) {
                        Player ticketPlayer = ticketPlayerOP.get();
                        PlayerUtils.tell(ticketPlayer, Translations.TICKET_COMMENT.ft(ticket.getTicketID(), name));
                    }

                    PlayerUtils.tell(consumer, Translations.TICKET_COMMENT_USER.ft(ticket.getTicketID()));
                }
            }
        };
    }
}
