package com.darwinreforged.server.core.entities.living;

import com.darwinreforged.server.core.entities.chat.LegacyText;
import com.darwinreforged.server.core.entities.chat.Text;
import com.darwinreforged.server.core.entities.living.state.GameModes;
import com.darwinreforged.server.core.entities.location.DarwinLocation;
import com.darwinreforged.server.core.entities.location.DarwinWorld;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.util.commands.PlayerUtils;

import java.util.Optional;
import java.util.UUID;

public class DarwinPlayer extends Target {

    public DarwinPlayer(UUID uuid, String name) {
        super.uuid = uuid;
        super.name = name;
    }

    public boolean isOnline() {
        return DarwinServer.getUtilChecked(PlayerUtils.class).isOnline(this);
    }

    public void tell(String text) {
        DarwinServer.getUtilChecked(PlayerUtils.class).tell(this, LegacyText.fromLegacy(text));
    }

    public void tellPlain(String text) {
        DarwinServer.getUtilChecked(PlayerUtils.class).tellPlain(this, LegacyText.fromLegacy(text));
    }

    public void tell(Text text) {

    }

    public void tellIfPermitted(String text, String permission) {
        if (hasPermission(permission)) tell(text);
    }

    public void kick() {
        DarwinServer.getUtilChecked(PlayerUtils.class).kick(this);
    }

    public boolean hasPermission(Permissions permission) {
        return hasPermission(permission.p());
    }

    public boolean hasPermission(String permission) {
        if (DarwinServer.getUtilChecked(PlayerUtils.class).isConsole(this)) return true; // Console
        return DarwinServer.getUtilChecked(PlayerUtils.class).hasPermission(this, permission);
    }

    public Optional<DarwinLocation> getLocation() {
        return DarwinServer.getUtilChecked(PlayerUtils.class).getLocation(this);
    }

    public Optional<DarwinWorld> getWorld() {
        return getLocation().map(DarwinLocation::getWorld);
    }

    public GameModes getGameMode() {
        return DarwinServer.getUtilChecked(PlayerUtils.class).getGameMode(this);
    }

    public void setGameMode(GameModes mode) {
        DarwinServer.getUtilChecked(PlayerUtils.class).setGameMode(mode, this);
    }

    @Override
    public void execute(String cmd) {
        DarwinServer.getUtilChecked(PlayerUtils.class).executeCmd(cmd, this);
    }
}
