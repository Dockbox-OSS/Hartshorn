package com.darwinreforged.servermodifications.util.todo.database;

import com.darwinreforged.servermodifications.DarwinServer;
import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.objects.TicketPlayerData;
import com.darwinreforged.servermodifications.objects.TicketStatus;
import com.darwinreforged.servermodifications.modules.TicketModule;
import com.darwinreforged.servermodifications.util.todo.FileManager;
import com.darwinreforged.servermodifications.util.todo.config.TicketConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.nucleuspowered.relocate.uk.co.drnaylor.quickstart.exceptions.MissingDependencyException;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class H2DataStore implements IDataStore {

    private final Optional<HikariDataSource> dataSource;

    public H2DataStore() {
        this.dataSource = getDataSource();
    }

    @Override
    public String getDatabaseName() {
        return "H2";
    }

    @Override
    public boolean load() {
        if (!dataSource.isPresent()) {
            DarwinServer.getLogger().error("Selected datastore: 'H2' is not avaiable please select another datastore.");
            return false;
        }
        try (Connection connection = getConnection()) {
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS " + TicketConfig.h2Prefix + "tickets ("
                    + " ticketid INTEGER NOT NULL PRIMARY KEY,"
                    + " playeruuid VARCHAR(60) NOT NULL,"
                    + " staffuuid VARCHAR(60) NOT NULL,"
                    + " comment VARCHAR(700) NOT NULL,"
                    + " timestamp BIGINT NOT NULL,"
                    + " world VARCHAR(100) NOT NULL,"
                    + " coordx INTEGER NOT NULL,"
                    + " coordy INTEGER NOT NULL,"
                    + " coordz INTEGER NOT NULL,"
                    + " yaw DOUBLE NOT NULL,"
                    + " pitch DOUBLE NOT NULL,"
                    + " message VARCHAR(700) NOT NULL,"
                    + " status VARCHAR(20) NOT NULL,"
                    + " notified INTEGER NOT NULL,"
                    + " server VARCHAR(100) NOT NULL,"
                    + " discord VARCHAR(100) NOT NULL"
                    + ");");

            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS " + TicketConfig.h2Prefix + "playerdata ("
                    + "uuid VARCHAR(36) NOT NULL PRIMARY KEY, "
                    + "playername VARCHAR(36) NOT NULL, "
                    + "banned INTEGER NOT NULL"
                    + ");");

            getConnection().commit();
        } catch (SQLException ex) {
            DarwinServer.getLogger().error("Unable to create tables", ex);
            return false;
        }
        return true;
    }

    @Override
    public List<TicketData> getTicketData() {
        List<TicketData> ticketList = new ArrayList<>();

        try (Connection connection = getConnection()) {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + TicketConfig.h2Prefix + "tickets");
            while (rs.next()) {
                TicketData ticketData = new TicketData(
                        rs.getInt("ticketid"),
                        rs.getString("playeruuid"),
                        rs.getString("staffuuid"),
                        rs.getString("comment"),
                        rs.getInt("timestamp"),
                        rs.getString("world"),
                        rs.getInt("coordx"),
                        rs.getInt("coordy"),
                        rs.getInt("coordz"),
                        rs.getDouble("yaw"),
                        rs.getDouble("pitch"),
                        rs.getString("message"),
                        TicketStatus.valueOf(rs.getString("status")),
                        rs.getInt("notified"),
                        rs.getString("server")
                );
                ticketData.setDiscordMessage(rs.getString("discord"));
                ticketList.add(ticketData);
            }
            return ticketList;
        } catch (SQLException ex) {
            DarwinServer.getLogger().info("H2: Couldn't read ticketdata from H2 database.", ex);
            return new ArrayList<>();
        }
    }

    @Override
    public List<TicketPlayerData> getPlayerData() {
        List<TicketPlayerData> playerList = new ArrayList<>();

        try (Connection connection = getConnection()) {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + TicketConfig.h2Prefix + "playerdata");
            while (rs.next()) {
				TicketPlayerData playerData = new TicketPlayerData(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("playername"),
                        rs.getInt("banned")
                );
                playerList.add(playerData);
            }
            return playerList;
        } catch (SQLException ex) {
            DarwinServer.getLogger().info("H2: Couldn't read playerdata from H2 database.", ex);
            return new ArrayList<>();
        }
    }

    @Override
    public ArrayList<UUID> getNotifications() {
        ArrayList<UUID> notifications = new ArrayList<>();
        List<TicketData> ticketData = getTicketData();
        for (TicketData ticket : ticketData) {
            if (ticket.getNotified() == 0 && ticket.getStatus() == TicketStatus.Closed) {
                notifications.add(ticket.getPlayerUUID());
            }
        }
        return notifications;
    }

    @Override
    public Optional<TicketData> getTicket(int ticketID) {
        List<TicketData> ticketList = getTicketData();
        if (ticketList == null || ticketList.isEmpty()) {
            return Optional.empty();
        }
        for (TicketData ticket : ticketList) {
            if (ticket.getTicketID() == ticketID) {
                return Optional.of(ticket);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean addTicketData(TicketData ticketData) {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + TicketConfig.h2Prefix + "tickets VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            statement.setInt(1, ticketData.getTicketID());
            statement.setString(2, ticketData.getPlayerUUID().toString());
            statement.setString(3, ticketData.getStaffUUID().toString());
            statement.setString(4, ticketData.getComment());
            statement.setLong(5, ticketData.getTimestamp());
            statement.setString(6, ticketData.getWorld());
            statement.setInt(7, ticketData.getX());
            statement.setInt(8, ticketData.getY());
            statement.setInt(9, ticketData.getZ());
            statement.setDouble(10, ticketData.getYaw());
            statement.setDouble(11, ticketData.getPitch());
            statement.setString(12, ticketData.getMessage());
            statement.setString(13, ticketData.getStatus().toString());
            statement.setInt(14, ticketData.getNotified());
            statement.setString(15, ticketData.getServer());
            statement.setString(16, ticketData.getDiscordMessage());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            DarwinServer.getLogger().error("H2: Error adding ticketdata", ex);
        }
        return false;
    }

    @Override
    public boolean addPlayerData(TicketPlayerData playerData) {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + TicketConfig.h2Prefix + "playerdata VALUES (?, ?, ?);");
            statement.setString(1, playerData.getPlayerUUID().toString());
            statement.setString(2, playerData.getPlayerName());
            statement.setInt(3, playerData.getBannedStatus());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            DarwinServer.getLogger().error("H2: Error adding playerdata", ex);
        }
        return false;
    }

    @Override
    public boolean updateTicketData(TicketData ticketData) {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("MERGE INTO " + TicketConfig.h2Prefix + "tickets VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            statement.setInt(1, ticketData.getTicketID());
            statement.setString(2, ticketData.getPlayerUUID().toString());
            statement.setString(3, ticketData.getStaffUUID().toString());
            statement.setString(4, ticketData.getComment());
            statement.setLong(5, ticketData.getTimestamp());
            statement.setString(6, ticketData.getWorld());
            statement.setInt(7, ticketData.getX());
            statement.setInt(8, ticketData.getY());
            statement.setInt(9, ticketData.getZ());
            statement.setDouble(10, ticketData.getYaw());
            statement.setDouble(11, ticketData.getPitch());
            statement.setString(12, ticketData.getMessage());
            statement.setString(13, ticketData.getStatus().toString());
            statement.setInt(14, ticketData.getNotified());
            statement.setString(15, ticketData.getServer());
            statement.setString(16, ticketData.getDiscordMessage());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            DarwinServer.getLogger().error("H2: Error updating ticketdata", ex);
        }
        return false;
    }

    @Override
    public boolean updatePlayerData(TicketPlayerData playerData) {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("MERGE INTO " + TicketConfig.h2Prefix + "playerdata VALUES (?, ?, ?);");
            statement.setString(1, playerData.getPlayerUUID().toString());
            statement.setString(2, playerData.getPlayerName());
            statement.setInt(3, playerData.getBannedStatus());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            DarwinServer.getLogger().error("H2: Error updating playerdata", ex);
        }
        return false;
    }

    public boolean hasColumn(String tableName, String columnName) {
        try (Connection connection = getConnection()) {
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getColumns(null, null, tableName, columnName);
            return rs.next();
        } catch (SQLException ex) {
            DarwinServer.getLogger().error("H2: Error checking if column exists.", ex);
        }
        return false;
    }

    public Optional<HikariDataSource> getDataSource() {
        try {
            HikariDataSource ds = new HikariDataSource();
            ds.setDriverClassName("org.h2.Driver");
            Optional<TicketModule> moduleOptional = DarwinServer.getModule(TicketModule.class);
            if (!moduleOptional.isPresent()) throw new MissingDependencyException("Missing ticket module");
            ds.setJdbcUrl("jdbc:h2://" + new File(FileManager.getConfigDirectory(moduleOptional.get()).toFile(), TicketConfig.databaseFile).getAbsolutePath());
            ds.setConnectionTimeout(1000);
            ds.setLoginTimeout(5);
            ds.setAutoCommit(true);
            return Optional.ofNullable(ds);
        } catch (SQLException | MissingDependencyException ex) {
            DarwinServer.getLogger().error("H2: Failed to get datastore.", ex);
            return Optional.empty();
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.get().getConnection();
    }

}
