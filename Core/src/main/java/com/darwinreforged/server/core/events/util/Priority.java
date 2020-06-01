package com.darwinreforged.server.core.events.util;

public enum Priority {
    LAST(0x14), LATE(0xf), NORMAL(0xa), EARLY(0x5), FIRST(0x0);

    private final int priority;

    Priority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
