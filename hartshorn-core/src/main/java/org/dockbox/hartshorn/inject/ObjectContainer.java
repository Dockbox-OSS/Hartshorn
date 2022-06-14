package org.dockbox.hartshorn.inject;

public class ObjectContainer<T> {

    private final T instance;
    private boolean processed;

    public ObjectContainer(final T instance, final boolean processed) {
        this.instance = instance;
        this.processed = processed;
    }

    public T instance() {
        return this.instance;
    }

    public boolean processed() {
        return this.processed;
    }

    public void processed(final boolean processed) {
        this.processed = processed;
    }
}
