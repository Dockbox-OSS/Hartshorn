package com.darwinreforged.server.core.events;

import com.darwinreforged.server.core.entities.living.Target;

public abstract class AbstractEvent {
    private Target target;

    public AbstractEvent(Target target) {
        this.target = target;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }
}
