package com.darwinreforged.server.core.entities;

import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.util.PlayerUtils;
import com.google.common.base.Preconditions;
import com.intellectualcrafters.plot.object.PlotPlayer;

import java.util.Optional;
import java.util.UUID;

public class DarwinPlayer extends Target {

    public DarwinPlayer(UUID uuid, String name) {
        super.uuid = uuid;
        super.name = name;
    }

    public DarwinPlayer(PlotPlayer plotPlayer) {
        Preconditions.checkNotNull(plotPlayer);
        super.uuid = plotPlayer.getUUID();
        super.name = plotPlayer.getName();
    }

    public boolean isOnline() {
        return DarwinServer.getUtilChecked(PlayerUtils.class).isOnline(this);
    }

    public void tell(String text) {
        DarwinServer.getUtilChecked(PlayerUtils.class).tell(this, text);
    }

    public void tellPlain(String text) {
        DarwinServer.getUtilChecked(PlayerUtils.class).tellPlain(this, text);
    }

    public void tellIfPermitted(String text, String permission) {
        if (hasPermission(permission)) tell(text);
    }

    public void kick() {
        DarwinServer.getUtilChecked(PlayerUtils.class).kick(this);
    }

    public boolean hasPermission(String permission) {
        return DarwinServer.getUtilChecked(PlayerUtils.class).hasPermission(this, permission);
    }

    public Optional<DarwinLocation> getLocation() {
        return DarwinServer.getUtilChecked(PlayerUtils.class).getLocation(this);
    }

}
