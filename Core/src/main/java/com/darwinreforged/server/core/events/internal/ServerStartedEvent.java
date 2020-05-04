package com.darwinreforged.server.core.events.internal;

import com.darwinreforged.server.core.entities.Target;
import com.darwinreforged.server.core.events.util.Event;

public class ServerStartedEvent extends Event {
    public ServerStartedEvent(Target target) {
        super(target);
    }
}
