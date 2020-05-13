package com.darwinreforged.server.core.events.internal.player;

import com.darwinreforged.server.core.types.living.Target;
import com.darwinreforged.server.core.events.util.Event;

/**
 The type Player logged in event.
 */
public class PlayerLoggedInEvent extends Event {
    /**
     Instantiates a new Player logged in event.

     @param target
     the target
     */
    public PlayerLoggedInEvent(Target target) {
        super(target);
    }
}
