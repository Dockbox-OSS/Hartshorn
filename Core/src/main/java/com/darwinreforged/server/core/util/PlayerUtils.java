package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.entities.living.DarwinPlayer;
import com.darwinreforged.server.core.entities.living.Target;
import com.darwinreforged.server.core.entities.living.inventory.DarwinItem;
import com.darwinreforged.server.core.entities.living.state.GameModes;
import com.darwinreforged.server.core.entities.location.DarwinLocation;
import com.darwinreforged.server.core.init.AbstractUtility;
import com.darwinreforged.server.core.init.DarwinServer;

import java.util.Optional;
import java.util.UUID;

@AbstractUtility("Common player action utilities")
public abstract class PlayerUtils {

    public abstract void broadcast(String message);

    public abstract void broadcastIfPermitted(String message, String permission);

    public abstract void tell(DarwinPlayer player, String message);

    public abstract void tellPlain(DarwinPlayer player, String message);

    public abstract boolean isOnline(DarwinPlayer player);

    public abstract void kick(DarwinPlayer player);

    public abstract boolean hasPermission(DarwinPlayer player, String permission);

    public abstract Optional<DarwinLocation> getLocation(DarwinPlayer player);

    public abstract DarwinItem<?> getItemInHand(DarwinPlayer player, boolean primaryHand);

    public DarwinItem<?> getItemInHand(DarwinPlayer player) {
        return getItemInHand(player, true);
    }

    public UUID getConsoleId() {
        return UUID.fromString("00000010-0010-0010-0010-000000000010");
    }

    public boolean isConsole(Target player) {
        return (player.getUuid().equals(getConsoleId()) || player instanceof DarwinServer);
    }

    public abstract GameModes getGameMode(DarwinPlayer player);

    public abstract void executeCmd(String cmd, Target target);
}
