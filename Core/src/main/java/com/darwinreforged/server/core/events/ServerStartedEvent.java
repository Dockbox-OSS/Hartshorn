package com.darwinreforged.server.core.events;

import com.darwinreforged.server.core.entities.Target;
import com.darwinreforged.server.core.events.test.Event;

public class ServerStartedEvent extends Event {
    public ServerStartedEvent(Target target) {
        super(target);
    }
}
