package com.darwinreforged.servermodifications.util.plugins;


import com.darwinreforged.servermodifications.objects.TicketPlayerData;
import com.darwinreforged.servermodifications.objects.TicketStatus;
import com.darwinreforged.servermodifications.permissions.TicketPermissions;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.translations.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.TimeUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.List;
import java.util.UUID;

public class TicketUtil {

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) time *= 1000;
        long now = System.currentTimeMillis();
        if (time > now || time <= 0) return null;
        return TimeUtils.getShortTimeDifferenceString(time);
    }

    public static String getServerFormatted(String server) {
        return "&" + Translations.COLOR_PRIMARY.s() + server;
    }

    public static String getTicketStatusColour(TicketStatus ticketIDStatus) {
        return Translations.valueOf("TICKET_" + ticketIDStatus.toString().toUpperCase()).s();
    }

    public static void notifyOnlineStaff(Text message) {
        PlayerUtils.broadcastForPermission(message, TicketPermissions.STAFF, true);
    }

    public static void notifyOnlineStaffOpen(Text message, int ticketID) {
        Text.Builder send = Text.builder();
        send.append(message);
        send.onClick(TextActions.runCommand("/ticket check " + ticketID));
        send.onHover(TextActions.showText(Text.of("Click here to get more details for ticket #" + ticketID)));
        PlayerUtils.broadcastForPermission(send.build(), TicketPermissions.STAFF, true);
    }

    // TODO : Split dataStore to commons
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

    // TODO : Split dataStore to commons
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
}
