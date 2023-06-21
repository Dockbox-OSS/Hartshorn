package org.dockbox.hartshorn.inject.strategy;

public enum BindingStrategyPriority {
    LOWEST(-256),
    LOW(-128),
    MEDIUM(0),
    HIGH(128),
    HIGHEST(256),
    ;

    private final int priority;

    BindingStrategyPriority(final int priority) {
        this.priority = priority;
    }

    public int priority() {
        return this.priority;
    }
}
