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

import java.util.Collection;
import java.util.Set;

import org.dockbox.hartshorn.inject.ApplicationDependencyResolver;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.ConfigurationDependencyVisitor;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.inject.DependencyDeclarationContext;
import org.dockbox.hartshorn.inject.DependencyPresenceValidationVisitor;
import org.dockbox.hartshorn.inject.DependencyResolutionException;
import org.dockbox.hartshorn.inject.DependencyResolver;
import org.dockbox.hartshorn.inject.processing.DependencyGraphBuilder;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.InitializerContext;
import org.dockbox.hartshorn.util.graph.Graph;
import org.dockbox.hartshorn.util.graph.GraphException;
import org.dockbox.hartshorn.util.graph.GraphNode;

public class DependencyGraphInitializer {

    private final DependencyGraphBuilder graphBuilder;
    private final ConfigurationDependencyVisitor dependencyVisitor;
    private final ApplicationContext applicationContext;
    private final DependencyResolver dependencyResolver;

    public DependencyGraphInitializer(InitializerContext<? extends ApplicationContext> initializerContext, Configurer configurer) {
        this.applicationContext = initializerContext.input();
        this.dependencyResolver = configurer.dependencyResolver.initialize(initializerContext);
        this.graphBuilder = configurer.dependencyGraphBuilder.initialize(initializerContext);
        this.dependencyVisitor = configurer.dependencyVisitor.initialize(initializerContext);
    }

    public void initializeDependencyGraph(Collection<DependencyDeclarationContext<?>> containers) throws DependencyResolutionException, GraphException {
        Graph<DependencyContext<?>> dependencyGraph = this.buildDependencyGraph(containers);
        Set<GraphNode<DependencyContext<?>>> visitedDependencies = this.dependencyVisitor.iterate(dependencyGraph);
        this.validateDependencies(dependencyGraph, visitedDependencies);

        this.applicationContext.log().debug("Visited %d dependencies".formatted(visitedDependencies.size()));
    }

    private void validateDependencies(Graph<DependencyContext<?>> dependencyGraph, Set<GraphNode<DependencyContext<?>>> visitedDependencies) throws GraphException {
        DependencyPresenceValidationVisitor validationVisitor = new DependencyPresenceValidationVisitor(visitedDependencies);
        validationVisitor.iterate(dependencyGraph);
        Set<GraphNode<DependencyContext<?>>> missingDependencies = validationVisitor.missingDependencies();
        if (!missingDependencies.isEmpty()) {
            throw new ComponentInitializationException("Failed to resolve dependencies: %s".formatted(missingDependencies));
        }
    }

    private Graph<DependencyContext<?>> buildDependencyGraph(Collection<DependencyDeclarationContext<?>> containers) throws DependencyResolutionException {
        Collection<DependencyContext<?>> dependencyContexts = this.dependencyResolver.resolve(containers, this.applicationContext);
        return this.graphBuilder.buildDependencyGraph(dependencyContexts);
    }

    public static ContextualInitializer<ApplicationContext, DependencyGraphInitializer> create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);
            return new DependencyGraphInitializer(context, configurer);
        };
    }

    public static class Configurer {

        private ContextualInitializer<ApplicationContext, DependencyResolver> dependencyResolver = ApplicationDependencyResolver.create(Customizer.useDefaults());
        private ContextualInitializer<ApplicationContext, DependencyGraphBuilder> dependencyGraphBuilder = ContextualInitializer.of(DependencyGraphBuilder::new);
        private ContextualInitializer<ApplicationContext, ConfigurationDependencyVisitor> dependencyVisitor = ContextualInitializer.of(ConfigurationDependencyVisitor::new);

        public Configurer dependencyResolver(DependencyResolver dependencyResolver) {
            return this.dependencyResolver(ContextualInitializer.of(dependencyResolver));
        }

        public Configurer dependencyResolver(ContextualInitializer<ApplicationContext, DependencyResolver> dependencyResolver) {
            this.dependencyResolver = dependencyResolver;
            return this;
        }

        public Configurer dependencyGraphBuilder(DependencyGraphBuilder dependencyGraphBuilder) {
            return this.dependencyGraphBuilder(ContextualInitializer.of(dependencyGraphBuilder));
        }

        public Configurer dependencyGraphBuilder(ContextualInitializer<ApplicationContext, DependencyGraphBuilder> dependencyGraphBuilder) {
            this.dependencyGraphBuilder = dependencyGraphBuilder;
            return this;
        }

        public Configurer dependencyVisitor(ConfigurationDependencyVisitor dependencyVisitor) {
            return this.dependencyVisitor(ContextualInitializer.of(dependencyVisitor));
        }

        public Configurer dependencyVisitor(ContextualInitializer<ApplicationContext, ConfigurationDependencyVisitor> dependencyVisitor) {
            this.dependencyVisitor = dependencyVisitor;
            return this;
        }

    }
}
