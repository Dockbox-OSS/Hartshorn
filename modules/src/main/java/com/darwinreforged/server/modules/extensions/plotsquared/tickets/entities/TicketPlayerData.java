package com.darwinreforged.server.modules.extensions.plotsquared.tickets.entities;

import com.google.common.reflect.TypeToken;

import java.util.List;
import java.util.UUID;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class TicketPlayerData {
	protected UUID playerUUID;
	protected String playerName;
	protected int bannedStatus;

	public TicketPlayerData(UUID playerUUID, String playerName, int bannedStatus ) {
		this.playerUUID = playerUUID;
		this.playerName = playerName;
		this.bannedStatus = bannedStatus;
	}

	public UUID getPlayerUUID () {
		return playerUUID;
	}

	public String getPlayerName () {
		return playerName;
	}

	public int getBannedStatus () {
		return bannedStatus;
	}

	public void setBannedStatus ( int bannedStatus ) {
		this.bannedStatus = bannedStatus;
	}

	public void setPlayerName ( String playerName ) {
		this.playerName = playerName;
	}

	public static class TicketPlayerDataSerializer implements TypeSerializer<TicketPlayerData> {
		@SuppressWarnings ("serial")
		final public static TypeToken<List<TicketPlayerData>> token = new TypeToken<List<TicketPlayerData>>() {
		};

		@Override
		public TicketPlayerData deserialize (TypeToken<?> token, ConfigurationNode node ) throws ObjectMappingException {
			return new TicketPlayerData(
					UUID.fromString(node.getNode("uuid").getString()),
					node.getNode("name").getString(),
					node.getNode("bannedstatus").getInt());
		}

		@Override
		public void serialize ( TypeToken<?> token, TicketPlayerData playerData, ConfigurationNode node ) throws ObjectMappingException {
			node.getNode("uuid").setValue(playerData.playerUUID.toString());
			node.getNode("name").setValue(playerData.playerName);
			node.getNode("bannedstatus").setValue(playerData.bannedStatus);
		}
	}
}
