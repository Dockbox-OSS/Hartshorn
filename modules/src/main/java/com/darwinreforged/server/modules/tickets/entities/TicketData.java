package com.darwinreforged.server.modules.tickets.entities;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.List;
import java.util.UUID;

public class TicketData {

  protected String playerUUID, world, staffUUID, comment, message, server;
  protected int ticketID, x, y, z, notified;
  protected Double yaw, pitch;
  protected long timestamp;
  protected TicketStatus status;
  protected String discordMessage;
  private String rank;

  public String getRank() {
    return rank;
  }

  public void setRank(String rank) {
    this.rank = rank;
  }

  public TicketData(
      int ticketID,
      String playerUUID,
      String staffUUID,
      String comment,
      long timestamp,
      String world,
      int x,
      int y,
      int z,
      Double yaw,
      Double pitch,
      String message,
      TicketStatus status,
      int notified,
      String server) {
    this.ticketID = ticketID;
    this.playerUUID = playerUUID;
    this.staffUUID = staffUUID;
    this.comment = comment;
    this.timestamp = timestamp;
    this.world = world;
    this.x = x;
    this.y = y;
    this.z = z;
    this.yaw = yaw;
    this.pitch = pitch;
    this.message = message;
    this.status = status;
    this.notified = notified;
    this.server = server;
  }

  public String getDiscordMessage() {
    return discordMessage;
  }

  public void setDiscordMessage(String discordMessage) {
    this.discordMessage = discordMessage;
  }

  public int getTicketID() {
    return ticketID;
  }

  public UUID getPlayerUUID() {
    return UUID.fromString(playerUUID);
  }

  public UUID getStaffUUID() {
    return UUID.fromString(staffUUID);
  }

  public String getOldPlayer() {
    return playerUUID;
  }

  public String getOldStaffname() {
    return staffUUID;
  }

  public String getComment() {
    return comment.replaceAll("(\\[)(.*)(\\])", "$2");
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getWorld() {
    return world;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getZ() {
    return z;
  }

  public Double getYaw() {
    return yaw;
  }

  public Double getPitch() {
    return pitch;
  }

  public String getMessage() {
    return message.replaceAll("(\\[)(.*)(\\])", "$2");
  }

  public TicketStatus getStatus() {
    return status;
  }

  public int getNotified() {
    return notified;
  }

  public void setStatus(TicketStatus status) {
    this.status = status;
  }

  public void setNotified(int notified) {
    this.notified = notified;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public void setStaffUUID(String uuid) {
    this.staffUUID = uuid;
  }

  public void setPlayerUUID(UUID uuid) {
    this.playerUUID = String.valueOf(uuid);
  }

  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }

  public static class TicketSerializer implements TypeSerializer<TicketData> {
    @SuppressWarnings ("serial")
    public static final TypeToken<List<TicketData>> token = new TypeToken<List<TicketData>>() {
    };

    @Override
    public TicketData deserialize ( TypeToken<?> token, ConfigurationNode node )
            throws ObjectMappingException {
      TicketData data =
              new TicketData(
                      node.getNode("ticketID").getInt(),
                      node.getNode("playerUUID").getString(),
                      node.getNode("staffUUID").getString(),
                      node.getNode("comment").getString(),
                      node.getNode("timestamp").getInt(),
                      node.getNode("world").getString(),
                      node.getNode("x").getInt(),
                      node.getNode("y").getInt(),
                      node.getNode("z").getInt(),
                      node.getNode("yaw").getDouble(),
                      node.getNode("pitch").getDouble(),
                      node.getNode("message").getString(),
                      TicketStatus.valueOf(node.getNode("status").getString()),
                      node.getNode("notified").getInt(),
                      node.getNode("server").getString());
      data.setDiscordMessage(node.getNode("discord").getString());
      return data;
    }

    @Override
    public void serialize ( TypeToken<?> token, TicketData ticket, ConfigurationNode node )
            throws ObjectMappingException {
      node.getNode("ticketID").setValue(ticket.ticketID);
      node.getNode("playerUUID").setValue(ticket.playerUUID);
      node.getNode("staffUUID").setValue(ticket.staffUUID);
      node.getNode("comment").setValue(ticket.comment);
      node.getNode("timestamp").setValue(ticket.timestamp);
      node.getNode("world").setValue(ticket.world);
      node.getNode("x").setValue(ticket.x);
      node.getNode("y").setValue(ticket.y);
      node.getNode("z").setValue(ticket.z);
      node.getNode("yaw").setValue(ticket.yaw);
      node.getNode("pitch").setValue(ticket.pitch);
      node.getNode("message").setValue(ticket.message);
      node.getNode("status").setValue(ticket.status.toString());
      node.getNode("notified").setValue(ticket.notified);
      node.getNode("server").setValue(ticket.server);
      node.getNode("discord").setValue(ticket.discordMessage);
    }
  }
}
