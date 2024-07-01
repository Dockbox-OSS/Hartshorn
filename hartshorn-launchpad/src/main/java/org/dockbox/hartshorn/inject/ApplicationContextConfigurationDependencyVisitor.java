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

package org.dockbox.hartshorn.inject;

import java.util.HashSet;
import java.util.Set;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.inject.processing.ComponentProcessor;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.provider.LifecycleType;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.graph.BreadthFirstGraphVisitor;
import org.dockbox.hartshorn.util.graph.ContentAwareGraph;
import org.dockbox.hartshorn.util.graph.Graph;
import org.dockbox.hartshorn.util.graph.GraphException;
import org.dockbox.hartshorn.util.graph.GraphNode;

/**
 * Simple implementation of {@link ConfigurationDependencyVisitor} that uses the {@link DependencyContext#configure(BindingFunction)
 * dependency context's default configuration method} to register contexts with the {@link ApplicationContext}. Additionally, if the
 * component that is registered is a {@link ComponentProcessor}, it is also registered with the {@link ApplicationContext} for later
 * use.
 *
 * @param applicationContext the application context to register the contexts with
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public record ApplicationContextConfigurationDependencyVisitor(
        ApplicationContext applicationContext
) implements BreadthFirstGraphVisitor<DependencyContext<?>>, ConfigurationDependencyVisitor {

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

    @Override
    public <T> void registerProvider(DependencyContext<T> dependencyContext) throws ComponentConfigurationException {
        BindingFunction<T> function = this.applicationContext.bind(dependencyContext.componentKey());
        dependencyContext.configure(function);
    }

    @Override
    public void doAfterRegister(DependencyContext<?> dependencyContext) {
        if(ComponentProcessor.class.isAssignableFrom(dependencyContext.componentKey().type())) {
            ComponentProcessor processor = (ComponentProcessor) this.applicationContext.get(dependencyContext.componentKey());
            this.applicationContext.add(processor);
        }
    }
}
