package org.dockbox.darwin.core.events;

import org.dockbox.darwin.core.objects.events.Cancellable;

public abstract class AbstractCancellableEvent implements Cancellable {

    private boolean isCancelled;

    public AbstractCancellableEvent() {}

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }
}
