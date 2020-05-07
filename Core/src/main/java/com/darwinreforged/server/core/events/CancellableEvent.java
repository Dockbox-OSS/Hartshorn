package com.darwinreforged.server.core.events;

import com.darwinreforged.server.core.entities.living.Target;
import com.darwinreforged.server.core.events.util.Event;

public abstract class CancellableEvent extends Event {

    private boolean isCancelled = false;

    public CancellableEvent() {
    }

    public CancellableEvent(Target target) {
        super(target);
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }
}
