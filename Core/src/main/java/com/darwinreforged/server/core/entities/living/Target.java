package com.darwinreforged.server.core.entities.living;

import java.util.UUID;

public abstract class Target {
    protected UUID uuid;
    protected String name;

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
