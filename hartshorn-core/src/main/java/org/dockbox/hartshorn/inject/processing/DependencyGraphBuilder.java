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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.application.context.DependencyGraph;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.HierarchicalComponentProvider;
import org.dockbox.hartshorn.inject.ComposedProvider;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.inject.DependencyResolver;
import org.dockbox.hartshorn.inject.Provider;
import org.dockbox.hartshorn.inject.TypeAwareProvider;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.util.graph.Graph;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.graph.MutableContainableGraphNode;
import org.dockbox.hartshorn.util.graph.SimpleGraphNode;

public class DependencyGraphBuilder {

    private final DependencyResolver resolver;
    private final HierarchicalComponentProvider hierarchicalComponentProvider;

    protected DependencyGraphBuilder(DependencyResolver resolver, HierarchicalComponentProvider hierarchicalComponentProvider) {
        this.resolver = resolver;
        this.hierarchicalComponentProvider = hierarchicalComponentProvider;
    }

    public static DependencyGraphBuilder create(DependencyResolver resolver) {
        return new DependencyGraphBuilder(resolver, resolver.applicationContext());
    }

    public static DependencyGraphBuilder create(DependencyResolver resolver, HierarchicalComponentProvider hierarchicalComponentProvider) {
        return new DependencyGraphBuilder(resolver, hierarchicalComponentProvider);
    }

    public DependencyGraph buildDependencyGraph(Collection<DependencyContext<?>> dependencyContexts) {
        Map<ComponentKey<?>, MutableContainableGraphNode<DependencyContext<?>>> nodes = this.createNodeMap(
            this.hierarchicalComponentProvider,
                dependencyContexts
        );
        DependencyGraph graph = new DependencyGraph();
        for (DependencyContext<?> dependencyContext : dependencyContexts) {
            this.buildSingleDependencyNode(nodes, graph, dependencyContext);
        }
        return graph;
    }

    protected void buildSingleDependencyNode(
            Map<ComponentKey<?>, MutableContainableGraphNode<DependencyContext<?>>> nodes,
            Graph<DependencyContext<?>> graph,
            DependencyContext<?> dependencyContext
    ) {
        MutableContainableGraphNode<DependencyContext<?>> node = nodes.get(dependencyContext.componentKey());
        graph.addRoot(node);

        Set<ComponentKey<?>> implementationKeys = this.lookupHierarchyDeclarations(dependencyContext);
        // TODO: Use resolver to resolve additional dependencies here

        for (ComponentKey<?> dependency : dependencyContext.dependencies()) {
            if (!nodes.containsKey(dependency)) {
                // provided by existing bindings, or will be dynamically created on request. It is not up to the
                // graph builder to verify that the dependency is (or will be) available.
                continue;
            }

            GraphNode<DependencyContext<?>> dependencyNode = nodes.get(dependency);
            graph.addRoot(dependencyNode);
            node.addParent(dependencyNode);
        }
    }

    public Map<ComponentKey<?>, MutableContainableGraphNode<DependencyContext<?>>> createNodeMap(
            HierarchicalComponentProvider hierarchicalComponentProvider,
            Iterable<DependencyContext<?>> dependencyContexts
    ) {
        Map<ComponentKey<?>, MutableContainableGraphNode<DependencyContext<?>>> nodes = new HashMap<>();
        for (DependencyContext<?> dependencyContext : dependencyContexts) {
            this.visitContextForNodeMapping(hierarchicalComponentProvider, dependencyContext, nodes);
        }
        return nodes;
    }

    protected void visitContextForNodeMapping(
            HierarchicalComponentProvider hierarchicalComponentProvider,
            DependencyContext<?> dependencyContext,
            Map<ComponentKey<?>, MutableContainableGraphNode<DependencyContext<?>>> nodes
    ) {
        this.visitForDirectBindingKey(dependencyContext, nodes);
    }

    protected void visitForDirectBindingKey(
            DependencyContext<?> dependencyContext,
            Map<ComponentKey<?>, MutableContainableGraphNode<DependencyContext<?>>> nodes
    ) {
        MutableContainableGraphNode<DependencyContext<?>> node = new SimpleGraphNode<>(dependencyContext);
        nodes.put(dependencyContext.componentKey(), node);
    }

    protected <T> Set<ComponentKey<?>> lookupHierarchyDeclarations(DependencyContext<T> dependencyContext) {
        ComponentKey<T> componentKey = dependencyContext.componentKey();
        BindingHierarchy<T> hierarchy = this.hierarchicalComponentProvider.hierarchy(componentKey);
        return hierarchy.highestPriority().map(provider -> {
            Provider<T> actualProvider = provider;
            if (provider instanceof ComposedProvider<T> composedProvider) {
                actualProvider = composedProvider.provider();
            }
            if (actualProvider instanceof TypeAwareProvider<T> typeAwareProvider) {
                return componentKey.mutable().type(typeAwareProvider.type()).build();
            }
            return null;
        }).stream().collect(Collectors.toSet());
    }
}
