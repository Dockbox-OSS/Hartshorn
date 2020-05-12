package com.darwinreforged.server.core.events.internal.player;

import com.darwinreforged.server.core.types.living.Target;
import com.darwinreforged.server.core.events.CancellableEvent;

public class InventoryInteractionEvent extends CancellableEvent {
    public InventoryInteractionEvent(Target target) {
        super(target);
    }
}
