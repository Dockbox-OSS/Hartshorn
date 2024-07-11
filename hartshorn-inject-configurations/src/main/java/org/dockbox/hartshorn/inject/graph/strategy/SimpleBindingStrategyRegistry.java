/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.inject.graph.strategy;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO: #1060 Add documentation
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class SimpleBindingStrategyRegistry extends DefaultContext implements BindingStrategyRegistry {

    private final MultiMap<BindingStrategyPriority, BindingStrategy> strategies = MultiMap.<BindingStrategyPriority, BindingStrategy>builder()
            .mapSupplier(() -> new EnumMap<>(BindingStrategyPriority.class))
            .collectionSupplier(HashSet::new)
            .build();

    @Override
    public Set<BindingStrategy> strategies() {
        return Set.copyOf(this.strategies.allValues());
    }

    @Override
    public BindingStrategyRegistry register(BindingStrategy strategy) {
        this.strategies.put(strategy.priority(), strategy);
        return this;
    }

    @Override
    public BindingStrategyRegistry unregister(BindingStrategy strategy) {
        this.strategies.remove(strategy.priority(), strategy);
        return this;
    }

    @Override
    public BindingStrategyRegistry clear() {
        this.strategies.clear();
        return this;
    }

    @Override
    public Option<BindingStrategy> find(BindingStrategyContext<?> context) {
        for (BindingStrategyPriority priority : BindingStrategyPriority.values()) {
            List<BindingStrategy> matchingStrategies = new ArrayList<>();
            for (BindingStrategy strategy : this.strategies.get(priority)) {
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
