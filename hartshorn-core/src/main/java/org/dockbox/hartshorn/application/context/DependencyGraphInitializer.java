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

package org.dockbox.hartshorn.application.context;

import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.ConfigurationDependencyVisitor;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.inject.DependencyPresenceValidationVisitor;
import org.dockbox.hartshorn.inject.DependencyResolutionException;
import org.dockbox.hartshorn.inject.DependencyResolver;
import org.dockbox.hartshorn.inject.processing.DependencyGraphBuilder;
import org.dockbox.hartshorn.util.graph.Graph;
import org.dockbox.hartshorn.util.graph.GraphException;
import org.dockbox.hartshorn.util.graph.GraphNode;

import java.util.Collection;
import java.util.Set;

public class DependencyGraphInitializer {

    private final DependencyGraphBuilder graphBuilder;
    private final ConfigurationDependencyVisitor dependencyVisitor;
    private final ApplicationContext applicationContext;
    private final DependencyResolver dependencyResolver;

    public DependencyGraphInitializer(final ApplicationContext applicationContext, final DependencyResolver dependencyResolver) {
        this.dependencyResolver = dependencyResolver;
        this.graphBuilder = new DependencyGraphBuilder();
        this.dependencyVisitor = new ConfigurationDependencyVisitor(applicationContext);
        this.applicationContext = applicationContext;
    }

    public void initializeDependencyGraph(final Collection<ComponentContainer> containers) throws DependencyResolutionException, GraphException {
        final Collection<DependencyContext<?>> dependencyContexts = this.dependencyResolver.resolve(containers, this.applicationContext);
        final Graph<DependencyContext<?>> dependencyGraph = this.graphBuilder.buildDependencyGraph(dependencyContexts);

        final Set<GraphNode<DependencyContext<?>>> visitedDependencies = this.dependencyVisitor.iterate(dependencyGraph);

        final DependencyPresenceValidationVisitor validationVisitor = new DependencyPresenceValidationVisitor(visitedDependencies);
        validationVisitor.iterate(dependencyGraph);
        final Set<GraphNode<DependencyContext<?>>> missingDependencies = validationVisitor.missingDependencies();
        if (!missingDependencies.isEmpty()) {
            throw new ComponentInitializationException("Failed to resolve dependencies: %s".formatted(missingDependencies));
        }

        this.applicationContext.log().debug("Visited %d dependencies".formatted(visitedDependencies.size()));
    }
}
