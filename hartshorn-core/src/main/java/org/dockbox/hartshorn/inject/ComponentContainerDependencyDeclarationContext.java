package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class ComponentContainerDependencyDeclarationContext<T> implements DependencyDeclarationContext<T> {

    private final ComponentContainer<T> container;

    public ComponentContainerDependencyDeclarationContext(final ComponentContainer<T> container) {
        this.container = container;
    }

    @Override
    public TypeView<T> type() {
        return this.container.type();
    }

    @Override
    public String id() {
        // Components have IDs, but these are not used for dependency resolution, so we can return null here
        return null;
    }
}
