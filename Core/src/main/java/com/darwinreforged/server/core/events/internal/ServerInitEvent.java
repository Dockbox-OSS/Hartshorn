package com.darwinreforged.server.core.events.internal;

import com.darwinreforged.server.core.entities.living.Target;
import com.darwinreforged.server.core.events.util.Event;

public class ServerInitEvent extends Event {
    public ServerInitEvent(Target target) {
        super(target);
    }
}
