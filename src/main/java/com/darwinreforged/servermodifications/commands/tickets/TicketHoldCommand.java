package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.permissions.TicketPermissions;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.TimeUtils;
import com.magitechserver.magibridge.MagiBridge;
import net.dv8tion.jda.core.EmbedBuilder;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.darwinreforged.servermodifications.objects.TicketStatus.*;

public class TicketHoldCommand implements CommandExecutor {
    private final TicketPlugin plugin;

    public TicketHoldCommand(TicketPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        final int ticketID = args.<Integer>getOne("ticketID").get();

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
                    if (ticket.getStatus() == Closed) {
                        PlayerUtils.tell(src, Translations.TICKET_ERROR_ALREADY_CLOSED.t());
                    }
                    if (ticket.getStatus() == Held) {
                        PlayerUtils.tell(src, Translations.TICKET_ERROR_ALREADY_HOLD.t());
                    }
                    if (ticket.getStatus() == Claimed && !ticket.getStaffUUID().equals(uuid)) {
                        PlayerUtils.tell(src, Translations.TICKET_ERROR_CLAIM.ft(ticket.getTicketID(), PlayerUtils.getNameFromUUID(ticket.getStaffUUID())));
                    }
                    ticket.setStatus(Held);
                    ticket.setStaffUUID(UUID.fromString("00000000-0000-0000-0000-000000000000").toString());

                    try {
                        plugin.getDataStore().updateTicketData(ticket);
                    } catch (Exception e) {
                        PlayerUtils.tell(src, Translations.UNKNOWN_ERROR.ft("Unable to put ticket on hold"));
                        e.printStackTrace();
                    }

                    PlayerUtils.broadcastForPermission(Translations.TICKET_HOLD.ft(ticket.getTicketID(), src.getName()), TicketPermissions.STAFF);

                    Optional<Player> ticketPlayerOP = Sponge.getServer().getPlayer(ticket.getPlayerUUID());
                    if (ticketPlayerOP.isPresent()) {
                        Player ticketPlayer = ticketPlayerOP.get();
                        PlayerUtils.tell(ticketPlayer, Translations.TICKET_HOLD_USER.ft(ticket.getTicketID(), src.getName()));
                    }

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(Color.PINK);
                    embedBuilder.setTitle(Translations.SUBMISSION_ON_HOLD.s());
                    embedBuilder.addField(
                            Translations.TICKET_DISCORD_SUBMITTED_BY.f(PlayerUtils.getSafely(PlayerUtils.getNameFromUUID(ticket.getPlayerUUID()))),
                            Translations.TICKET_DISCORD_CLOSED_COMBINED.f(
                                    ticketID,
                                    ticket.getMessage(),
                                    src.getName(),
                                    ticket.getComment().length() == 0 ? Translations.NONE.s() : ticket.getComment(),
                                    LocalDateTime.now().toString(),
                                    TimeUtils.localDateTimeFromMillis(ticket.getTimestamp()).toString()),
                            false);
                    embedBuilder.setThumbnail(
                            Translations.TICKET_DISCORD_RESOURCE_HELD.s());

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
