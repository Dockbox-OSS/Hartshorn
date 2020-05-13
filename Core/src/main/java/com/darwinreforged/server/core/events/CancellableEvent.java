package com.darwinreforged.server.core.events;

import com.darwinreforged.server.core.types.living.Target;
import com.darwinreforged.server.core.events.util.Event;

/**
 The type Cancellable event.
 */
public abstract class CancellableEvent extends Event {

    private boolean isCancelled = false;

    /**
     Instantiates a new Cancellable event.
     */
    public CancellableEvent() {
    }

    /**
     Instantiates a new Cancellable event.

     @param target
     the target
     */
    public CancellableEvent(Target target) {
        super(target);
    }

    /**
     Is cancelled boolean.

     @return the boolean
     */
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     Sets cancelled.

     @param cancelled
     the cancelled
     */
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }
}
