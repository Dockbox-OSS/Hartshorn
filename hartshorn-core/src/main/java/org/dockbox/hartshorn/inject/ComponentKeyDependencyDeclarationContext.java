package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class ComponentKeyDependencyDeclarationContext<T> implements DependencyDeclarationContext<T> {

    private final ComponentKey<T> key;
    private final TypeView<T> type;

    public ComponentKeyDependencyDeclarationContext(Introspector introspector, ComponentKey<T> key) {
        this.key = key;
        this.type = introspector.introspect(key.parameterizedType());
    }

    @Override
    public TypeView<T> type() {
        return this.type;
    }

    @Override
    public String id() {
        return this.key.name();
    }
}
