package com.darwinreforged.servermodifications.util.todo.database;

import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.objects.TicketPlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDataStore {

	String getDatabaseName();

	boolean load();

	List<TicketData> getTicketData();

	List<TicketPlayerData> getPlayerData();

	ArrayList<UUID> getNotifications();

	Optional<TicketData> getTicket(int ticketID);

	boolean addTicketData(TicketData ticketData);

	boolean addPlayerData(TicketPlayerData playerData);

	boolean updateTicketData(TicketData ticketData);

	boolean updatePlayerData(TicketPlayerData playerData);
}
