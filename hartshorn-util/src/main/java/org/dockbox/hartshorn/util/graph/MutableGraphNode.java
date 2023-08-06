package org.dockbox.hartshorn.util.graph;

import java.util.Collection;

public interface MutableGraphNode<T> extends GraphNode<T> {

    void addChild(GraphNode<T> child);

    void addChildren(Collection<GraphNode<T>> children);
}
