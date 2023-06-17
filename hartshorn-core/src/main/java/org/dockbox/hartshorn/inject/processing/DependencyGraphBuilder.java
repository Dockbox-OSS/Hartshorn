/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.graph.Graph;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.graph.SimpleGraph;
import org.dockbox.hartshorn.util.graph.SimpleGraphNode;

import java.util.HashMap;
import java.util.Map;

public class DependencyGraphBuilder {

    public Graph<DependencyContext<?>> buildDependencyGraph(final Iterable<DependencyContext<?>> providerContexts) {
        final Map<ComponentKey<?>, GraphNode<DependencyContext<?>>> nodes = this.createNodeMap(providerContexts);
        final Graph<DependencyContext<?>> graph = new SimpleGraph<>();

        for (final DependencyContext<?> providerContext : providerContexts) {
            final GraphNode<DependencyContext<?>> node = nodes.get(providerContext.componentKey());
            graph.addRoot(node);

            for (final ComponentKey<?> dependency : providerContext.dependencies()) {
                if (!nodes.containsKey(dependency)) {
                    // Don't consider this dependency if it's not in the context. This is likely a dependency that is
                    // provided by existing bindings, or will be dynamically created on request. It is not up to the
                    // graph builder to verify that the dependency is (or will be) available.
                    continue;
                }

                final GraphNode<DependencyContext<?>> dependencyNode = nodes.get(dependency);
                graph.addRoot(dependencyNode);
                node.addParent(dependencyNode);
            }
        }
        return graph;
    }

    public Map<ComponentKey<?>, GraphNode<DependencyContext<?>>> createNodeMap(final Iterable<DependencyContext<?>> providerContexts) {
        final Map<ComponentKey<?>, GraphNode<DependencyContext<?>>> nodes = new HashMap<>();
        for (final DependencyContext<?> providerContext : providerContexts) {
            final GraphNode<DependencyContext<?>> node = new SimpleGraphNode<>(providerContext);
            nodes.put(providerContext.componentKey(), node);
        }
        return nodes;
    }
}
