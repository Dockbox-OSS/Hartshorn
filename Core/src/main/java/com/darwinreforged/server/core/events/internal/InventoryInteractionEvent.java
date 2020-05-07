package com.darwinreforged.server.core.events.internal;

import com.darwinreforged.server.core.entities.living.Target;
import com.darwinreforged.server.core.events.util.Event;

public class InventoryInteractionEvent extends Event {
    public InventoryInteractionEvent(Target target) {
        super(target);
    }
}
