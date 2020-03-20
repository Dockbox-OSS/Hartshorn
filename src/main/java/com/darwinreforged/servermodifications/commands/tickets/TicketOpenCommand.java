package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.objects.TicketPlayerData;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.translations.TicketMessages;
import com.darwinreforged.servermodifications.translations.Translations;
import com.darwinreforged.servermodifications.util.plugins.TicketUtil;
import com.darwinreforged.servermodifications.util.todo.config.TicketConfig;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.magitechserver.magibridge.MagiBridge;
import net.dv8tion.jda.core.EmbedBuilder;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.darwinreforged.servermodifications.objects.TicketStatus.Closed;
import static com.darwinreforged.servermodifications.objects.TicketStatus.Open;


public class TicketOpenCommand implements CommandExecutor {

  private final TicketPlugin plugin;

  public TicketOpenCommand(TicketPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    String message = args.<String>getOne("message").get();
    /*if (!(src instanceof Player)) {
        throw new CommandException(Translations.UNKNOWN_ERROR.ft("Only players can run this command"));
    }*/

    if (src instanceof Player) {
      Plot plot = null;
      try {
        Location location =
            new Location(
                ((Player) src).getWorld().getName(),
                ((Player) src).getLocation().getBlockX(),
                ((Player) src).getLocation().getBlockY(),
                ((Player) src).getLocation().getBlockZ());

        plot = Plot.getPlot(location);
        if (plot != null) {
          message = plot.getWorldName() + "  |  " + plot.getId().toString();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      Player player = (Player) src;
      UUID uuid = player.getUniqueId();

      if (plot != null && !plot.getOwners().contains(player.getUniqueId())) plot = null;

      if (TicketConfig.server.isEmpty()) {
        throw new CommandException(Translations.UNKNOWN_ERROR.ft("Server name inside config is not set"));
      }
      if (plugin.getWaitTimer().contains(src.getName())) {
        throw new CommandException(TicketMessages.getTicketTooFast(TicketConfig.delayTimer));
      }
      final List<TicketData> tickets =
          new ArrayList<TicketData>(plugin.getDataStore().getTicketData());
      int totalTickets = 0;
      boolean duplicate = false;
      int ticketID = tickets.size() + 1;

      if (!tickets.isEmpty()) {
        for (TicketData ticket : tickets) {
          if (ticket.getTicketID() == ticketID) {
            ticketID++;
          }
          if (ticket.getPlayerUUID().equals(uuid) && ticket.getStatus() != Closed) {
            totalTickets++;
          }
          if (TicketConfig.preventDuplicates) {
            if (ticket.getMessage().equals(message)
                && ticket.getStatus() != Closed
                && ticket.getPlayerUUID().equals(uuid)) {
              duplicate = true;
            }
          }
        }
      }

      if (duplicate) {
        throw new CommandException(TicketMessages.getTicketDuplicate());
      }
      if (totalTickets >= TicketConfig.maxTickets) {
        throw new CommandException(TicketMessages.getTicketTooMany());
      }
      if (message.split("\\s+").length < TicketConfig.minWords) {
        throw new CommandException(TicketMessages.getTicketTooShort(TicketConfig.minWords));
      }

      final List<TicketPlayerData> playerData =
              new ArrayList<>(plugin.getDataStore().getPlayerData());
      for (TicketPlayerData pData : playerData) {
        if (pData.getPlayerName().equals(src.getName()) && pData.getBannedStatus() == 1) {
          throw new CommandException(Translations.TICKET_ERROR_BANNED.t());
        }
      }

      if (plot != null) {
        try {
          TicketData ticketData =
              new TicketData(
                  ticketID,
                  String.valueOf(uuid),
                  UUID.fromString("00000000-0000-0000-0000-000000000000").toString(),
                  "",
                  System.currentTimeMillis() / 1000,
                  player.getWorld().getName(),
                  player.getLocation().getBlockX(),
                  player.getLocation().getBlockY(),
                  player.getLocation().getBlockZ(),
                  player.getHeadRotation().getX(),
                  player.getHeadRotation().getY(),
                  message,
                  Open,
                  0,
                  TicketConfig.server);

          player.sendMessage(TicketMessages.getTicketOpenUser(ticketID));
          if (TicketConfig.staffNotification) {
            TicketUtil.notifyOnlineStaffOpen(
                TicketMessages.getTicketOpen(player.getName(), ticketID), ticketID);
          }
          EmbedBuilder embedBuilder = new EmbedBuilder();
          embedBuilder.setColor(Color.YELLOW);
          embedBuilder.setTitle("New submission");
          embedBuilder.addField(
              "Submitted by : " + player.getName(),
              "ID assigned : " + ticketID + "\nPlot : " + message,
              false);
          embedBuilder.setThumbnail("https://app.buildersrefuge.com/img/created.png");
          MagiBridge.jda
              .getTextChannelById("525424284731047946")
              .sendMessage(embedBuilder.build())
              .queue(
                  msg -> {
                    plugin.getLogger().warn("Ticket opened, Discord ID assigned : " + msg.getId());
                    ticketData.setDiscordMessage(msg.getId());
                    plugin.getDataStore().addTicketData(ticketData);
                  });
        } catch (Exception e) {
          player.sendMessage(Translations.UNKNOWN_ERROR.ft("Data was not saved correctly."));
          e.printStackTrace();
        }
      } else {
        src.sendMessage(
            Text.of(
                TextColors.DARK_GRAY,
                "[] ",
                TextColors.RED,
                "You can only open a submission while standing inside your own plot!"));
      }
      plugin.getWaitTimer().add(src.getName());

      Sponge.getScheduler()
          .createTaskBuilder()
          .execute(
              new Runnable() {
                @Override
                public void run() {
                  plugin.getWaitTimer().removeAll(Collections.singleton(src.getName()));
                }
              })
          .delay(TicketConfig.delayTimer, TimeUnit.SECONDS)
          .name("mmctickets-s-openTicketWaitTimer")
          .submit(this.plugin);

      return CommandResult.success();
    } else {
      if (TicketConfig.server.isEmpty()) {
        throw new CommandException(Translations.UNKNOWN_ERROR.ft("Server name inside config is not set"));
      }

      final List<TicketData> tickets =
          new ArrayList<TicketData>(plugin.getDataStore().getTicketData());
      int ticketID = tickets.size() + 1;

      try {
        plugin
            .getDataStore()
            .addTicketData(
                new TicketData(
                    ticketID,
                    UUID.fromString("00000000-0000-0000-0000-000000000000").toString(),
                    UUID.fromString("00000000-0000-0000-0000-000000000000").toString(),
                    "",
                    System.currentTimeMillis() / 1000,
                    Sponge.getServer().getDefaultWorldName(),
                    0,
                    0,
                    0,
                    0.0,
                    0.0,
                    message,
                    Open,
                    0,
                    TicketConfig.server));

        src.sendMessage(TicketMessages.getTicketOpenUser(ticketID));
        if (TicketConfig.staffNotification) {
          TicketUtil.notifyOnlineStaffOpen(TicketMessages.getTicketOpen("Console", ticketID), ticketID);
        }
      } catch (Exception e) {
        src.sendMessage(Translations.UNKNOWN_ERROR.ft("Data was not saved correctly."));
        e.printStackTrace();
      }

      return CommandResult.success();
    }
  }
}
