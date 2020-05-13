package com.darwinreforged.server.core.events.internal.server;

import com.darwinreforged.server.core.types.living.Target;
import com.darwinreforged.server.core.events.util.Event;

/**
 The type Server init event.
 */
public class ServerInitEvent extends Event {
    /**
     Instantiates a new Server init event.

     @param target
     the target
     */
    public ServerInitEvent(Target target) {
        super(target);
    }
}
