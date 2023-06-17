/*
 * Copyright 2019-2023 the original author or authors.
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

public abstract class BreadthFirstGraphVisitor<T> extends AbstractGraphVisitor<T> {

    @Override
    public final Set<GraphNode<T>> iterate(final Graph<T> graph) throws GraphException {
        final Set<GraphNode<T>> visited = new HashSet<>();

        Set<GraphNode<T>> nodes = graph.roots();
        while (!nodes.isEmpty()) {
            this.beforePathVisited();
            final Set<GraphNode<T>> currentRow = this.filterNodesWithUnresolvedParents(visited, nodes);
            nodes = this.visitRow(visited, currentRow);
            this.afterPathVisited();
        }
        return visited;
    }

    private Set<GraphNode<T>> visitRow(final Set<GraphNode<T>> visited, final Set<GraphNode<T>> currentRow) throws GraphException {
        final Set<GraphNode<T>> nextRow = new HashSet<>();
        for (final GraphNode<T> node : currentRow) {
            if (visited.add(node) && this.visit(node)) {
                nextRow.addAll(node.children());
            }
        }
        return nextRow;
    }

    private Set<GraphNode<T>> filterNodesWithUnresolvedParents(final Set<GraphNode<T>> visited, final Set<GraphNode<T>> nodes) {
        final Set<GraphNode<T>> currentRow = new HashSet<>(nodes);
        // Filter out nodes that have parents that haven't been visited yet, as they can't be visited yet
        // until their parents have been visited first.
        for (final GraphNode<T> node : nodes) {
            if (!visited.containsAll(node.parents())) {
                currentRow.remove(node);
            }
        }
        return currentRow;
    }
}
