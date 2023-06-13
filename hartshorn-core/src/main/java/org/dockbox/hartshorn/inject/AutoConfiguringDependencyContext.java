package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.util.function.CheckedSupplier;

import java.util.Set;

public class AutoConfiguringDependencyContext<T> implements DependencyContext<T> {

    private final ComponentKey<T> componentKey;
    private final Set<ComponentKey<?>> dependencies;
    private final Class<? extends Scope> scope;
    private final CheckedSupplier<T> supplier;
    private final int priority;

    private boolean lazy;
    private boolean singleton;

    public AutoConfiguringDependencyContext(final ComponentKey<T> componentKey, final Set<ComponentKey<?>> dependencies,
                                            final Class<? extends Scope> scope, final int priority,
                                            final CheckedSupplier<T> supplier) {
        this.componentKey = componentKey;
        this.dependencies = Set.copyOf(dependencies);
        this.scope = scope;
        this.priority = priority;
        this.supplier = supplier;
    }

    public AutoConfiguringDependencyContext<T> lazy(final boolean lazy) {
        this.lazy = lazy;
        return this;
    }

    public AutoConfiguringDependencyContext<T> singleton(final boolean singleton) {
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

    @Override
    public void configure(final BindingFunction<T> function) {
        // TODO: Configure correct binding, this should support:
        //  - Type:type binding (ContextDrivenProvider)
        //  - Type:supplier binding (SupplierProvider)
        //  - Type:singleton binding (SingletonProvider)
        //  - Type:lazy singleton binding (LazySingletonProvider)
    }
}
