package com.darwinreforged.server.core.events.internal.player;

import com.darwinreforged.server.core.types.living.Target;
import com.darwinreforged.server.core.events.CancellableEvent;

/**
 The type Inventory interaction event.
 */
public class InventoryInteractionEvent extends CancellableEvent {
    /**
     Instantiates a new Inventory interaction event.

     @param target
     the target
     */
    public InventoryInteractionEvent(Target target) {
        super(target);
    }
}
