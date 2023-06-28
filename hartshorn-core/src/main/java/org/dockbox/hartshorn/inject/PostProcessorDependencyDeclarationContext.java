package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public record PostProcessorDependencyDeclarationContext<T extends ComponentPostProcessor>(
        TypeView<T> type) implements DependencyDeclarationContext<T> {

    @Override
    public String id() {
        return null;
    }
}
