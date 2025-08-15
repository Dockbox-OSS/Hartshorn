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

import org.dockbox.hartshorn.inject.graph.declaration.DependencyContext;
import org.dockbox.hartshorn.inject.graph.resolve.ManagedConfigurationDependencyResolver;

/**
 * A resolver for dependency contexts, typically for use within a {@link ManagedConfigurationDependencyResolver}.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface DependencyContextResolver {

    /**
     * Checks whether the given context is compatible with this resolver.
     *
     * @param context The binding strategy context to check
     * @param <T> The type of the component being resolved
     * @return {@code true} if the context is compatible, {@code false} otherwise
     */
    <T> boolean isCompatible(BindingStrategyContext<T> context);

    /**
     * Resolves the dependency context for the given binding.
     *
     * @param context The binding strategy context to resolve
     * @param <T> The type of the component being resolved
     * @return The resolved dependency context
     */
    <T> DependencyContext<?> resolveToDependency(BindingStrategyContext<T> context);

    /**
     * Returns the priority of this binding strategy resolver. Lower values indicate higher priority.
     *
     * @return The priority of this resolver
     */
    BindingStrategyPriority priority();
}
