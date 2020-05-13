package com.darwinreforged.server.core.events.internal.server;

import com.darwinreforged.server.core.types.living.Target;
import com.darwinreforged.server.core.events.util.Event;

/**
 The type Server started event.
 */
public class ServerStartedEvent extends Event {
    /**
     Instantiates a new Server started event.

     @param target
     the target
     */
    public ServerStartedEvent(Target target) {
        super(target);
    }
}
