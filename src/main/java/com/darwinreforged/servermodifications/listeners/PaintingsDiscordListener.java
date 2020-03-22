package com.darwinreforged.servermodifications.listeners;

import com.darwinreforged.servermodifications.objects.PaintingSubmission;
import com.darwinreforged.servermodifications.plugins.PaintingsPlugin;
import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.magitechserver.magibridge.DiscordHandler;
import com.magitechserver.magibridge.MagiBridge;
import com.magitechserver.magibridge.api.DiscordEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class PaintingsDiscordListener {

   private static final String CHANNEL_ID = "555462653917790228";

   @Listener
   public void onDiscordChat ( DiscordEvent.MessageEvent event ) throws SQLException {
      String playername = event.getMember().getEffectiveName();
      String message = event.getRawMessage().toLowerCase();
      String uri = "jdbc:sqlite:" + PaintingsPlugin.staticRoots + "/DarwinPaintings.db";

      if ((!playername.equals("DR")) && event.getChannel().getId().equals(CHANNEL_ID)) {
         Connection conn = PaintingsPlugin.getDataSource(uri).getConnection();
         if (message.startsWith("!approve")) {
            message = message.replaceAll("!approve", "");
            int id = Integer.parseInt(message.replaceAll(" ", ""));

            if (PaintingsPlugin.submissions.containsKey(id) && PaintingsPlugin.submissions.get(id).getStatus().equals(Translations.PAINTING_STATUS_SUBMITTED.s())) {
               PaintingSubmission submission = PaintingsPlugin.submissions.get(id);
               Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "mail send " + PaintingsPlugin.getUser(submission.getPlayerUUID()).get().getName() + " your submission of " + submission.getCommand() + " was approved, use /paintingslist to obtain it.");
               Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "uploadpainting " + PaintingsPlugin.getUser(submission.getPlayerUUID()).get().getName() + " " + submission.getCommand());
               PaintingsPlugin.submissions.remove(id);
               String query2 = "UPDATE Submissions set Status = 'Approved' where id = '" + id + "'";
               PreparedStatement stmt2 = conn.prepareStatement(query2);
               stmt2.executeUpdate();
               DiscordHandler.sendMessageToChannel(CHANNEL_ID, Translations.PAINTING_APPROVING.f(id));
               conn.close();
            } else {
               DiscordHandler.sendMessageToChannel(CHANNEL_ID, Translations.PAINTING_CANNOT_UPDATE_STATUS.f("approve", id));
            }

         } else if (message.startsWith("!reject")) {
            message = message.replaceAll("!reject", "");
            int id = Integer.parseInt(message.replaceAll(" ", ""));

            if (PaintingsPlugin.submissions.containsKey(id) && PaintingsPlugin.submissions.get(id).getStatus().equals(Translations.PAINTING_STATUS_SUBMITTED.s())) {
               PaintingSubmission submission = PaintingsPlugin.submissions.get(id);
               Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "mail send " + PaintingsPlugin.getUser(submission.getPlayerUUID()).get().getName() + " your submission of " + submission.getCommand() + " was rejected.");
               PaintingsPlugin.submissions.remove(id);
               String query2 = "UPDATE Submissions set Status = 'Rejected' where id = '" + id + "'";
               PreparedStatement stmt2 = conn.prepareStatement(query2);
               stmt2.executeUpdate();
               DiscordHandler.sendMessageToChannel(CHANNEL_ID, Translations.PAINTING_REJECTING.f(id));
               conn.close();
            } else {
               DiscordHandler.sendMessageToChannel(CHANNEL_ID, Translations.PAINTING_CANNOT_UPDATE_STATUS.f("reject", id));
            }
         } else if (message.startsWith("!list")) {
            DiscordHandler.sendMessageToChannel(CHANNEL_ID, Translations.PAINTING_SUBMISSION_LIST.f(PaintingsPlugin.submissions.keySet()));
         } else if (message.startsWith("!info")) {
            message = message.replaceAll("!info ", "");
            int id = Integer.parseInt(message.replaceAll(" ", ""));

            ArrayList<String> queries = new ArrayList<>();
            String query = "SELECT * from Submissions where id = '" + id + "'";
            boolean exists = false;
            PreparedStatement stmt = conn.prepareStatement(query);
            try (ResultSet results = stmt.executeQuery()) {

               while (results.next()) {
                  exists = true;
                  String playerName = PlayerUtils.getSafely(PlayerUtils.getNameFromUUID(UUID.fromString(results.getString("PlayerUUID"))));
                  String status = results.getString("Status");
                  Color color = status.equals(Translations.PAINTING_STATUS_SUBMITTED.s()) ? Color.YELLOW :
                          status.equals(Translations.PAINTING_STATUS_REJECTED.s()) ? Color.RED :
                                  status.equals(Translations.PAINTING_STATUS_APPROVED.s()) ? Color.GREEN : null;

                  if (color != null) {
                     EmbedBuilder embed = new EmbedBuilder();
                     embed.setTitle(Translations.PAINTING_DISCORD_TITLE.s());
                     embed.setColor(Color.YELLOW);
                     embed.addField(
                             Translations.PAINTING_DISCORD_FIELD_TITLE.f(results.getInt("ID")),
                             Translations.PAINTING_DISCORD_FIELD_VALUE.f(playerName, status),
                             false);
                     embed.setImage(results.getString("Command"));
                     MagiBridge.jda.getTextChannelById(CHANNEL_ID).sendMessage(embed.build()).queue();
                     conn.close();
                     results.close();
                     stmt.close();
                     return;
                  }
               }
            }
            if (!exists) {
               DiscordHandler.sendMessageToChannel(CHANNEL_ID, Translations.PAINTING_SUBMISSION_NOT_FOUND.f(id));
            }


            conn.close();
            stmt.close();
         } else if (message.startsWith("!help")) {
            DiscordHandler.sendMessageToChannel(CHANNEL_ID, "Use !approve <ID> to approve a submission and !reject <ID> to reject a submission.");
            DiscordHandler.sendMessageToChannel(CHANNEL_ID, "Use !list to see all submissions awaiting approval/rejection.");
            DiscordHandler.sendMessageToChannel(CHANNEL_ID, "Use !info <ID> to see submission information.");
         }
         conn.close();
      }
   }
}


