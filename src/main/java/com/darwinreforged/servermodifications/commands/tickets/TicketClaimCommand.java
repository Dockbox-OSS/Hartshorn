package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.permissions.TicketPermissions;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.darwinreforged.servermodifications.objects.TicketStatus.*;

public class TicketClaimCommand implements CommandExecutor {

    private final TicketPlugin plugin;

    public TicketClaimCommand(TicketPlugin plugin) {
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
                    if (!ticket.getStaffUUID().equals(uuid)
                            && ticket.getStatus() == Claimed
                            && !src.hasPermission(TicketPermissions.CLAIMED_TICKET_BYPASS)) {
                        throw new CommandException(
                                Translations.TICKET_ERROR_CLAIM.ft(ticket.getTicketID(), PlayerUtils.getSafely(PlayerUtils.getNameFromUUID(ticket.getStaffUUID()))));
                    }
                    if (ticket.getStaffUUID().equals(uuid) && ticket.getStatus() == Claimed) {
                        throw new CommandException(Translations.TICKET_ERROR_CLAIM.ft(ticket.getTicketID(), "you"));
                    }
                    if (ticket.getStatus() == Closed || ticket.getStatus() == Held) {
                        throw new CommandException(Translations.TICKET_NOT_OPEN.ft(ticketID));
                    }

                    ticket.setStaffUUID(uuid.toString());
                    ticket.setStatus(Claimed);

                    try {
                        plugin.getDataStore().updateTicketData(ticket);
                    } catch (Exception e) {
                        PlayerUtils.tell(src, Translations.UNKNOWN_ERROR.ft("Unable to claim ticket"));
                        e.printStackTrace();
                    }

                    Optional<Player> ticketPlayerOP = Sponge.getServer().getPlayer(ticket.getPlayerUUID());
                    if (ticketPlayerOP.isPresent()) {
                        Player ticketPlayer = ticketPlayerOP.get();
                        PlayerUtils.tell(ticketPlayer, Translations.TICKET_CLAIM_USER.ft(src.getName(), ticket.getTicketID()));
                    }

                    PlayerUtils.broadcastForPermission(Translations.TICKET_CLAIM.ft(src.getName(), ticket.getTicketID()), TicketPermissions.STAFF);

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(Color.GREEN);
                    embedBuilder.setTitle("Submission claimed");
                    embedBuilder.addField(
                            "Submitted by : " + PlayerUtils.getSafely(PlayerUtils.getNameFromUUID(ticket.getPlayerUUID())),
                            "ID : #"
                                    + ticketID
                                    + "\nPlot : "
                                    + ticket.getMessage()
                                    + "\nClaimed by : "
                                    + src.getName(),
                            false);
                    embedBuilder.setThumbnail(
                            "https://webstockreview.net/images/green-clipart-magnifying-glass.png");
                    MagiBridge.jda
                            .getTextChannelById("525424284731047946")
                            .getMessageById(ticket.getDiscordMessage())
                            .queue(
                                    msg -> {
                                        msg.editMessage(embedBuilder.build()).queue();
                                    });
                    //          MagiBridge.jda
                    //              .getTextChannelById("525424284731047946")
                    //              .sendMessage(embedBuilder.build())
                    //              .queue();

                    return CommandResult.success();
                }
            }
            throw new CommandException(Translations.TICKET_NOT_EXIST.ft(ticketID));
        }
    }
}
