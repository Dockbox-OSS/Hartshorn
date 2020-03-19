package com.darwinreforged.servermodifications.objects;

import java.util.UUID;

public class PaintingSubmission {
	private String Command;
	private int ID;
	private UUID PlayerUUID;
	private String Status;
	
	public String getCommand() {
		return Command;
	}
	
	public void setCommand(String command) {
		Command = command;
	}
	
	public void setID(int id) {
		ID = id;
	}
	public int getID() {
		return ID;
	}
	
	public UUID getPlayerUUID() {
		return PlayerUUID;
	}
	
	public void setPlayerUUID(UUID uuid) {
		PlayerUUID = uuid;
	}
	
	public String getStatus() {
		return Status;
	}
	
	public void setStatus(String status) {
		Status = status;
	}
}
