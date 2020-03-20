package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.permissions.TicketPermissions;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.translations.TicketMessages;
import com.darwinreforged.servermodifications.translations.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.plugins.TicketUtil;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.darwinreforged.servermodifications.objects.TicketStatus.Claimed;
import static com.darwinreforged.servermodifications.objects.TicketStatus.Closed;


public class TicketCloseCommand implements CommandExecutor {

  private final TicketPlugin plugin;

  public TicketCloseCommand(TicketPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

    final int ticketID = args.<Integer>getOne("ticketID").get();
    final Optional<Boolean> rejected = args.<Boolean>getOne("rejected");
    final Optional<String> commentOP = args.<String>getOne("comment");

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
          if (ticket.getPlayerUUID().equals(uuid)
              && !src.hasPermission(TicketPermissions.COMMAND_TICKET_CLOSE_SELF)) {
            throw new CommandException(
                    Translations.TICKET_ERROR_PERMISSION.ft(TicketPermissions.COMMAND_TICKET_CLOSE_SELF));
          }
          if (!ticket.getPlayerUUID().equals(uuid)
              && !src.hasPermission(TicketPermissions.COMMAND_TICKET_CLOSE_ALL)) {
            throw new CommandException(Translations.TICKET_ERROR_OWNER.t());
          }
          if (ticket.getStatus() == Closed) {
            throw new CommandException(Translations.TICKET_ERROR_ALREADY_CLOSED.t());
          }
          if (ticket.getStatus() == Claimed
              && !ticket.getStaffUUID().equals(uuid)
              && !src.hasPermission(TicketPermissions.CLAIMED_TICKET_BYPASS)) {
            throw new CommandException(
                Translations.TICKET_ERROR_CLAIM.ft(
                    ticket.getTicketID(),
                    TicketUtil.getPlayerNameFromData(plugin, ticket.getStaffUUID())));
          }
          if (commentOP.isPresent()) {
            String comment = commentOP.get();
            ticket.setComment(comment);
          }
          ticket.setStatus(Closed);
          ticket.setStaffUUID(uuid.toString());

          PlayerUtils.broadcastForPermission(Translations.TICKET_CLOSE.f(ticketID, src.getName()), TicketPermissions.STAFF);
          Optional<Player> ticketPlayerOP = Sponge.getServer().getPlayer(ticket.getPlayerUUID());
          if (ticketPlayerOP.isPresent()) {
            Player ticketPlayer = ticketPlayerOP.get();
            ticketPlayer.sendMessage(
                TicketMessages.getTicketCloseUser(ticket.getTicketID(), src.getName()));
            ticket.setNotified(1);
          } else {
            plugin.getDataStore().getNotifications().add(ticket.getPlayerUUID());
          }
          EmbedBuilder embedBuilder = new EmbedBuilder();
          embedBuilder.setColor(Color.PINK);

          String rank;
          switch (ticket.getWorld()) {
            case "Plots1":
              rank = "Member";
              break;
            case "Plots2":
              rank = "Expert";
              break;
            case "MasterPlots":
              rank = "Mastered Skill";
              break;
            default:
              rank = "Unknown";
          }

          if (rejected.isPresent()) {
            embedBuilder.setTitle("Submission rejected");
          } else {
            embedBuilder.setTitle("Submission approved");
          }

          embedBuilder.addField(
              "Submitted by : " + TicketUtil.getPlayerNameFromData(plugin, ticket.getPlayerUUID()),
              MessageFormat.format(
                      "ID : #{0}\nPlot : {1}\nClosed by : {2}\nComments : {3}\nTime closed : {4}",
                      ticketID,
                      ticket.getMessage(),
                      src.getName(),
                      ticket.getComment().length() == 0 ? "None" : ticket.getComment(),
                      LocalDateTime.now().toString())
                  + (rejected.isPresent() ? "" : "\nPromoted to : " + rank),
              false);
          if (rejected.isPresent()) {
            embedBuilder.setColor(Color.RED);
            embedBuilder.setThumbnail("https://app.buildersrefuge.com/img/rejected.png");
          } else {
            embedBuilder.setColor(Color.GREEN);
            embedBuilder.setThumbnail("https://app.buildersrefuge.com/img/approved.png");
          }

          MagiBridge.jda
              .getTextChannelById("525424284731047946")
              .getMessageById(ticket.getDiscordMessage())
              .queue(msg -> msg.editMessage(embedBuilder.build()).queue());

          try {
            plugin.getDataStore().updateTicketData(ticket);
          } catch (Exception e) {
            src.sendMessage(Translations.UNKNOWN_ERROR.ft("Unable to close ticket"));
            e.printStackTrace();
          }
          return CommandResult.success();
        }
      }
      throw new CommandException(Translations.TICKET_NOT_EXIST.ft(ticketID));
    }
  }
}
