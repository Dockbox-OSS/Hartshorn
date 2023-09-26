package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public record TypePathNode<T>(TypeView<T> type, ComponentKey<T> componentKey) {

    public String qualifiedName() {
        return componentKey.qualifiedName(true);
    }
}
