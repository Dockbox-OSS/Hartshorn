package org.dockbox.hartshorn.util.graph;

public abstract class AbstractGraphVisitor<T> implements GraphIterator<T> {

    protected abstract boolean visit(GraphNode<T> node) throws GraphException;

    protected void beforePathVisited() {
        // Do nothing by default, can be overridden
    }

    protected void afterPathVisited() {
        // Do nothing by default, can be overridden
    }
}
