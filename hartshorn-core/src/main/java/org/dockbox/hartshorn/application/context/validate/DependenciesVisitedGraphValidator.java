package org.dockbox.hartshorn.application.context.validate;

import org.dockbox.hartshorn.application.context.DependencyGraph;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.inject.DependencyPresenceValidationVisitor;
import org.dockbox.hartshorn.util.graph.GraphException;
import org.dockbox.hartshorn.util.graph.GraphNode;

import java.util.Set;

public class DependenciesVisitedGraphValidator implements DependencyGraphValidator {

    @Override
    public void validateAfterConfiguration(DependencyGraph dependencyGraph, Set<GraphNode<DependencyContext<?>>> visited) throws GraphException {
        DependencyPresenceValidationVisitor validationVisitor = new DependencyPresenceValidationVisitor(visited);
        validationVisitor.iterate(dependencyGraph);
        Set<GraphNode<DependencyContext<?>>> missingDependencies = validationVisitor.missingDependencies();
        if (!missingDependencies.isEmpty()) {
            throw new ComponentInitializationException("Failed to resolve dependencies: %s".formatted(missingDependencies));
        }
    }
}
