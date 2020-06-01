package com.darwinreforged.server.core.events.internal.player;

import com.darwinreforged.server.core.types.living.Target;
import com.darwinreforged.server.core.events.util.CancellableEvent;

/**
 The type Player move event.
 */
public class PlayerMoveEvent extends CancellableEvent {

    /**
     Instantiates a new Player move event.

     @param target
     the target
     */
    public PlayerMoveEvent(Target target) {
        super(target);
    }
}
