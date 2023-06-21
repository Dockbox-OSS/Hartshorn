package org.dockbox.hartshorn.inject.strategy;

import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.context.ApplicationAwareContext;

public interface BindingStrategyContext<T> extends ApplicationAwareContext {

    ComponentContainer<T> componentContainer();
}
