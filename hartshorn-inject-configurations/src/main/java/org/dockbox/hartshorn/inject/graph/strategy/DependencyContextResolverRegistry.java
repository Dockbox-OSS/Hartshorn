/*
 * Copyright 2019-2025 the original author or authors.
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

import org.dockbox.hartshorn.util.configure.Configurer;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

/**
 * A registry for {@link DependencyContextResolver} strategies.
 *
 * @see DependencyContextResolver
 * 
 * @since 0.5.0
 * 
 * @author Guus Lieben
 */
public interface DependencyContextResolverRegistry extends Configurer {

    /**
     * Get all registered resolver strategies.
     *
     * @return the registered strategies
     */
    Set<DependencyContextResolver> strategies();

    /**
     * Register a new resolver strategy. If the strategy is already registered, it will not be added
     * again. If the strategy was previously registered with a different priority, the priority will
     * be updated to the new value.
     *
     * @param strategy the strategy to register
     *
     * @return this registry
     */
    DependencyContextResolverRegistry register(DependencyContextResolver strategy);

    /**
     * Unregister a specific resolver strategy. If the strategy is not registered, or if its
     * priority does not match the registered strategy, no action is taken.
     *
     * @param strategy the strategy to unregister
     *
     * @return this registry
     */
    DependencyContextResolverRegistry unregister(DependencyContextResolver strategy);

    /**
     * Clear all registered resolver strategies.
     *
     * @return this registry
     */
    DependencyContextResolverRegistry clear();

    /**
     * Find a resolver strategy that matches the given binding context.
     *
     * @param context the binding context
     *
     * @return an optional resolver strategy
     */
    Option<DependencyContextResolver> find(BindingStrategyContext<?> context);
}
