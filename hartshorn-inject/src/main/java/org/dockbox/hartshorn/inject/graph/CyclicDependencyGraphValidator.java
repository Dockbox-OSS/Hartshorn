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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentDiscoveryList;
import org.dockbox.hartshorn.inject.CyclicComponentException;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.inject.TypePathNode;
import org.dockbox.hartshorn.inject.processing.ImplementationDependencyContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.graph.ContainableGraphNode;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.introspect.view.View;

/**
 * A validator that can be used to detect cyclic dependencies in a {@link DependencyGraph}. A cyclic dependency is a
 * dependency that is required by a component, but is also a dependency of that same component. This is a problem,
 * as it would require the component to be instantiated before it can be instantiated.
 *
 * <p>This validator will detect cyclic dependencies by traversing the graph, and checking if any of the dependencies
 * of a component are also dependencies of that same component. If that is the case, a {@link CyclicComponentException}
 * is thrown. The exception contains a {@link ComponentDiscoveryList} that describes the cyclic dependency.
 *
 * <p>Dependencies are checked to any depth. This means that if component A depends on component B, and component B
 * depends on component C, and component C depends on component A, a cyclic dependency is detected. This is true
 * even if component A does not directly depend on component C.
 *
 * @see CyclicComponentException
 * @see ComponentDiscoveryList
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class CyclicDependencyGraphValidator implements DependencyGraphValidator {

    @Override
    public void validateBeforeConfiguration(DependencyGraph dependencyGraph, Introspector introspector) throws ApplicationException {
        Set<GraphNode<DependencyContext<?>>> nodes = dependencyGraph.nodes();
        for (GraphNode<DependencyContext<?>> node : nodes) {
            if (node.isLeaf()) {
                continue;
            }
            if (node instanceof ContainableGraphNode<DependencyContext<?>> contextContainableGraphNode
                && contextContainableGraphNode.isRoot()) {
                continue;
            }
            List<GraphNode<DependencyContext<?>>> graphNodes = this.checkNodeNotCyclicRecursive(node, new ArrayList<>());
            if (!graphNodes.isEmpty()) {
                ComponentDiscoveryList discoveryList = this.createDiscoveryList(graphNodes, applicationContext);
                throw new CyclicComponentException(discoveryList, node.value().origin());
            }
        }
    }

    /**
     * Checks if a node is part of a cyclic dependency. This will ignore any nodes that do not need immediate
     * resolution, as they can be lazily initialized and therefore do not pose a problem.
     *
     * @param node the node to check
     * @param knownNodes the nodes that have already been checked
     *
     * @return a list of nodes that are part of a cyclic dependency, or an empty list if no cyclic dependency was found
     */
    public List<GraphNode<DependencyContext<?>>> checkNodeNotCyclicRecursive(GraphNode<DependencyContext<?>> node, List<GraphNode<DependencyContext<?>>> knownNodes) {
        if (knownNodes.contains(node)) {
            return List.of(node);
        }

        // Defaults to true, as we should assume that the node needs immediate resolution unless proven otherwise.
        boolean needsImmediateResolution = true;
        if (node instanceof ContainableGraphNode<DependencyContext<?>> containableGraphNode) {
            ComponentKey<?> dependencyCandidate = node.value().componentKey();
            // If none of the parents need immediate resolution, then we can cut potential cyclic graphs short.
            needsImmediateResolution = containableGraphNode.children().stream()
                    .anyMatch(parent -> parent.value().needsImmediateResolution(dependencyCandidate));
        }

        // If the node doesn't need immediate resolution, then we can skip it. Note that this does not affect potential grandchild
        // dependencies, as this validator goes over all nodes in the graph, and not just the roots.
        if (!needsImmediateResolution) {
            return List.of();
        }

        knownNodes.add(node);

        for(GraphNode<DependencyContext<?>> child : node.children()) {
            List<GraphNode<DependencyContext<?>>> graphNodes = this.checkNodeNotCyclicRecursive(child, knownNodes);
            if(!graphNodes.isEmpty()) {
                List<GraphNode<DependencyContext<?>>> path = new ArrayList<>();
                path.add(node);
                path.addAll(graphNodes);
                return path;
            }
        }
        return List.of();
    }

    /**
     * Creates a {@link ComponentDiscoveryList} from a path of {@link GraphNode}s. The path is expected to be a path
     * of nodes that are part of a cyclic dependency. The path is traversed, and the {@link ComponentKey}s of the
     * nodes are added to the discovery list.
     *
     * @param path the path of nodes
     * @param introspector the introspector to use for introspection
     *
     * @return a discovery list that describes the cyclic dependency
     *
     * @implNote The use of {@link ImplementationDependencyContext}s is supported, and will result in appropriate
     *           {@link TypePathNode}s being added to the discovery list.
     */
    public ComponentDiscoveryList createDiscoveryList(List<GraphNode<DependencyContext<?>>> path, Introspector introspector) {
        ComponentDiscoveryList discoveryList = new ComponentDiscoveryList();
        for (GraphNode<DependencyContext<?>> node : path) {
            DependencyContext<?> dependencyContext = node.value();
            if (dependencyContext instanceof ImplementationDependencyContext<?,?> implementationDependencyContext) {
                ComponentKey<?> componentKey = implementationDependencyContext.declarationContext().componentKey();
                TypePathNode<?> typePathNode = this.createTypePathNode(componentKey, implementationDependencyContext.origin(), introspector);
                TypeView<?> actualType = introspector.introspect(implementationDependencyContext.implementationContext().componentKey().parameterizedType());
                discoveryList.add(typePathNode, actualType);
            }
            else {
                TypePathNode<?> pathNode = this.createTypePathNode(dependencyContext.componentKey(), dependencyContext.origin(), introspector);
                discoveryList.add(pathNode);
            }
        }
        return discoveryList;
    }

    private <T> TypePathNode<T> createTypePathNode(ComponentKey<T> componentKey, View origin, Introspector introspector) {
        TypeView<T> view = introspector.introspect(componentKey.type());
        return new TypePathNode<>(view, componentKey, origin);
    }
}
