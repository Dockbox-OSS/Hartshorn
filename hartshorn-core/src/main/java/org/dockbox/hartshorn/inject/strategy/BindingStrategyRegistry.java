package org.dockbox.hartshorn.inject.strategy;

import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public interface BindingStrategyRegistry {

    Set<BindingStrategy> strategies();

    BindingStrategyRegistry register(BindingStrategy strategy);

    BindingStrategyRegistry unregister(BindingStrategy strategy);

    BindingStrategyRegistry clear();

    Option<BindingStrategy> find(BindingStrategyContext<?> context);
}
