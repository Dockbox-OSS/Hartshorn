package org.dockbox.hartshorn.util.graph;

import org.dockbox.hartshorn.util.ApplicationRuntimeException;

import java.util.HashSet;
import java.util.Set;

public class SimpleContentAwareGraph<T> extends SimpleGraph<T> implements ContentAwareGraph<T>, BreadthFirstGraphVisitor<T> {

    private final Set<GraphNode<T>> nodes = new HashSet<>();

    @Override
    public Set<GraphNode<T>> nodes() {
        return Set.copyOf(this.nodes);
    }

    @Override
    public void addRoot(GraphNode<T> root) {
        Set<GraphNode<T>> collected = new HashSet<>();
        try {
            this.visitSingle(collected, root);
            this.nodes.addAll(collected);
        }
        catch (GraphException e) {
            throw new ApplicationRuntimeException(e);
        }
        super.addRoot(root);
    }

    @Override
    public Set<GraphNode<T>> iterate(Graph<T> graph) throws GraphException {
        throw new UnsupportedOperationException("Visiting is only for internal tracking.");
    }

    @Override
    public boolean visit(GraphNode<T> node) throws GraphException {
        // Always continue, as children may have changed.
        return true;
    }
}
