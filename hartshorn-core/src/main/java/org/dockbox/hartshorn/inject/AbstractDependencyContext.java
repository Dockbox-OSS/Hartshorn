package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.Scope;

import java.util.Set;

public abstract class AbstractDependencyContext<T> implements DependencyContext<T> {

    private final ComponentKey<T> componentKey;
    private final Set<ComponentKey<?>> dependencies;
    private final Class<? extends Scope> scope;
    private final int priority;

    private boolean lazy;
    private boolean singleton;

    protected AbstractDependencyContext(final ComponentKey<T> componentKey, final Set<ComponentKey<?>> dependencies,
                                            final Class<? extends Scope> scope, final int priority) {
        this.componentKey = componentKey;
        this.dependencies = Set.copyOf(dependencies);
        this.scope = scope;
        this.priority = priority;
    }

    public AbstractDependencyContext<T> lazy(final boolean lazy) {
        this.lazy = lazy;
        return this;
    }

    public AbstractDependencyContext<T> singleton(final boolean singleton) {
        this.singleton = singleton;
        return this;
    }

    @Override
    public ComponentKey<T> componentKey() {
        return this.componentKey;
    }

    @Override
    public Set<ComponentKey<?>> dependencies() {
        return this.dependencies;
    }

    @Override
    public int priority() {
        return this.priority;
    }

    @Override
    public Class<? extends Scope> scope() {
        return this.scope;
    }

    public boolean lazy() {
        return lazy;
    }

    public boolean singleton() {
        return singleton;
    }
}
