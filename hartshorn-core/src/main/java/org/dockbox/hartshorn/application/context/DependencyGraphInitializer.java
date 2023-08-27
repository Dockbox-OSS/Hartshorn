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

import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.inject.ApplicationDependencyResolver;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.ConfigurationDependencyVisitor;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.inject.DependencyDeclarationContext;
import org.dockbox.hartshorn.inject.DependencyPresenceValidationVisitor;
import org.dockbox.hartshorn.inject.DependencyResolutionException;
import org.dockbox.hartshorn.inject.DependencyResolver;
import org.dockbox.hartshorn.inject.processing.DependencyGraphBuilder;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.LazyInitializer;
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
    private final ConditionMatcher conditionMatcher;

    public DependencyGraphInitializer(final ApplicationContext applicationContext, final Configurer configurer) {
        this.applicationContext = applicationContext;
        this.conditionMatcher = configurer.conditionMatcher.initialize(applicationContext);
        this.dependencyResolver = configurer.dependencyResolver.initialize(this.conditionMatcher);
        this.graphBuilder = configurer.dependencyGraphBuilder.initialize(applicationContext);
        this.dependencyVisitor = configurer.dependencyVisitor.initialize(applicationContext);
    }

    public void initializeDependencyGraph(final Collection<DependencyDeclarationContext<?>> containers) throws DependencyResolutionException, GraphException {
        final Graph<DependencyContext<?>> dependencyGraph = this.buildDependencyGraph(containers);
        final Set<GraphNode<DependencyContext<?>>> visitedDependencies = this.dependencyVisitor.iterate(dependencyGraph);
        this.validateDependencies(dependencyGraph, visitedDependencies);

        this.applicationContext.log().debug("Visited %d dependencies".formatted(visitedDependencies.size()));
    }

    private void validateDependencies(final Graph<DependencyContext<?>> dependencyGraph, final Set<GraphNode<DependencyContext<?>>> visitedDependencies) throws GraphException {
        final DependencyPresenceValidationVisitor validationVisitor = new DependencyPresenceValidationVisitor(visitedDependencies);
        validationVisitor.iterate(dependencyGraph);
        final Set<GraphNode<DependencyContext<?>>> missingDependencies = validationVisitor.missingDependencies();
        if (!missingDependencies.isEmpty()) {
            throw new ComponentInitializationException("Failed to resolve dependencies: %s".formatted(missingDependencies));
        }
    }

    private Graph<DependencyContext<?>> buildDependencyGraph(final Collection<DependencyDeclarationContext<?>> containers) throws DependencyResolutionException {
        final Collection<DependencyContext<?>> dependencyContexts = this.dependencyResolver.resolve(containers, this.applicationContext);
        return this.graphBuilder.buildDependencyGraph(dependencyContexts);
    }

    public static LazyInitializer<ApplicationContext, DependencyGraphInitializer> create(final Customizer<Configurer> customizer) {
        return context -> {
            final Configurer configurer = new Configurer();
            customizer.configure(configurer);
            return new DependencyGraphInitializer(context, configurer);
        };
    }

    public static class Configurer {

        private LazyInitializer<ApplicationContext, ConditionMatcher> conditionMatcher = ConditionMatcher::new;
        private LazyInitializer<ConditionMatcher, DependencyResolver> dependencyResolver = ApplicationDependencyResolver.create(Customizer.useDefaults());
        private LazyInitializer<ApplicationContext, DependencyGraphBuilder> dependencyGraphBuilder = LazyInitializer.of(DependencyGraphBuilder::new);
        private LazyInitializer<ApplicationContext, ConfigurationDependencyVisitor> dependencyVisitor = ConfigurationDependencyVisitor::new;

        public Configurer conditionMatcher(final ConditionMatcher conditionMatcher) {
            return this.conditionMatcher(LazyInitializer.of(conditionMatcher));
        }

        public Configurer conditionMatcher(final LazyInitializer<ApplicationContext, ConditionMatcher> conditionMatcher) {
            this.conditionMatcher = conditionMatcher;
            return this;
        }

        public Configurer dependencyResolver(final DependencyResolver dependencyResolver) {
            return this.dependencyResolver(LazyInitializer.of(dependencyResolver));
        }

        public Configurer dependencyResolver(final LazyInitializer<ConditionMatcher, DependencyResolver> dependencyResolver) {
            this.dependencyResolver = dependencyResolver;
            return this;
        }

        public Configurer dependencyGraphBuilder(final DependencyGraphBuilder dependencyGraphBuilder) {
            return this.dependencyGraphBuilder(LazyInitializer.of(dependencyGraphBuilder));
        }

        public Configurer dependencyGraphBuilder(final LazyInitializer<ApplicationContext, DependencyGraphBuilder> dependencyGraphBuilder) {
            this.dependencyGraphBuilder = dependencyGraphBuilder;
            return this;
        }

        public Configurer dependencyVisitor(final ConfigurationDependencyVisitor dependencyVisitor) {
            return this.dependencyVisitor(LazyInitializer.of(dependencyVisitor));
        }

        public Configurer dependencyVisitor(final LazyInitializer<ApplicationContext, ConfigurationDependencyVisitor> dependencyVisitor) {
            this.dependencyVisitor = dependencyVisitor;
            return this;
        }

    }
}
