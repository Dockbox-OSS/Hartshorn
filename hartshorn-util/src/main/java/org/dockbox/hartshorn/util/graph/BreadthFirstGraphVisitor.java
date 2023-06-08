package org.dockbox.hartshorn.util.graph;

import java.util.HashSet;
import java.util.Set;

public abstract class BreadthFirstGraphVisitor<T> extends AbstractGraphVisitor<T> {

    @Override
    public final void iterate(final Graph<T> graph) {
        final Set<GraphNode<T>> visited = new HashSet<>();

        Set<GraphNode<T>> nodes = graph.roots();
        while (!nodes.isEmpty()) {
            this.beforePathVisited();
            final Set<GraphNode<T>> currentRow = this.filterNodesWithUnresolvedParents(visited, nodes);
            nodes = this.visitRow(visited, currentRow);
            this.afterPathVisited();
        }
    }

    private Set<GraphNode<T>> visitRow(final Set<GraphNode<T>> visited, final Set<GraphNode<T>> currentRow) {
        final Set<GraphNode<T>> nextRow = new HashSet<>();
        for (final GraphNode<T> node : currentRow) {
            if (visited.add(node) && this.visit(node)) {
                nextRow.addAll(node.children());
            }
        }
        return nextRow;
    }

    private Set<GraphNode<T>> filterNodesWithUnresolvedParents(final Set<GraphNode<T>> visited, final Set<GraphNode<T>> nodes) {
        final Set<GraphNode<T>> currentRow = new HashSet<>(nodes);
        // Filter out nodes that have parents that haven't been visited yet, as they can't be visited yet
        // until their parents have been visited first.
        for (final GraphNode<T> node : nodes) {
            if (!visited.containsAll(node.parents())) {
                currentRow.remove(node);
            }
        }
        return currentRow;
    }
}
