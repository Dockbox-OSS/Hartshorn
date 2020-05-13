package com.darwinreforged.server.core.events.internal.server;

import com.darwinreforged.server.core.types.living.Target;
import com.darwinreforged.server.core.events.util.Event;

/**
 The type Server reload event.
 */
public class ServerReloadEvent extends Event {
    /**
     Instantiates a new Server reload event.

     @param target
     the target
     */
    public ServerReloadEvent(Target target) {
        super(target);
    }
}
