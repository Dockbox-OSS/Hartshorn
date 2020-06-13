package org.dockbox.darwin.core.events;

import org.dockbox.darwin.core.objects.events.Targetable;
import org.dockbox.darwin.core.objects.targets.Target;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractTargetEvent implements Targetable {

    private Target target;

    public AbstractTargetEvent(Target target) {
        this.target = target;
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
