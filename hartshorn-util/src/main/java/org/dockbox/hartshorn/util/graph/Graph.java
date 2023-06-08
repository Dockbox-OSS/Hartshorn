package org.dockbox.hartshorn.util.graph;

import java.util.Set;

public interface Graph<T> {
    Set<GraphNode<T>> roots();

    void addRoot(GraphNode<T> root);

    void addRoots(Set<GraphNode<T>> roots);

    void clear();

    boolean isEmpty();
}
