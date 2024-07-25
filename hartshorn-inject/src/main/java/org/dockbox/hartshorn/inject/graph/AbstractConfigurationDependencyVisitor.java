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

import java.util.HashSet;
import java.util.Set;

import org.dockbox.hartshorn.inject.graph.declaration.DependencyContext;
import org.dockbox.hartshorn.inject.provider.LifecycleType;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.graph.BreadthFirstGraphVisitor;
import org.dockbox.hartshorn.util.graph.ContentAwareGraph;
import org.dockbox.hartshorn.util.graph.Graph;
import org.dockbox.hartshorn.util.graph.GraphException;
import org.dockbox.hartshorn.util.graph.GraphNode;

/**
 * Simple implementation of {@link ConfigurationDependencyVisitor} that provides a default implementation for
 * walking the graph of dependencies and registering them. Note that the actual registration of dependencies
 * is delegated to the {@link #registerProvider(DependencyContext)} and {@link #doAfterRegister(DependencyContext)}
 * methods, which should be implemented by the extending class.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public abstract class AbstractConfigurationDependencyVisitor implements BreadthFirstGraphVisitor<DependencyContext<?>>, ConfigurationDependencyVisitor {

    @Override
    public Set<GraphNode<DependencyContext<?>>> iterate(Graph<DependencyContext<?>> graph) throws GraphException {
        Set<GraphNode<DependencyContext<?>>> iterated = BreadthFirstGraphVisitor.super.iterate(graph);
        if (graph instanceof ContentAwareGraph<DependencyContext<?>> contentAwareGraph) {
            Set<GraphNode<DependencyContext<?>>> nodes = contentAwareGraph.nodes();
            Set<GraphNode<DependencyContext<?>>> danglingNodes = CollectionUtilities.difference(nodes, iterated);
            Set<GraphNode<DependencyContext<?>>> iteratedDanglingNodes = this.tryIterateDanglingNodes(danglingNodes);
            iterated.addAll(iteratedDanglingNodes);
        }
        return iterated;
    }

    private Set<GraphNode<DependencyContext<?>>> tryIterateDanglingNodes(Set<GraphNode<DependencyContext<?>>> danglingNodes) throws GraphException {
        Set<GraphNode<DependencyContext<?>>> iterated = new HashSet<>();
        for (GraphNode<DependencyContext<?>> danglingNode : danglingNodes) {
            DependencyContext<?> context = danglingNode.value();
            if (context.lifecycleType() == LifecycleType.SINGLETON) {
                if (this.visit(danglingNode)) {
                    iterated.add(danglingNode);
                }
                else {
                    throw new GraphException("Failed to register singleton dependency: " + context.componentKey());
                }
            }
            else {
                throw new GraphException("Dangling prototype node found: " + context.componentKey());
            }
        }
        return iterated;
    }

    @Override
    public boolean visit(GraphNode<DependencyContext<?>> node) throws GraphException {
        DependencyContext<?> dependencyContext = node.value();
        try {
            this.registerProvider(dependencyContext);
            this.doAfterRegister(dependencyContext);
            return true;
        }
        catch(ComponentConfigurationException e) {
            throw new GraphException(e);
        }
    }
}
