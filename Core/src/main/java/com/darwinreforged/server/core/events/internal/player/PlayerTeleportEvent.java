package com.darwinreforged.server.core.events.internal.player;

import com.darwinreforged.server.core.types.living.Target;
import com.darwinreforged.server.core.events.util.CancellableEvent;
import com.darwinreforged.server.core.types.location.DarwinLocation;

/**
 The type Player teleport event.
 */
public class PlayerTeleportEvent extends CancellableEvent {

    private final DarwinLocation oldLocation;
    private final DarwinLocation newLocation;

    public PlayerTeleportEvent(Target target, DarwinLocation oldLocation, DarwinLocation newLocation) {
        super(target);
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
    }

    public DarwinLocation getOldLocation() {
        return oldLocation;
    }

    public DarwinLocation getNewLocation() {
        return newLocation;
    }
}
