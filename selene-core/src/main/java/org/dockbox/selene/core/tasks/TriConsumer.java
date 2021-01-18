package org.dockbox.selene.core.tasks;

@FunctionalInterface
public interface TriConsumer<T, U, O> {
    void accept(T t, U u, O o);
}
