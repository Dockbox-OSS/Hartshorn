package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.context.element.TypeContext;

public interface NameGenerator {
    String get(TypeContext<?> type);
    String get(Class<?> type);
    String get(String type);
}
