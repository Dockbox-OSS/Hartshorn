package org.dockbox.hartshorn.util.graph;

import java.util.Collection;

public interface MutableContainableGraphNode<T> extends MutableGraphNode<T>, ContainableGraphNode<T> {

    void addParent(GraphNode<T> parent);

    void addParents(Collection<GraphNode<T>> parents);
}
