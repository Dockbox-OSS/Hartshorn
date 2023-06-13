package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.Scope;

import java.util.Set;

public interface DependencyContext<T> {

    ComponentKey<T> componentKey();

    Set<ComponentKey<?>> dependencies();

    int priority();

    Provider<T> provider();

    Class<? extends Scope> scope();
}
