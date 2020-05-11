package com.darwinreforged.server.core.entities.living;

import java.util.UUID;

public abstract class Target {
    protected UUID uuid;
    protected String name;

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public abstract void execute(String cmd);
}
