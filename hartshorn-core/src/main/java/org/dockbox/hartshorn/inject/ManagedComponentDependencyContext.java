package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.inject.binding.BindingFunction;

import java.util.Set;

public class ManagedComponentDependencyContext<T> implements DependencyContext<T> {

    private final ComponentKey<T> componentKey;
    private final Set<ComponentKey<?>> dependencies;

    public ManagedComponentDependencyContext(final ComponentKey<T> componentKey, final Set<ComponentKey<?>> dependencies) {
        this.componentKey = componentKey;
        this.dependencies = dependencies;
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
        return -1;
    }

    @Override
    public Class<? extends Scope> scope() {
        return Scope.DEFAULT_SCOPE.installableScopeType();
    }

    @Override
    public void configure(final BindingFunction<T> function) throws ComponentConfigurationException {
        // Do nothing, require processing or standard instance provision
    }
}
