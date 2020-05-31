package com.darwinreforged.server.core.events.internal.player;

import com.darwinreforged.server.core.types.living.Target;
import com.darwinreforged.server.core.events.util.CancellableEvent;

/**
 The type Player teleport event.
 */
public class PlayerTeleportEvent extends CancellableEvent {

    /**
     Instantiates a new Player teleport event.

     @param target
     the target
     */
    public PlayerTeleportEvent(Target target) {
        super(target);
    }
}
