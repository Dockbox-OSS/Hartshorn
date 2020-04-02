package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.modules.TicketModule;
import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.resources.Permissions;
import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.magitechserver.magibridge.MagiBridge;

import net.dv8tion.jda.core.EmbedBuilder;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.darwinreforged.servermodifications.objects.TicketStatus.Claimed;
import static com.darwinreforged.servermodifications.objects.TicketStatus.Open;

public class TicketReopenCommand
        implements CommandExecutor {
    private final TicketModule plugin;

    public TicketReopenCommand(TicketModule plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args)
            throws CommandException {
        final int ticketID = args.<Integer>getOne("ticketID").get();

        final List<TicketData> tickets =
                new ArrayList<TicketData>(plugin.getDataStore().getTicketData());

        if (tickets.isEmpty()) {
            throw new CommandException(Translations.UNKNOWN_ERROR.ft("Tickets list is empty."));
        } else {
            for (TicketData ticket : tickets) {
                if (ticket.getTicketID() == ticketID) {
                    if (ticket.getStatus() == Claimed || ticket.getStatus() == Open) {
                        throw new CommandException(Translations.TICKET_ERROR_NOT_CLOSED.ft(ticket.getTicketID()));
                    }
                    if (ticket.getStatus() == Claimed) {
                        throw new CommandException(
                                Translations.TICKET_ERROR_CLAIM.ft(ticket.getTicketID(), PlayerUtils
                                        .getSafely(PlayerUtils
                                                .getNameFromUUID(ticket.getStaffUUID()))));
                    }
                    ticket.setStatus(Open);
                    ticket.setStaffUUID(UUID.fromString("00000000-0000-0000-0000-000000000000").toString());
                    ticket.setComment("");
                    ticket.setNotified(0);

                    try {
                        plugin.getDataStore().updateTicketData(ticket);
                    } catch (Exception e) {
                        PlayerUtils.tell(src, Translations.UNKNOWN_ERROR.ft("Unable to reopen ticket"));
                        e.printStackTrace();
                    }

                    PlayerUtils.broadcastForPermission(Translations.TICKET_REOPEN
                            .ft(src.getName(), ticket.getTicketID()), Permissions.TICKET_STAFF
                            .p());

                    Optional<Player> ticketPlayerOP = Sponge.getServer().getPlayer(ticket.getPlayerUUID());
                    if (ticketPlayerOP.isPresent()) {
                        Player ticketPlayer = ticketPlayerOP.get();
                        PlayerUtils.tell(ticketPlayer, Translations.TICKET_REOPEN_USER
                                .ft(src.getName(), ticket.getTicketID()));
                    }
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(Color.YELLOW);
                    embedBuilder.setTitle(Translations.SUBMISSION_NEW.s());
                    embedBuilder.addField(
                            Translations.TICKET_DISCORD_SUBMITTED_BY
                                    .f(PlayerUtils.getSafely(PlayerUtils.getNameFromUUID(ticket.getPlayerUUID()))),
                            Translations.TICKET_DISCORD_NEW_COMBINED
                                    .f(ticketID, ticket.getMessage(), LocalDateTime.now().toString()),
                            false);
                    embedBuilder.setThumbnail(Translations.TICKET_DISCORD_RESOURCE_NEW.s());

                    MagiBridge.jda
                            .getTextChannelById("525424284731047946")
                            .getMessageById(ticket.getDiscordMessage())
                            .queue(msg -> msg.editMessage(embedBuilder.build()).queue());

                    return CommandResult.success();
                }
            }
            throw new CommandException(Translations.TICKET_NOT_EXIST.ft(ticketID));
        }
    }
}
