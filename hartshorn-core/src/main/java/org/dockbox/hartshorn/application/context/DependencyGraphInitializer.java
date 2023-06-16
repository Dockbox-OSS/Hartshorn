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
