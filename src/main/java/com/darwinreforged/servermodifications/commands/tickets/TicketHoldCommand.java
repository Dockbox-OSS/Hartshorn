package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.translations.TicketMessages;
import com.darwinreforged.servermodifications.util.TicketUtil;
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
import java.text.MessageFormat;
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
            throw new CommandException(TicketMessages.getErrorGen("Tickets list is empty."));
        } else {
            for (TicketData ticket : tickets) {
                if (ticket.getTicketID() == ticketID) {
                    if (ticket.getStatus() == Closed) {
                        src.sendMessage(TicketMessages.getErrorTicketAlreadyClosed());
                    }
                    if (ticket.getStatus() == Held) {
                        src.sendMessage(TicketMessages.getErrorTicketlreadyHold());
                    }
                    if (ticket.getStatus() == Claimed && !ticket.getStaffUUID().equals(uuid)) {
                        src.sendMessage(
                                TicketMessages.getErrorTicketClaim(
                                        ticket.getTicketID(),
                                        TicketUtil.getPlayerNameFromData(plugin, ticket.getStaffUUID())));
                    }
                    ticket.setStatus(Held);
                    ticket.setStaffUUID(UUID.fromString("00000000-0000-0000-0000-000000000000").toString());

                    try {
                        plugin.getDataStore().updateTicketData(ticket);
                    } catch (Exception e) {
                        src.sendMessage(TicketMessages.getErrorGen("Unable to put ticket on hold"));
                        e.printStackTrace();
                    }

					TicketUtil.notifyOnlineStaff(TicketMessages.getTicketHold(ticket.getTicketID(), src.getName()));

                    Optional<Player> ticketPlayerOP = Sponge.getServer().getPlayer(ticket.getPlayerUUID());
                    if (ticketPlayerOP.isPresent()) {
                        Player ticketPlayer = ticketPlayerOP.get();
                        ticketPlayer.sendMessage(
                                TicketMessages.getTicketHoldUser(ticket.getTicketID(), src.getName()));
                    }

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(Color.PINK);
                    embedBuilder.setTitle("Submission on hold");
                    embedBuilder.addField(
                            "Submitted by : " + TicketUtil.getPlayerNameFromData(plugin, ticket.getPlayerUUID()),
                            MessageFormat.format(
                                    "ID : #{0}\nPlot : {1}\nClosed by : {2}\nScore : {3}\n",
                                    ticketID,
                                    ticket.getMessage(),
                                    src.getName(),
                                    ticket.getComment().length() == 0 ? "None" : ticket.getComment()),
                            false);
                    embedBuilder.setThumbnail(
                            "https://icon-library.net/images/stop-sign-icon-png/stop-sign-icon-png-8.jpg");

                    MagiBridge.jda
                            .getTextChannelById("525424284731047946")
                            .getMessageById(ticket.getDiscordMessage())
                            .queue(msg -> msg.editMessage(embedBuilder.build()).queue());
                    return CommandResult.success();
                }
            }
            throw new CommandException(TicketMessages.getTicketNotExist(ticketID));
        }
    }
}
