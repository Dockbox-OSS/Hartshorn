package com.darwinreforged.server.modules.tickets.util;

import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.sponge.utils.TimeUtils;
import com.darwinreforged.server.modules.tickets.TicketModule;
import com.darwinreforged.server.modules.tickets.entities.TicketPlayerData;
import com.darwinreforged.server.modules.tickets.entities.TicketStatus;

import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

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
        return Translations.valueOf("TICKET_STATUS_" + ticketIDStatus.toString().toUpperCase()).s();
    }

    // TODO : Split dataStore to commons
    public static void checkPlayerData(TicketModule plugin, Player player) {
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
}
