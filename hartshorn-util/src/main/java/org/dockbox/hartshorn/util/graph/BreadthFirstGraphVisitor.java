/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.util.graph;

import java.util.HashSet;
import java.util.Set;

/**
 * A {@link GraphIterator} that iterates over a {@link Graph} in a breadth-first manner. This means
 * that all nodes on the same level are visited before any nodes on the next level are visited.
 *
 * <p>This iterator will not visit nodes that have parents that have not been visited yet. This
 * means that if a node has a parent that has not been visited yet, the node will not be visited
 * until all of its parents have been visited.
 *
 * @param <T> the type of the value of the node
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface BreadthFirstGraphVisitor<T> extends ObservableGraphIterator<T> {

    @Override
    default Set<GraphNode<T>> iterate(Graph<T> graph) throws GraphException {
        Set<GraphNode<T>> visited = new HashSet<>();

        Set<GraphNode<T>> nodes = graph.roots();
        while (!nodes.isEmpty()) {
            this.beforePathVisited();
            Set<GraphNode<T>> currentRow = this.filterNodesWithUnresolvedParents(visited, nodes);
            nodes = this.visitRow(visited, currentRow);
            this.afterPathVisited();
        }
        return visited;
    }

    /**
     * Visits a single node. If the node has not been visited yet, it will be visited through
     * {@link #visitRow(Set, Set)}.
     *
     * @param visited the set of visited nodes
     * @param node the node to visit
     * @throws GraphException when an error occurs while visiting the node
     */
    default void visitSingle(Set<GraphNode<T>> visited, GraphNode<T> node) throws GraphException {
        if (visited.add(node)) {
            this.visitRow(visited, Set.of(node));
        }
    }

    private Set<GraphNode<T>> visitRow(Set<GraphNode<T>> visited, Set<GraphNode<T>> currentRow) throws GraphException {
        Set<GraphNode<T>> nextRow = new HashSet<>();
        for (GraphNode<T> node : currentRow) {
            if (visited.add(node) && this.visit(node)) {
                nextRow.addAll(node.children());
            }
        }
        return nextRow;
    }

    private Set<GraphNode<T>> filterNodesWithUnresolvedParents(Set<GraphNode<T>> visited, Set<GraphNode<T>> nodes) {
        Set<GraphNode<T>> currentRow = new HashSet<>(nodes);
        // Filter out nodes that have parents that haven't been visited yet, as they can't be visited yet
        // until their parents have been visited first.
        for (GraphNode<T> node : nodes) {
            if (!this.hasVisitedParents(visited, nodes, node)) {
                currentRow.remove(node);
            }
        }
        return currentRow;
    }

    /**
     * Indicates whether all parent nodes of the given node have been visited or can reasonably be expected to be visited. This
     * method is used to determine whether a node can be visited or not.
     *
     * @param visited the set of visited nodes
     * @param allNodes the set of all nodes
     * @param node the node to check
     * @return {@code true} if all parent nodes have been visited, {@code false} otherwise
     */
    default boolean hasVisitedParents(Set<GraphNode<T>> visited, Set<GraphNode<T>> allNodes, GraphNode<T> node) {
        if (node instanceof ContainableGraphNode<T> containable) {
            return visited.containsAll(containable.parents());
        }
        return true;
    }
}
