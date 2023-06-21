package org.dockbox.hartshorn.inject.strategy;

import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleBindingStrategyRegistry implements BindingStrategyRegistry {

    private final MultiMap<BindingStrategyPriority, BindingStrategy> strategies = MultiMap.<BindingStrategyPriority, BindingStrategy>builder()
            .mapSupplier(() -> new EnumMap<>(BindingStrategyPriority.class))
            .collectionSupplier(HashSet::new)
            .build();

    @Override
    public Set<BindingStrategy> strategies() {
        return Set.copyOf(this.strategies.allValues());
    }

    @Override
    public BindingStrategyRegistry register(final BindingStrategy strategy) {
        this.strategies.put(strategy.priority(), strategy);
        return this;
    }

    @Override
    public BindingStrategyRegistry unregister(final BindingStrategy strategy) {
        this.strategies.remove(strategy.priority(), strategy);
        return this;
    }

    @Override
    public BindingStrategyRegistry clear() {
        this.strategies.clear();
        return this;
    }

    @Override
    public Option<BindingStrategy> find(final BindingStrategyContext<?> context) {
        for (final BindingStrategyPriority priority : BindingStrategyPriority.values()) {
            final List<BindingStrategy> matchingStrategies = new ArrayList<>();
            for (final BindingStrategy strategy : this.strategies.get(priority)) {
                if (strategy.canHandle(context)) {
                    matchingStrategies.add(strategy);
                }
            }
            if (matchingStrategies.size() > 1) {
                throw new IllegalStateException("Multiple strategies found for " + context + " at priority " + priority);
            }
            else if (!matchingStrategies.isEmpty()) {
                return Option.of(matchingStrategies.get(0));
            }
        }
        return Option.empty();
    }
}
