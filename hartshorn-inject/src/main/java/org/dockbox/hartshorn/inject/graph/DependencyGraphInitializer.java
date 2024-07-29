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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.graph.declaration.DependencyContext;
import org.dockbox.hartshorn.inject.graph.declaration.DependencyDeclarationContext;
import org.dockbox.hartshorn.inject.graph.support.CyclicDependencyGraphValidator;
import org.dockbox.hartshorn.inject.graph.support.DependenciesVisitedGraphValidator;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.SingleElementContext;
import org.dockbox.hartshorn.util.StreamableConfigurer;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The dependency graph initializer is responsible for initializing the dependency graph. It does so by first
 * resolving all dependency declarations, and then building a graph from the resolved dependencies. The graph
 * is then validated before and after the configuration phase.
 *
 * @see DependencyGraph
 * @see DependencyDeclarationContext
 * @see DependencyContext
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public final class DependencyGraphInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(DependencyGraphInitializer.class);

    private final DependencyGraphBuilder graphBuilder;
    private final ConfigurationDependencyVisitor dependencyVisitor;
    private final Introspector introspector;
    private final DependencyResolver dependencyResolver;
    private final DependencyGraphValidator graphValidator;

    private DependencyGraphInitializer(SingleElementContext<? extends InjectionCapableApplication> initializerContext, Configurer configurer) {
        initializerContext.firstContext(InjectionCapableApplication.class).onEmpty(() -> {
            initializerContext.addContext(initializerContext.input());
        });
        this.introspector = initializerContext.input().environment().introspector();
        List<DependencyResolver> resolvers = configurer.dependencyResolvers.initialize(initializerContext);
        this.dependencyResolver = new CompositeDependencyResolver(Set.copyOf(resolvers));
        this.graphBuilder = configurer.dependencyGraphBuilder.initialize(initializerContext.transform(this.dependencyResolver));
        this.dependencyVisitor = configurer.dependencyVisitor.initialize(initializerContext);
        this.graphValidator = new CompositeDependencyGraphValidator(configurer.graphValidator.initialize(initializerContext));
    }

    /**
     * Initializes the dependency graph. This method will first resolve all dependency declarations, and then
     * build and validate the dependency graph. The graph is not returned, as it should not be used directly for
     * anything other than validation.
     *
     * @param containers the dependency declarations
     * @return the initialized dependency graph
     * @throws ApplicationException when the graph is invalid, or when the validation fails
     */
    public DependencyGraph initializeDependencyGraph(Collection<DependencyDeclarationContext<?>> containers) throws ApplicationException {
        DependencyGraph dependencyGraph = this.buildDependencyGraph(containers);
        this.graphValidator.validateBeforeConfiguration(dependencyGraph, this.introspector);

        Set<GraphNode<DependencyContext<?>>> visitedDependencies = this.dependencyVisitor.iterate(dependencyGraph);
        this.graphValidator.validateAfterConfiguration(dependencyGraph, this.introspector, visitedDependencies);

        LOG.debug("Validated %d dependencies".formatted(visitedDependencies.size()));
        return dependencyGraph;
    }

    private DependencyGraph buildDependencyGraph(Collection<DependencyDeclarationContext<?>> containers) throws DependencyResolutionException {
        Collection<DependencyContext<?>> dependencyContexts = this.dependencyResolver.resolve(containers);
        return this.graphBuilder.buildDependencyGraph(dependencyContexts);
    }

    /**
     * Creates a new {@link DependencyGraphInitializer} which may be customized using the given {@link Customizer}.
     *
     * @param customizer the customizer
     * @return the new {@link DependencyGraphInitializer}
     */
    public static ContextualInitializer<InjectionCapableApplication, DependencyGraphInitializer> create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);
            return new DependencyGraphInitializer(context, configurer);
        };
    }

    /**
     * Configuration class for the {@link DependencyGraphInitializer}. This class is used to configure the
     * {@link DependencyGraphInitializer} before it is created.
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public static class Configurer {

        private final LazyStreamableConfigurer<InjectionCapableApplication, DependencyResolver> dependencyResolvers = LazyStreamableConfigurer.empty();

        private ContextualInitializer<DependencyResolver, DependencyGraphBuilder> dependencyGraphBuilder = DependencyGraphBuilder.create();
        private ContextualInitializer<InjectionCapableApplication, ConfigurationDependencyVisitor> dependencyVisitor = ContextualInitializer.of(SkipConfigurationDependencyVisitor::new);
        private final LazyStreamableConfigurer<InjectionCapableApplication, DependencyGraphValidator> graphValidator = LazyStreamableConfigurer.of(Set.of(
            new DependenciesVisitedGraphValidator(),
            new CyclicDependencyGraphValidator()
        ));

        /**
         * Configures the dependency resolver to use the given {@link DependencyResolver}.
         *
         * @param dependencyResolver the dependency resolver
         * @return the current instance
         */
        public Configurer dependencyResolver(DependencyResolver dependencyResolver) {
            return this.dependencyResolver(ContextualInitializer.of(dependencyResolver));
        }

        /**
         * Configures the dependency resolver to use the given {@link ContextualInitializer} to create a {@link DependencyResolver}.
         *
         * @param dependencyResolver the dependency resolver initializer
         * @return the current instance
         */
        public Configurer dependencyResolver(ContextualInitializer<InjectionCapableApplication, DependencyResolver> dependencyResolver) {
            return this.dependencyResolvers(resolvers -> resolvers.add(dependencyResolver));
        }

        /**
         * Configures the dependency resolvers to use the given {@link Customizer}.
         *
         * @param customizer the customizer
         * @return the current instance
         */
        public Configurer dependencyResolvers(Customizer<StreamableConfigurer<InjectionCapableApplication, DependencyResolver>> customizer) {
            this.dependencyResolvers.customizer(customizer);
            return this;
        }

        /**
         * Configures the dependency graph builder to use the given {@link DependencyGraphBuilder}.
         *
         * @param dependencyGraphBuilder the dependency graph builder
         * @return the current instance
         */
        public Configurer dependencyGraphBuilder(DependencyGraphBuilder dependencyGraphBuilder) {
            return this.dependencyGraphBuilder(ContextualInitializer.of(dependencyGraphBuilder));
        }

        /**
         * Configures the dependency graph builder to use the given {@link ContextualInitializer} to create a {@link DependencyGraphBuilder}.
         *
         * @param dependencyGraphBuilder the dependency graph builder initializer
         * @return the current instance
         */
        public Configurer dependencyGraphBuilder(ContextualInitializer<DependencyResolver, DependencyGraphBuilder> dependencyGraphBuilder) {
            this.dependencyGraphBuilder = dependencyGraphBuilder;
            return this;
        }

        /**
         * Configures the dependency visitor to use the given {@link ConfigurationDependencyVisitor}.
         *
         * @param dependencyVisitor the dependency visitor
         * @return the current instance
         */
        public Configurer dependencyVisitor(ConfigurationDependencyVisitor dependencyVisitor) {
            return this.dependencyVisitor(ContextualInitializer.of(dependencyVisitor));
        }

        /**
         * Configures the dependency visitor to use the given {@link ContextualInitializer} to create a {@link ConfigurationDependencyVisitor}.
         *
         * @param dependencyVisitor the dependency visitor initializer
         * @return the current instance
         */
        public Configurer dependencyVisitor(ContextualInitializer<InjectionCapableApplication, ConfigurationDependencyVisitor> dependencyVisitor) {
            this.dependencyVisitor = dependencyVisitor;
            return this;
        }

        /**
         * Configures the graph validator to use the given {@link DependencyGraphValidator}.
         *
         * @param graphValidator the graph validator
         * @return the current instance
         */
        public Configurer graphValidator(Customizer<StreamableConfigurer<InjectionCapableApplication, DependencyGraphValidator>> graphValidator) {
            this.graphValidator.customizer(graphValidator);
            return this;
        }
    }
}
