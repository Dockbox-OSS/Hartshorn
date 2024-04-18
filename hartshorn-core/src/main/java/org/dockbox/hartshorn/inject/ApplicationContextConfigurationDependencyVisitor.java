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

import java.util.Set;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.DependencyGraph;
import org.dockbox.hartshorn.component.processing.ComponentProcessor;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.util.graph.BreadthFirstGraphVisitor;
import org.dockbox.hartshorn.util.graph.ContainableGraphNode;
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

    @Override
    public boolean hasVisitedParents(Set<GraphNode<DependencyContext<?>>> visited, Set<GraphNode<DependencyContext<?>>> allNodes, GraphNode<DependencyContext<?>> node) {
        if (BreadthFirstGraphVisitor.super.hasVisitedParents(visited, allNodes, node)) {
            return true;
        }
        // For singletons we only need to know their parents exist, not that they have been visited. This is to allow
        // for circular dependencies in singletons. For prototypes this is not allowed, so those abide by the default
        // implementation.
        if (DependencyGraph.isSingletonNode(node)) {
            if (node instanceof ContainableGraphNode<DependencyContext<?>> containable) {
                return allNodes.containsAll(containable.parents());
            }
            return true;
        }
        return false;
    }
}
