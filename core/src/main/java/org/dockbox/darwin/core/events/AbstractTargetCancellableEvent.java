package org.dockbox.darwin.core.events;

import org.dockbox.darwin.core.objects.events.Cancellable;
import org.dockbox.darwin.core.objects.events.Targetable;
import org.dockbox.darwin.core.objects.targets.Target;
import org.jetbrains.annotations.NotNull;

public class AbstractTargetCancellableEvent implements Cancellable, Targetable {

    private boolean isCancelled;
    private Target target;

    public AbstractTargetCancellableEvent(Target target) {
        this.target = target;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @NotNull
    @Override
    public Target getTarget() {
        return this.target;
    }

    @Override
    public void setTarget(@NotNull Target target) {
        this.target = target;
    }
}
