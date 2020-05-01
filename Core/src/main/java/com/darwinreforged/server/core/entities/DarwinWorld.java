package com.darwinreforged.server.core.entities;

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
}
