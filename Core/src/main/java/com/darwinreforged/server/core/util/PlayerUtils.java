package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.init.AbstractUtility;
import com.darwinreforged.server.core.types.chat.Text;
import com.darwinreforged.server.core.types.living.DarwinPlayer;
import com.darwinreforged.server.core.types.living.MessageReceiver;
import com.darwinreforged.server.core.types.living.Target;
import com.darwinreforged.server.core.types.living.inventory.DarwinItem;
import com.darwinreforged.server.core.types.living.state.GameModes;
import com.darwinreforged.server.core.types.location.DarwinLocation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AbstractUtility("Common player action utilities")
public abstract class PlayerUtils {

    public abstract void broadcast(Text message);

    public abstract void broadcastIfPermitted(Text message, String permission);

    public abstract void tell(MessageReceiver receiver, Text message);

    public abstract void tellNoMarkup(MessageReceiver receiver, Text message);

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
        return (player.getUniqueId().equals(getConsoleId()));
    }

    public abstract GameModes getGameMode(DarwinPlayer player);

    public abstract void setGameMode(GameModes mode, DarwinPlayer player);

    public abstract void executeCmd(String cmd, Target target);

    public abstract List<DarwinPlayer> getOnlinePlayers();

    public abstract Optional<DarwinPlayer> getPlayer(String player);

    public abstract Optional<DarwinPlayer> getPlayer(UUID uuid);

}
