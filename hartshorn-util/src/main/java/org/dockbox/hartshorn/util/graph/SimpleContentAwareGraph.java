package org.dockbox.hartshorn.util.graph;

import org.dockbox.hartshorn.util.ApplicationRuntimeException;

import java.util.HashSet;
import java.util.Set;

public class SimpleContentAwareGraph<T> extends SimpleGraph<T> implements ContentAwareGraph<T> {

    private final Set<GraphNode<T>> nodes = new HashSet<>();
    private final GraphIterator<T> visitor = new PassOnlyGraphVisitor<>();

    @Override
    public Set<GraphNode<T>> nodes() {
        return Set.copyOf(this.nodes);
    }

    @Override
    public void addRoot(GraphNode<T> root) {
        try {
            // TODO: #998 This is PoC only, needs to be replaced with a proper implementation
            Graph<T> graph = new SimpleGraph<>();
            graph.addRoot(root);
            Set<GraphNode<T>> set = this.visitor.iterate(graph);
            this.nodes.addAll(set);
        }
        catch (GraphException e) {
            throw new ApplicationRuntimeException(e);
        }
        super.addRoot(root);
    }

    private static class PassOnlyGraphVisitor<T> extends BreadthFirstGraphVisitor<T> {

        @Override
        protected boolean visit(GraphNode<T> node) {
            return true;
        }
    }
}
