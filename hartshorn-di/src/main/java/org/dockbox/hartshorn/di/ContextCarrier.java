package org.dockbox.hartshorn.di;

import org.dockbox.hartshorn.di.context.ApplicationContext;

@FunctionalInterface
public interface ContextCarrier {
    ApplicationContext applicationContext();
}
