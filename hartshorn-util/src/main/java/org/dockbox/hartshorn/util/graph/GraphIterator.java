package org.dockbox.hartshorn.util.graph;

import java.util.Set;

@FunctionalInterface
public interface GraphIterator<T> {

    Set<GraphNode<T>> iterate(Graph<T> graph) throws GraphException;

}
