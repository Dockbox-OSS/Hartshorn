package com.darwinreforged.server.core.entities.location;

import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.util.LocationUtils;

import java.util.UUID;

public class DarwinWorld {

    private final UUID worldUUID;
    private final String name;

    public DarwinWorld(UUID worldUUID, String name) {
        this.worldUUID = worldUUID;
        this.name = name;
    }

    public UUID getWorldUUID() {
        return worldUUID;
    }

    public String getName() {
        return name;
    }

    public int getPlayerCount() {
        return DarwinServer.getUtilChecked(LocationUtils.class).getPlayerCountInWorld(this);
    }

    public void unloadWorld() {
        unloadWorld(true);
    }

    public void unloadWorld(boolean keepLoaded) {
        DarwinServer.getUtilChecked(LocationUtils.class).unloadWorld(this, keepLoaded);
    }
}
