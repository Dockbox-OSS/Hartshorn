package org.dockbox.hartshorn.util.graph;

import java.util.Set;

public class ImmutableGraphNode<T> implements GraphNode<T> {

    private final T value;
    private final Set<GraphNode<T>> children;

    public ImmutableGraphNode(final GraphNode<T> node) {
        this.value = node.value();
        this.children = Set.copyOf(node.children());
    }

    @Override
    public T value() {
        return this.value;
    }

    @Override
    public Set<GraphNode<T>> children() {
        return this.children;
    }
}
