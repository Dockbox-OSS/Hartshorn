package org.dockbox.hartshorn.util.graph;

import java.util.Set;

public interface ContentAwareGraph<T> extends Graph<T> {

    Set<GraphNode<T>> nodes();
}
