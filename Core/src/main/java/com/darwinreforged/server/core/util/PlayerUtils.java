package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.entities.DarwinLocation;
import com.darwinreforged.server.core.entities.DarwinPlayer;

import java.util.Optional;

public abstract class PlayerUtils<T> {

    public abstract void broadcast(String message);

    public abstract void broadcastIfPermitted(String message, String permission);

    public abstract void tell(DarwinPlayer player, String message);

    public abstract void tellPlain(DarwinPlayer player, String message);

    public abstract boolean isOnline(DarwinPlayer player);

    public abstract void kick(DarwinPlayer player);

    public abstract boolean hasPermission(DarwinPlayer player, String permission);

    public abstract Optional<DarwinLocation> getLocation(DarwinPlayer player);



}
