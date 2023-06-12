package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.ComponentKey;

import java.util.Set;

public interface DependencyContext {

    ComponentKey<?> componentKey();

    Set<ComponentKey<?>> dependencies();


}
