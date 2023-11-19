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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.application.context.DependencyGraph;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.HierarchicalComponentProvider;
import org.dockbox.hartshorn.component.processing.Binds.BindingType;
import org.dockbox.hartshorn.inject.ComponentKeyDependencyDeclarationContext;
import org.dockbox.hartshorn.inject.ComposedProvider;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.inject.DependencyDeclarationContext;
import org.dockbox.hartshorn.inject.DependencyResolutionException;
import org.dockbox.hartshorn.inject.DependencyResolver;
import org.dockbox.hartshorn.inject.Provider;
import org.dockbox.hartshorn.inject.TypeAwareProvider;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.collection.ComponentCollection;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.collections.ArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.graph.Graph;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.graph.MutableContainableGraphNode;
import org.dockbox.hartshorn.util.graph.SimpleGraphNode;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.jetbrains.annotations.NotNull;

public class DependencyGraphBuilder {

    private final HierarchicalComponentProvider hierarchicalComponentProvider;
    private final DependencyResolver resolver;

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

    public DependencyGraph buildDependencyGraph(Iterable<DependencyContext<?>> dependencyContexts) throws DependencyResolutionException {
        Set<DependencyContext<?>> contexts = this.inflateDependencyContexts(dependencyContexts);
        MultiMap<ComponentKey<?>, MutableContainableGraphNode<DependencyContext<?>>> nodes = this.computeNodeMap(contexts);
        DependencyGraph graph = new DependencyGraph();
        // Don't use inflated contexts here, as we want to keep the original context for the graph. If the inflated contexts are relevant,
        // they've already been attached to the component key of the original context.
        this.buildDependencyNodes(dependencyContexts, nodes, graph);
        return graph;
    }

    private void buildDependencyNodes(
        Iterable<DependencyContext<?>> dependencyContexts,
        MultiMap<ComponentKey<?>, MutableContainableGraphNode<DependencyContext<?>>> nodes,
        Graph<DependencyContext<?>> graph
    ) {
        for (DependencyContext<?> dependencyContext : dependencyContexts) {
            this.buildSingleDependencyNode(nodes, graph, dependencyContext);
        }
    }

    protected <T> Set<ComponentKey<? extends T>> lookupHierarchyDeclarations(DependencyContext<T> dependencyContext) {
        ComponentKey<T> componentKey = dependencyContext.componentKey();
        BindingHierarchy<T> hierarchy = this.hierarchicalComponentProvider.hierarchy(componentKey);
        int highestPriority = hierarchy.highestPriority();
        return hierarchy.get(highestPriority).map(provider -> {
            Provider<T> actualProvider = provider;
            if(provider instanceof ComposedProvider<T> composedProvider) {
                actualProvider = composedProvider.provider();
            }
            if(actualProvider instanceof TypeAwareProvider<T> typeAwareProvider) {
                return componentKey.mutable().type(typeAwareProvider.type()).build();
            }
            return null;
        }).stream().collect(Collectors.toSet());
    }

    private Set<DependencyContext<?>> inflateDependencyContexts(Iterable<DependencyContext<?>> dependencyContexts) throws DependencyResolutionException {
        Set<DependencyContext<?>> contexts = new HashSet<>();
        for (DependencyContext<?> dependencyContext : dependencyContexts) {
            contexts.add(dependencyContext);
            Class<?> dependencyType = dependencyContext.componentKey().type();
            Set<DependencyContext<?>> resolvedContexts = this.resolver.resolve(this.getImplementationContexts(dependencyContext)).stream()
                    .map(implementationContext -> {
                        Class<?> implementationType = implementationContext.componentKey().type();
                        if (dependencyType.isAssignableFrom(implementationType)) {
                            return new ImplementationDependencyContext<>(implementationContext, TypeUtils.adjustWildcards(dependencyContext, DependencyContext.class));
                        }
                        return null;
                    }).collect(Collectors.toSet());
            contexts.addAll(resolvedContexts);
        }
        return contexts;
    }

    @NotNull
    private <T> Set<DependencyDeclarationContext<?>> getImplementationContexts(DependencyContext<T> dependencyContext) {
        Set<ComponentKey<? extends T>> implementationKeys = this.lookupHierarchyDeclarations(dependencyContext);
        Introspector introspector = this.resolver.applicationContext().environment().introspector();
        return implementationKeys.stream()
                .map(key -> new ComponentKeyDependencyDeclarationContext<>(introspector, key))
                .collect(Collectors.toSet());
    }

    protected void visitContextForNodeMapping(
            DependencyContext<?> dependencyContext,
            MultiMap<ComponentKey<?>, MutableContainableGraphNode<DependencyContext<?>>> nodes
    ) {
        MutableContainableGraphNode<DependencyContext<?>> node = new SimpleGraphNode<>(dependencyContext);
        if (dependencyContext instanceof ImplementationDependencyContext<?,?> implementationDependencyContext) {
            nodes.put(implementationDependencyContext.declarationContext().componentKey(), node);
        }

        ComponentKey<?> componentKey = dependencyContext.componentKey();
        switch(dependencyContext.type()) {
        case COMPONENT -> nodes.put(componentKey, node);
        case COLLECTION -> {
            ComponentKey<? extends ComponentCollection<?>> collectorComponentKey = componentKey.mutable().collector().build();
            nodes.put(collectorComponentKey, node);
        }
        }
    }

    private void buildSingleDependencyNode(
            MultiMap<ComponentKey<?>, MutableContainableGraphNode<DependencyContext<?>>> nodes,
            Graph<DependencyContext<?>> graph,
            DependencyContext<?> dependencyContext
    ) {
        Collection<MutableContainableGraphNode<DependencyContext<?>>> dependencyContexts = collectDependencyContexts(nodes, dependencyContext);
        graph.addRoots(Set.copyOf(dependencyContexts));

        for(MutableContainableGraphNode<DependencyContext<?>> componentNode : dependencyContexts) {
            DependencyContext<?> componentDependencyContext = componentNode.value();
            for(ComponentKey<?> dependency : componentDependencyContext.dependencies().allValues()) {
                Set<GraphNode<DependencyContext<?>>> dependencyNodes = Set.copyOf(nodes.get(dependency));
                if (dependencyNodes.size() > 1) {
                    boolean collectionsOnly = dependencyNodes.stream().allMatch(node -> node.value().type() == BindingType.COLLECTION);
                    if (!collectionsOnly) {
                        // Priority is ignored for collections, so we can fail fast here.
                        throw new IllegalStateException("Multiple nodes found for dependency " + dependency + " but not all are collections");
                    }
                }
                graph.addRoots(dependencyNodes);
                dependencyContexts.forEach(node -> node.addParents(dependencyNodes));
            }
        }
    }

    @NonNull
    private static Collection<MutableContainableGraphNode<DependencyContext<?>>> collectDependencyContexts(
        MultiMap<ComponentKey<?>, MutableContainableGraphNode<DependencyContext<?>>> nodes, DependencyContext<?> dependencyContext) {
        Collection<MutableContainableGraphNode<DependencyContext<?>>> componentNodes = nodes.get(dependencyContext.componentKey());

        ComponentKey<? extends ComponentCollection<?>> collectionComponentKey = dependencyContext.componentKey().mutable().collector().build();
        Collection<MutableContainableGraphNode<DependencyContext<?>>> collectionComponentNodes = nodes.get(collectionComponentKey);

        return CollectionUtilities.merge(componentNodes, collectionComponentNodes);
    }

    private static boolean containsAllDependencies(DependencyContext<?> dependencyContext, MultiMap<ComponentKey<?>, MutableContainableGraphNode<DependencyContext<?>>> nodes) {
        for (ComponentKey<?> dependency : dependencyContext.dependencies().allValues()) {
            if (!nodes.containsKey(dependency)) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    private MultiMap<ComponentKey<?>, MutableContainableGraphNode<DependencyContext<?>>> computeNodeMap(
            Iterable<DependencyContext<?>> allDependencyContexts
    ) {
        MultiMap<ComponentKey<?>, MutableContainableGraphNode<DependencyContext<?>>> nodes = new ArrayListMultiMap<>();
        for(DependencyContext<?> dependencyContext : allDependencyContexts) {
            this.visitContextForNodeMapping(dependencyContext, nodes);
        }

        return nodes;
    }
}
