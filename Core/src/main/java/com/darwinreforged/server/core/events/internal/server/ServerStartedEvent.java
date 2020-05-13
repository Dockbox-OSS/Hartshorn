package com.darwinreforged.server.core.events.internal.server;

import com.darwinreforged.server.core.types.living.Target;
import com.darwinreforged.server.core.events.util.Event;

public class ServerStartedEvent extends Event {
    public ServerStartedEvent(Target target) {
        super(target);
    }
}
