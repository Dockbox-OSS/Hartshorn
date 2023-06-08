package org.dockbox.hartshorn.util.graph;

@FunctionalInterface
public interface GraphIterator<T> {

    void iterate(Graph<T> graph);

}
