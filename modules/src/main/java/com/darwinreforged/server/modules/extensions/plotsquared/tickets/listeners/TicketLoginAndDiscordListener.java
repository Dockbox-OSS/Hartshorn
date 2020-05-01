package com.darwinreforged.server.modules.extensions.plotsquared.tickets.listeners;

import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.sponge.utils.PlayerUtils;
import com.darwinreforged.server.modules.extensions.plotsquared.tickets.TicketModule;
import com.darwinreforged.server.modules.extensions.plotsquared.tickets.entities.TicketData;
import com.darwinreforged.server.modules.extensions.plotsquared.tickets.entities.TicketPlayerData;
import com.darwinreforged.server.modules.extensions.plotsquared.tickets.entities.TicketStatus;
import com.darwinreforged.server.modules.extensions.plotsquared.tickets.util.TicketUtil;
import com.magitechserver.magibridge.MagiBridge;
import com.magitechserver.magibridge.api.DiscordEvent;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TicketLoginAndDiscordListener {

    private TicketModule plugin;

    public TicketLoginAndDiscordListener(TicketModule instance) {
        plugin = instance;
    }

    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Join event, @Root Player player) {
        // If the playerdata for the player exists, Check if they have changed their name.
        Sponge.getScheduler()
                .createTaskBuilder()
                .execute(
                        new Runnable() {
                            public void run() {
                                boolean exists = false;
                                final List<TicketPlayerData> playerData =
                                        new ArrayList<TicketPlayerData>(plugin.getDataStore().getPlayerData());
                                for (TicketPlayerData pData : playerData) {
                                    if (pData.getPlayerUUID().equals(player.getUniqueId())
                                            && !pData.getPlayerName().equals(player.getName())) {
                                        exists = true;
                                        pData.setPlayerName(player.getName());
                                        try {
                                            plugin.getDataStore().updatePlayerData(pData);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                if (!exists) {
                                    TicketUtil.checkPlayerData(plugin, player);
                                }
                            }
                        })
                .delay(15, TimeUnit.SECONDS)
                .name("mmctickets-s-checkUserNameOnLogin")
                .submit(this.plugin);

        // Notify a player if a ticket they created was closed while they were offline
        if (plugin.getDataStore().getNotifications().contains(player.getUniqueId())) {
            final List<TicketData> tickets =
                    new ArrayList<TicketData>(plugin.getDataStore().getTicketData());
            int totalTickets = 0;
            for (TicketData ticket : tickets) {
                if (ticket.getPlayerUUID().equals(player.getUniqueId()) && ticket.getNotified() == 0) {
                    totalTickets++;
                    ticket.setNotified(1);
                    try {
                        plugin.getDataStore().updateTicketData(ticket);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            plugin
                    .getDataStore()
                    .getNotifications()
                    .removeAll(Collections.singleton(player.getUniqueId()));
            final int finalTotalTickets = totalTickets;
            Sponge.getScheduler()
                    .createTaskBuilder()
                    .execute(
                            () -> {
                                if (finalTotalTickets < 2)
                                    PlayerUtils.tell(player, Translations.TICKET_CLOSE_OFFLINE.t());
                                else
                                    PlayerUtils.tell(player, Translations.TICKET_CLOSE_OFFLINE_MULTI
                                            .ft(finalTotalTickets, "check self"));
                            })
                    .delay(5, TimeUnit.SECONDS)
                    .name("mmctickets-s-sendUserNotifications")
                    .submit(this.plugin);
        }

        // Notify staff of the current open tickets when they login
        if (player.hasPermission(Permissions.TICKET_STAFF.p())) {
            final List<TicketData> tickets =
                    new ArrayList<TicketData>(plugin.getDataStore().getTicketData());
            int openTickets = 0;
            int heldTickets = 0;
            for (TicketData ticket : tickets) {
                if (ticket.getStatus() == TicketStatus.Open) openTickets++;
                if (ticket.getStatus() == TicketStatus.Held) heldTickets++;
            }
            final int finalOpen = openTickets;
            final int finalHeld = heldTickets;
            Sponge.getScheduler()
                    .createTaskBuilder()
                    .execute(
                            () -> {

                                if (finalOpen == 0) PlayerUtils.tell(player, Translations.TICKET_READ_NONE_OPEN.t());
                                if (finalOpen > 0 && finalHeld == 0)
                                    PlayerUtils.tell(player, Translations.TICKET_UNRESOLVED.ft(finalOpen, "check"));
                                if (finalOpen > 0 && finalHeld > 0)
                                    PlayerUtils.tell(player, Translations.TICKET_UNRESOLVED_HELD
                                            .ft(finalOpen, finalHeld, "check"));
                            })
                    .delay(3, TimeUnit.SECONDS)
                    .name("mmctickets-s-sendStaffNotifications")
                    .submit(this.plugin);
        }
    }

    @Listener
    public void onDiscordMessage(DiscordEvent.MessageEvent event) {
        if (event.getChannel().getId().equals("525424009978970112")
                && event.getUser().getId().equals("151771899985264640")
                && event.getRawMessage().startsWith("[#]")) {
            String message = event.getRawMessage().replace("[#]", "");
            MagiBridge.jda
                    .getTextChannelById("466934478519140372")
                    .sendMessage(message + "\n\n@everyone")
                    .queue();
        } else if (event.getChannel().getId().equals("525424273318215681")) {
            if (event.getRawMessage().startsWith(".check")) {
                String command = event.getRawMessage();
                if (command.split(" ").length == 2) {
                    final List<TicketData> tickets =
                            new ArrayList<TicketData>(plugin.getDataStore().getTicketData());
                    String id = command.split(" ")[1];
                    int ticketId = Integer.parseInt(id);
                    Optional<TicketData> optionalTicket =
                            tickets.stream().filter(ticket -> ticket.getTicketID() == ticketId).findFirst();
                    if (optionalTicket.isPresent()) {
                        TicketData ticketData = optionalTicket.get();
                        MessageBuilder message = new MessageBuilder();
                        EmbedBuilder embed = new EmbedBuilder();

                        String body = "";
                        String playerName = PlayerUtils
                                .getSafely(PlayerUtils.getNameFromUUID(ticketData.getPlayerUUID()));
                        String age = TicketUtil.getTimeAgo(ticketData.getTimestamp());
                        String location =
                                ticketData.getWorld()
                                        + " | x: "
                                        + ticketData.getX()
                                        + ", y: "
                                        + ticketData.getY()
                                        + ", z: "
                                        + ticketData.getZ();

                        int ticketNum =
                                (int)
                                        tickets.stream()
                                                .filter(
                                                        t ->
                                                                t.getPlayerUUID()
                                                                        .toString()
                                                                        .equals(ticketData.getPlayerUUID().toString())
                                                                        && t.getMessage()
                                                                        .equals(ticketData.getMessage()))
                                                .count();

                        for (String regex :
                                new String[]{"(&)([a-f])+", "(&)([0-9])+", "&l", "&n", "&o", "&k", "&m", "&r"})
                            age = age.replaceAll(regex, "");

                        body = Translations.TICKET_DISCORD_DETAILS_BODY.f(
                                playerName,
                                age,
                                location,
                                ticketData.getMessage(),
                                ticketNum,
                                (ticketData.getComment().length() == 0 ? "None" : ticketData.getComment()));

                        embed.setColor(Color.CYAN);

                        embed.setTitle(Translations.TICKET_DISCORD_DETAILS_TITLE.f(ticketData.getTicketID()));
                        embed.addField("", body, true);

                        message.setEmbed(embed.build());
                        MagiBridge.jda
                                .getTextChannelById("525424273318215681")
                                .sendMessage(message.build())
                                .queue();
                    } else {
                        MagiBridge.jda
                                .getTextChannelById("525424273318215681")
                                .sendMessage(Translations.TICKET_DISCORD_DETAIL_NOT_FOUND.s())
                                .queue();
                    }
                } else {
                    final List<TicketData> tickets = new ArrayList<>(plugin.getDataStore().getTicketData());
                    MessageBuilder message = new MessageBuilder();
                    EmbedBuilder embed = new EmbedBuilder();
                    AtomicInteger amount = new AtomicInteger();
                    tickets.stream()
                            .filter(t -> t.getStatus().equals(TicketStatus.Open))
                            .forEach(
                                    ticket -> {
                                        amount.getAndIncrement();
                                        String playerName =
                                                PlayerUtils
                                                        .getSafely(PlayerUtils.getNameFromUUID(ticket.getPlayerUUID()));
                                        String age = TicketUtil.getTimeAgo(ticket.getTimestamp());

                                        String title =
                                                MessageFormat.format(
                                                        "#{0} | {1}", ticket.getTicketID(), ticket.getMessage());

                                        for (String regex :
                                                new String[]{
                                                        "(&)([a-f])+", "(&)([0-9])+", "&l", "&n", "&o", "&k", "&m", "&r"
                                                })
                                            age = age.replaceAll(regex, "");

                                        String body = Translations.TICKET_DISCORD_ROW_BODY.f(playerName, age);
                                        embed.addField(title, body, true);
                                    });

                    if (amount.get() <= 3) embed.setColor(Color.CYAN);
                    else if (amount.get() >= 7) embed.setColor(Color.PINK);
                    else embed.setColor(Color.YELLOW);

                    embed.setTitle(Translations.TICKET_DISCORD_ROW_TITLE.f(amount.get()));
                    message.setEmbed(embed.build());
                    MagiBridge.jda
                            .getTextChannelById("525424273318215681")
                            .sendMessage(message.build())
                            .queue();
                }
            }
        }
    }
}
