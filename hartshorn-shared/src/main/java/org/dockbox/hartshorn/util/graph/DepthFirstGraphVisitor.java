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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * A {@link GraphIterator} that traverses a {@link Graph} in a depth-first manner. This means that
 * the visitor will visit all nodes of a path before moving on to the next path. A node is only
 * visited if all of its parents have been visited, and if the node has not been visited before.
 *
 * @param <T> the type of the value of the node
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface DepthFirstGraphVisitor<T> extends ObservableGraphIterator<T> {

    @Override
    default Set<GraphNode<T>> iterate(Graph<T> graph) throws GraphException {
        Set<GraphNode<T>> visited = new HashSet<>();
        for (GraphNode<T> root : graph.roots()) {
            this.visitSingleRoot(visited, root);
        }
        return visited;
    }

    private void visitSingleRoot(Set<GraphNode<T>> visited, GraphNode<T> root) throws GraphException {
        Deque<GraphNode<T>> stack = new ArrayDeque<>();
        stack.push(root);
        boolean atNextPathStart = true;
        while (!stack.isEmpty()) {
            GraphNode<T> node = stack.pop();
            if (visited.add(node) && this.visit(node)) {
                atNextPathStart = this.visitNextNode(stack, visited, atNextPathStart, node);
            }
        }
    }

    private boolean visitNextNode(Deque<GraphNode<T>> stack, Set<GraphNode<T>> visited, boolean atNextPathStart, GraphNode<T> node) {
        // Do not use children directly, as we want to ensure they can be visited
        // before considering them for the next node.
        Set<GraphNode<T>> visitableChildren = this.visitableChildren(node, visited);

        if (visitableChildren.isEmpty()) {
            this.afterPathVisited();
            atNextPathStart = true;
        } else {
            for (GraphNode<T> visitableChild : visitableChildren) {
                stack.push(visitableChild);
            }
            if (atNextPathStart) {
                this.beforePathVisited();
                atNextPathStart = false;
            }
        }
        return atNextPathStart;
    }

    private Set<GraphNode<T>> visitableChildren(GraphNode<T> node, Set<GraphNode<T>> visited) {
        Set<GraphNode<T>> visitableChildren = new HashSet<>();
        for (GraphNode<T> child : node.children()) {
            if (child instanceof ContainableGraphNode<T> containable && visited.containsAll(containable.parents())) {
                visitableChildren.add(child);
            }
        }
        return visitableChildren;
    }
}
