package org.dockbox.hartshorn.util.graph;

import java.util.Collection;
import java.util.Set;

public interface GraphNode<T> {

    T value();

    Set<GraphNode<T>> parents();

    Set<GraphNode<T>> children();

    void addParent(GraphNode<T> parent);

    void addParents(Collection<GraphNode<T>> parents);

    void addChild(GraphNode<T> child);

    void addChildren(Collection<GraphNode<T>> children);

    default boolean isRoot() {
        return this.parents().isEmpty();
    }

    default boolean isLeaf() {
        return this.children().isEmpty();
    }
}
