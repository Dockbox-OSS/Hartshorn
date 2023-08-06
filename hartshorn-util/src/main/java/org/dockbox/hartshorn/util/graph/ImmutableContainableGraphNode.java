package org.dockbox.hartshorn.util.graph;

import java.util.Set;

public class ImmutableContainableGraphNode<T> extends ImmutableGraphNode<T> implements ContainableGraphNode<T> {

    private final Set<GraphNode<T>> parents;

    public ImmutableContainableGraphNode(final GraphNode<T> node) {
        super(node);
        if (node instanceof ContainableGraphNode<T> containable) {
            this.parents = Set.copyOf(containable.parents());
        }
        else {
            this.parents = Set.of();
        }
    }

    @Override
    public Set<GraphNode<T>> parents() {
        return this.parents;
    }
}
