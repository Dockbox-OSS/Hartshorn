package org.dockbox.hartshorn.util.graph;

import java.util.HashSet;
import java.util.Set;

public class SimpleGraph<T> implements Graph<T> {

    private final Set<GraphNode<T>> nodes;

    public SimpleGraph() {
        this(new HashSet<>());
    }

    public SimpleGraph(final Set<GraphNode<T>> nodes) {
        this.nodes = new HashSet<>();
        this.addRoots(nodes);
    }

    @Override
    public Set<GraphNode<T>> roots() {
        // Node could have been modified to no longer be a root, so filter out any non-root nodes
        this.nodes.removeIf(node -> !node.isRoot());
        return Set.copyOf(this.nodes);
    }

    @Override
    public void addRoot(final GraphNode<T> root) {
        if (root.isRoot()) {
            this.nodes.add(root);
        }
    }

    @Override
    public void addRoots(final Set<GraphNode<T>> roots) {
        for (final GraphNode<T> root : roots) {
            this.addRoot(root);
        }
    }

    @Override
    public void clear() {
        this.nodes.clear();
    }

    @Override
    public boolean isEmpty() {
        return this.nodes.isEmpty();
    }
}
