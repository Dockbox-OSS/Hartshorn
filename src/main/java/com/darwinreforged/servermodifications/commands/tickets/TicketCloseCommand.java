package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.resources.Permissions;
import com.darwinreforged.servermodifications.modules.TicketModule;
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

import static com.darwinreforged.servermodifications.objects.TicketStatus.Claimed;
import static com.darwinreforged.servermodifications.objects.TicketStatus.Closed;


public class TicketCloseCommand implements CommandExecutor {

  private final TicketModule plugin;

  public TicketCloseCommand(TicketModule plugin) {
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
              && !src.hasPermission(Permissions.COMMAND_TICKET_CLOSE_SELF.p())) {
            throw new CommandException(
                    Translations.TICKET_ERROR_PERMISSION.ft(Permissions.COMMAND_TICKET_CLOSE_SELF.p()));
          }
          if (!ticket.getPlayerUUID().equals(uuid)
              && !src.hasPermission(Permissions.COMMAND_TICKET_CLOSE_ALL.p())) {
            throw new CommandException(Translations.TICKET_ERROR_OWNER.t());
          }
          if (ticket.getStatus() == Closed) {
            throw new CommandException(Translations.TICKET_ERROR_ALREADY_CLOSED.t());
          }
          if (ticket.getStatus() == Claimed
              && !ticket.getStaffUUID().equals(uuid)
              && !src.hasPermission(Permissions.CLAIMED_TICKET_BYPASS.p())) {
            throw new CommandException(
                Translations.TICKET_ERROR_CLAIM.ft(
                    ticket.getTicketID(),
                    PlayerUtils.getSafely(PlayerUtils.getNameFromUUID(ticket.getStaffUUID()))));
          }
          if (commentOP.isPresent()) {
            String comment = commentOP.get();
            ticket.setComment(comment);
          }
          ticket.setStatus(Closed);
          ticket.setStaffUUID(uuid.toString());

          PlayerUtils.broadcastForPermission(Translations.TICKET_CLOSE.f(ticketID, src.getName()), Permissions.TICKET_STAFF.p());
          Optional<Player> ticketPlayerOP = Sponge.getServer().getPlayer(ticket.getPlayerUUID());
          if (ticketPlayerOP.isPresent()) {
            Player ticketPlayer = ticketPlayerOP.get();
            PlayerUtils.tell(ticketPlayer, Translations.TICKET_CLOSE_USER.ft(ticket.getTicketID(), src.getName()));
            ticket.setNotified(1);
          } else {
            plugin.getDataStore().getNotifications().add(ticket.getPlayerUUID());
          }
          EmbedBuilder embedBuilder = new EmbedBuilder();
          embedBuilder.setColor(Color.PINK);

          String rank;
          if (Translations.PLOTS1_NAME.s().equals(ticket.getWorld())) {
            rank = Translations.MEMBER_RANK_DISPLAY.s();
          } else if (Translations.PLOTS2_NAME.s().equals(ticket.getWorld())) {
            rank = Translations.EXPERT_RANK_DISPLAY.s();
          } else if (Translations.MASTERPLOTS_NAME.s().equals(ticket.getWorld())) {
            rank = Translations.MASTER_RANK_DISPLAY.s();
          } else {
            rank = Translations.UNKNOWN.s();
          }

          if (rejected.isPresent()) {
            embedBuilder.setTitle(Translations.SUBMISSION_REJECTED.s());
          } else {
            embedBuilder.setTitle(Translations.SUBMISSION_APPROVED.s());
          }

          embedBuilder.addField(
              Translations.TICKET_DISCORD_SUBMITTED_BY.f(PlayerUtils.getSafely(PlayerUtils.getNameFromUUID(ticket.getPlayerUUID()))),
              Translations.TICKET_DISCORD_CLOSED_COMBINED.f(
                      ticketID,
                      ticket.getMessage(),
                      src.getName(),
                      ticket.getComment().length() == 0 ? Translations.NONE.s() : ticket.getComment(),
                      LocalDateTime.now().toString(),
                      TimeUtils.localDateTimeFromMillis(ticket.getTimestamp()).toString())
                  + (rejected.isPresent() ? "" : Translations.TICKET_DISCORD_PROMOTED_TO.f(rank)),
              false);
          if (rejected.isPresent()) {
            embedBuilder.setColor(Color.RED);
            embedBuilder.setThumbnail(Translations.TICKET_DISCORD_RESOURCE_REJECTED.s());
          } else {
            embedBuilder.setColor(Color.GREEN);
            embedBuilder.setThumbnail(Translations.TICKET_DISCORD_RESOURCE_APPROVED.s());
          }

          MagiBridge.jda
              .getTextChannelById("525424284731047946")
              .getMessageById(ticket.getDiscordMessage())
              .queue(msg -> msg.editMessage(embedBuilder.build()).queue());

          try {
            plugin.getDataStore().updateTicketData(ticket);
          } catch (Exception e) {
            PlayerUtils.tell(src, Translations.UNKNOWN_ERROR.ft("Unable to close ticket"));
            e.printStackTrace();
          }
          return CommandResult.success();
        }
      }
      throw new CommandException(Translations.TICKET_NOT_EXIST.ft(ticketID));
    }
  }
}
