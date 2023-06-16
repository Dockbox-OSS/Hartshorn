package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.util.graph.BreadthFirstGraphVisitor;
import org.dockbox.hartshorn.util.graph.GraphException;
import org.dockbox.hartshorn.util.graph.GraphNode;

import java.util.HashSet;
import java.util.Set;

public class DependencyPresenceValidationVisitor extends BreadthFirstGraphVisitor<DependencyContext<?>> {

    private final Set<GraphNode<DependencyContext<?>>> missingDependencies = new HashSet<>();
    private final Set<GraphNode<DependencyContext<?>>> visitedDependencies;

    public DependencyPresenceValidationVisitor(final Set<GraphNode<DependencyContext<?>>> visitedDependencies) {
        this.visitedDependencies = visitedDependencies;
    }

    public Set<GraphNode<DependencyContext<?>>> missingDependencies() {
        return this.missingDependencies;
    }

    @Override
    protected boolean visit(final GraphNode<DependencyContext<?>> node) throws GraphException {
        if (!this.visitedDependencies.contains(node)) {
            this.missingDependencies.add(node);
        }
        return true;
    }
}
