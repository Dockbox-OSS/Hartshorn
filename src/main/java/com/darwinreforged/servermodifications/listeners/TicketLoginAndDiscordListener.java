package com.darwinreforged.servermodifications.listeners;

import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.objects.TicketPlayerData;
import com.darwinreforged.servermodifications.objects.TicketStatus;
import com.darwinreforged.servermodifications.permissions.TicketPermissions;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.translations.TicketMessages;
import com.darwinreforged.servermodifications.util.TicketUtil;
import com.magitechserver.magibridge.MagiBridge;
import com.magitechserver.magibridge.api.DiscordEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.awt.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TicketLoginAndDiscordListener {

	private TicketPlugin plugin;

	public TicketLoginAndDiscordListener(TicketPlugin instance ) {
		plugin = instance;
	}

	@Listener
	public void onPlayerLogin ( ClientConnectionEvent.Join event, @Root Player player ) {
		// If the playerdata for the player exists, Check if they have changed their name.
		Sponge.getScheduler()
				.createTaskBuilder()
				.execute(
						new Runnable() {
							public void run () {
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
							new Runnable() {
								public void run () {
									if (finalTotalTickets < 2) {
										player.sendMessage(TicketMessages.getTicketCloseOffline());
									} else {
										player.sendMessage(
												TicketMessages.getTicketCloseOfflineMulti(finalTotalTickets, "check self"));
									}
								}
							})
					.delay(5, TimeUnit.SECONDS)
					.name("mmctickets-s-sendUserNotifications")
					.submit(this.plugin);
		}

		// Notify staff of the current open tickets when they login
		if (player.hasPermission(TicketPermissions.STAFF)) {
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
							new Runnable() {
								public void run () {

									if (finalOpen == 0) {
										player.sendMessage(TicketMessages.getTicketReadNone());
									}
									if (finalOpen > 0 && finalHeld == 0) {
										player.sendMessage(TicketMessages.getTicketUnresolved(finalOpen, "check"));
									}
									if (finalOpen > 0 && finalHeld > 0) {
										player.sendMessage(
												TicketMessages.getTicketUnresolvedHeld(finalOpen, finalHeld, "check"));
									}
								}
							})
					.delay(3, TimeUnit.SECONDS)
					.name("mmctickets-s-sendStaffNotifications")
					.submit(this.plugin);
		}

		// Send update notification to players with permission
		if (player.hasPermission(TicketPermissions.NOTIFY)) {
			plugin.updatechecker.startUpdateCheckPlayer(player);
		}
	}

	@Listener
	public void onDiscordMessage ( DiscordEvent.MessageEvent event ) {
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
						String playerName =
								TicketUtil.getPlayerNameFromData(plugin, ticketData.getPlayerUUID());
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
																		&& t.getMessage().equals(ticketData.getMessage()))
												.count();

						for (String regex :
								new String[] { "(&)([a-f])+", "(&)([0-9])+", "&l", "&n", "&o", "&k", "&m", "&r" })
							age = age.replaceAll(regex, "");

						body += "Player : " + playerName;
						body += "\nSubmitted : " + age;
						body += "\nLocation : " + location;
						body += "\nPlot : " + ticketData.getMessage();
						body += "\nSubmission : #" + ticketNum;
						body +=
								"\nComments/score : "
										+ (ticketData.getComment().length() == 0 ? "None" : ticketData.getComment());

						embed.setColor(Color.CYAN);

						embed.setTitle("Ticket : #" + ticketData.getTicketID());
						embed.addField("", body, true);

						message.setEmbed(embed.build());
						MagiBridge.jda
								.getTextChannelById("525424273318215681")
								.sendMessage(message.build())
								.queue();
					} else {
						MagiBridge.jda
								.getTextChannelById("525424273318215681")
								.sendMessage(":no_entry: **Unable to find ticket**")
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
												TicketUtil.getPlayerNameFromData(plugin, ticket.getPlayerUUID());
										String age = TicketUtil.getTimeAgo(ticket.getTimestamp());

										String title =
												MessageFormat.format(
														"#{0} | {1}", ticket.getTicketID(), ticket.getMessage());

										for (String regex :
												new String[] {
														"(&)([a-f])+", "(&)([0-9])+", "&l", "&n", "&o", "&k", "&m", "&r"
												})
											age = age.replaceAll(regex, "");

										String body =
												MessageFormat.format("By : {0}\nSubmitted : {1}", playerName, age);
										embed.addField(title, body, true);
									});

					if (amount.get() <= 3) embed.setColor(Color.CYAN);
					else if (amount.get() >= 7) embed.setColor(Color.PINK);
					else embed.setColor(Color.YELLOW);

					embed.setTitle("Open tickets (" + amount.get() + ")");
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
