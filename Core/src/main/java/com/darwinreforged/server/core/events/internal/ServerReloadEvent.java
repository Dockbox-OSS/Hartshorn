package com.darwinreforged.server.core.events.internal;

import com.darwinreforged.server.core.entities.Target;
import com.darwinreforged.server.core.events.util.Event;

public class ServerReloadEvent extends Event {
    public ServerReloadEvent(Target target) {
        super(target);
    }
}
