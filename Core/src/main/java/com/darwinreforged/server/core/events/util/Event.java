package com.darwinreforged.server.core.events.util;

import com.darwinreforged.server.core.types.living.Target;

/**
 * Represents an event.
 *
 * @author Matt
 */
public abstract class Event {
    private Target target;

    public Event() {}

    public Event(Target target) {
        this.target = target;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }
}
