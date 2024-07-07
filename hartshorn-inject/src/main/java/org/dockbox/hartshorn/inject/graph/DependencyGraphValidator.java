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

package org.dockbox.hartshorn.inject.graph;

import java.util.Set;

import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.graph.declaration.DependencyContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.introspect.Introspector;

/**
 * A validator that checks whether a dependency graph is valid. This validator can be used to validate the graph both
 * before and after the {@link DependencyContext#configure(BindingFunction) dependency configuration phase}.
 *
 * @see DependencyGraph
 * @see DependencyGraphInitializer.Configurer#graphValidator(Customizer)
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface DependencyGraphValidator {

    /**
     * Validates the graph before the configuration phase. This method is invoked before any dependency is configured.
     *
     * @param dependencyGraph the dependency graph to validate
     * @param introspector the introspector to use introspection of types
     * @throws ApplicationException when the graph is invalid, or when the validation fails
     */
    default void validateBeforeConfiguration(DependencyGraph dependencyGraph, Introspector introspector) throws ApplicationException {
        // NOOP, override if needed
    }

    /**
     * Validates the graph after the configuration phase. This method is invoked after all dependencies have been
     * configured.
     *
     * @param dependencyGraph the dependency graph to validate
     * @param introspector the introspector to use introspection of types
     * @param visited the set of visited nodes
     * @throws ApplicationException when the graph is invalid, or when the validation fails
     */
    default void validateAfterConfiguration(DependencyGraph dependencyGraph, Introspector introspector, Set<GraphNode<DependencyContext<?>>> visited) throws ApplicationException {
        // NOOP, override if needed
    }
}
