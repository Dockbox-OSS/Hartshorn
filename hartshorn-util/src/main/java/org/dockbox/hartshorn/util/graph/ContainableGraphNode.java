package org.dockbox.hartshorn.util.graph;

import java.util.Set;

public interface ContainableGraphNode<T> extends GraphNode<T> {

    Set<GraphNode<T>> parents();

    default boolean isRoot() {
        return this.parents().isEmpty();
    }
}
