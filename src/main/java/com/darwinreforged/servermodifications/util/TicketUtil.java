package com.darwinreforged.servermodifications.util;


import com.darwinreforged.servermodifications.objects.TicketPlayerData;
import com.darwinreforged.servermodifications.objects.TicketStatus;
import com.darwinreforged.servermodifications.permissions.TicketPermissions;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.util.config.TicketConfig;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.title.Title;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.darwinreforged.servermodifications.objects.TicketStatus.*;

public class TicketUtil {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) return null;

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "&bjust now&3";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "&b1 minute ago&3"; // a minute ago
        } else if (diff < 50 * MINUTE_MILLIS) {
            return "&b" + diff / MINUTE_MILLIS + " min ago&3";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "&b1 hour ago&3";
        } else if (diff < 24 * HOUR_MILLIS) {
            return "&b" + diff / HOUR_MILLIS + " hours ago&3";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "&byesterday&3";
        } else {
            return "&b" + diff / DAY_MILLIS + " days ago&3";
        }
    }

    public static String shortenMessage(String message) {
        if (message.length() >= 20) {
            message = message.substring(0, 20) + "...";
        }
        return message;
    }

    public static String isUserOnline(UUID uuid) {
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            if (uuid.equals(player.getUniqueId())) {
                return "&b";
            }
        }
        return "&b";
    }

    public static boolean checkUserOnline(String name) {
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            if (name.equals(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public static String checkTicketServer(String server) {
        if (server.equalsIgnoreCase(TicketConfig.server)) {
            return "&b" + server;
        }
        return "&b" + server;
    }

    public static String getTicketStatusColour(TicketStatus ticketIDStatus) {
        String TicketStatus = "";
        if (ticketIDStatus == Open) TicketStatus = "&bOpen";
        if (ticketIDStatus == Claimed) TicketStatus = "&bClaimed";
        if (ticketIDStatus == Held) TicketStatus = "&bHeld";
        if (ticketIDStatus == Closed) TicketStatus = "&bClosed";

        return TicketStatus;
    }

    public static void notifyOnlineStaff(Text message) {
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            if (player.hasPermission(TicketPermissions.STAFF)) {
                player.sendMessage(message);
            }
        }
    }

    public static void notifyOnlineStaffOpen(Text message, int ticketID) {
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            if (player.hasPermission(TicketPermissions.STAFF)) {
                Text.Builder send = Text.builder();
                send.append(message);
                send.onClick(TextActions.runCommand("/ticket check " + ticketID));
                send.onHover(TextActions.showText(Text.of("Click here to get more details for ticket #" + ticketID)));
                player.sendMessage(send.build());
            }
        }
    }

    public static void notifyOnlineStaffTitle(Text message) {
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            if (player.hasPermission(TicketPermissions.STAFF)) {
                player.sendTitle(Title.builder().subtitle(message).fadeIn(20).fadeOut(20).stay(40).build());
            }
        }
    }

    public static void notifyOnlineStaffSound() {
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            if (player.hasPermission(TicketPermissions.STAFF)) {
                player.playSound(SoundTypes.BLOCK_NOTE_PLING, player.getLocation().getPosition(), 2);
            }
        }
    }

    /*public static String getNameFromUUID(UUID uuid) {
        if (uuid.toString().equals("00000000-0000-0000-0000-000000000000")) {
            return "Console";
        }

        Optional<Player> onlinePlayer = Sponge.getServer().getPlayer(uuid);
        if (onlinePlayer.isPresent()) {
            return Sponge.getServer().getPlayer(uuid).get().getName();
        }
        Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
        return userStorage.get().get(uuid).get().getName();
    }*/

    public static void checkPlayerData(TicketPlugin plugin, Player player) {
        List<TicketPlayerData> playerData = plugin.getDataStore().getPlayerData();
        boolean exists = false;
        for (TicketPlayerData pData : playerData) {
            if (pData.getPlayerUUID().equals(player.getUniqueId())) {
                exists = true;
            }
        }
        if (!exists) {
            plugin.getDataStore().addPlayerData(new TicketPlayerData(player.getUniqueId(), player.getName(), 0));
        }
    }

    public static String getPlayerNameFromData(TicketPlugin plugin, UUID uuid) {
        if (uuid.toString().equals("00000000-0000-0000-0000-000000000000")) {
            return "Console";
        }

        List<TicketPlayerData> playerData = plugin.getDataStore().getPlayerData();
        for (TicketPlayerData pData : playerData) {
            if (pData.getPlayerUUID().equals(uuid)) {
                return pData.getPlayerName();
            }
        }
        return "Unavailable";
    }

    public static UUID getUUIDFromName(String name) {
        Optional<Player> onlinePlayer = Sponge.getServer().getPlayer(name);
        if (onlinePlayer.isPresent()) {
            return Sponge.getServer().getPlayer(name).get().getUniqueId();
        }
        Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
        return userStorage.get().get(name).get().getUniqueId();
    }
}
