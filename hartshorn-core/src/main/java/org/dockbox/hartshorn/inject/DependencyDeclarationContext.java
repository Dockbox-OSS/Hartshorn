package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.util.introspect.view.TypeView;

public interface DependencyDeclarationContext<T> {

    TypeView<T> type();

    String id();
}
