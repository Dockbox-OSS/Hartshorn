package org.dockbox.hartshorn.inject.strategy;

import org.dockbox.hartshorn.inject.DependencyContext;

public interface BindingStrategy {

    <T> boolean canHandle(BindingStrategyContext<T> context);

    <T> DependencyContext<?> handle(BindingStrategyContext<T> context);

    BindingStrategyPriority priority();
}
