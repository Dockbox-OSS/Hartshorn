package org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.graph.Graph;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.graph.SimpleGraph;
import org.dockbox.hartshorn.util.graph.SimpleGraphNode;

import java.util.HashMap;
import java.util.Map;

public class DependencyGraphBuilder {

    public Graph<ProviderContext> buildDependencyGraph(final Iterable<ProviderContext> providerContexts) {
        final Map<ComponentKey<?>, GraphNode<ProviderContext>> nodes = this.createNodeMap(providerContexts);
        final Graph<ProviderContext> graph = new SimpleGraph<>();

        for (final ProviderContext providerContext : providerContexts) {
            final GraphNode<ProviderContext> node = nodes.get(providerContext.key());
            graph.addRoot(node);

            for (final ComponentKey<?> dependency : providerContext.dependencies()) {
                if (!nodes.containsKey(dependency)) {
                    // Don't consider this dependency if it's not in the context. This is likely a dependency that is
                    // provided by existing bindings, or will be dynamically created on request. It is not up to the
                    // graph builder to verify that the dependency is (or will be) available.
                    continue;
                }

                final GraphNode<ProviderContext> dependencyNode = nodes.get(dependency);
                graph.addRoot(dependencyNode);
                node.addParent(dependencyNode);
            }
        }
        return graph;
    }

    public Map<ComponentKey<?>, GraphNode<ProviderContext>> createNodeMap(final Iterable<ProviderContext> providerContexts) {
        final Map<ComponentKey<?>, GraphNode<ProviderContext>> nodes = new HashMap<>();
        for (final ProviderContext providerContext : providerContexts) {
            final GraphNode<ProviderContext> node = new SimpleGraphNode<>(providerContext);
            nodes.put(providerContext.key(), node);
        }
        return nodes;
    }
}
