package com.darwinreforged.server.core.types.living;

import java.util.UUID;

/**
 The type Target.
 */
public abstract class Target {
    /**
     The Uuid.
     */
    protected UUID uuid;
    /**
     The Name.
     */
    protected String name;

    /**
     Gets unique id.

     @return the unique id
     */
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     Gets name.

     @return the name
     */
    public String getName() {
        return name;
    }

    /**
     Execute.

     @param cmd
     the cmd
     */
    public abstract void execute(String cmd);
}
