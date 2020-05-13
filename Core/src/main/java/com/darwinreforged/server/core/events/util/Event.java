package com.darwinreforged.server.core.events.util;

import com.darwinreforged.server.core.types.living.Target;

/**
 Represents an event.

 @author Matt */
public abstract class Event {
    private Target target;

    /**
     Instantiates a new Event.
     */
    public Event() {}

    /**
     Instantiates a new Event.

     @param target
     the target
     */
    public Event(Target target) {
        this.target = target;
    }

    /**
     Gets target.

     @return the target
     */
    public Target getTarget() {
        return target;
    }

    /**
     Sets target.

     @param target
     the target
     */
    public void setTarget(Target target) {
        this.target = target;
    }
}
