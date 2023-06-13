package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.inject.binding.BindingFunction;

import java.util.Set;

public interface DependencyContext<T> {

    ComponentKey<T> componentKey();

    Set<ComponentKey<?>> dependencies();

    int priority();

    Class<? extends Scope> scope();

    void configure(BindingFunction<T> function) throws ComponentConfigurationException;
}
