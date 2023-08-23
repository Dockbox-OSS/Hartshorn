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

public abstract class DepthFirstGraphVisitor<T> extends AbstractGraphVisitor<T> {

    @Override
    public Set<GraphNode<T>> iterate(final Graph<T> graph) throws GraphException {
        final Set<GraphNode<T>> visited = new HashSet<>();
        for (final GraphNode<T> root : graph.roots()) {
            this.visitSingleRoot(visited, root);
        }
        return visited;
    }

    private void visitSingleRoot(final Set<GraphNode<T>> visited, final GraphNode<T> root) throws GraphException {
        final Deque<GraphNode<T>> stack = new ArrayDeque<>();
        stack.push(root);
        boolean atNextPathStart = true;
        while (!stack.isEmpty()) {
            final GraphNode<T> node = stack.pop();
            if (visited.add(node) && this.visit(node)) {
                atNextPathStart = this.visitNextNode(stack, visited, atNextPathStart, node);
            }
        }
    }

    private boolean visitNextNode(final Deque<GraphNode<T>> stack, final Set<GraphNode<T>> visited, boolean atNextPathStart, final GraphNode<T> node) {
        // Do not use children directly, as we want to ensure they can be visited
        // before considering them for the next node.
        final Set<GraphNode<T>> visitableChildren = this.visitableChildren(node, visited);

        if (visitableChildren.isEmpty()) {
            this.afterPathVisited();
            atNextPathStart = true;
        } else {
            for (final GraphNode<T> visitableChild : visitableChildren) {
                stack.push(visitableChild);
            }
            if (atNextPathStart) {
                this.beforePathVisited();
                atNextPathStart = false;
            }
        }
        return atNextPathStart;
    }

    private Set<GraphNode<T>> visitableChildren(final GraphNode<T> node, final Set<GraphNode<T>> visited) {
        final Set<GraphNode<T>> visitableChildren = new HashSet<>();
        for (final GraphNode<T> child : node.children()) {
            if (child instanceof ContainableGraphNode<T> containable && visited.containsAll(containable.parents())) {
                visitableChildren.add(child);
            }
        }
        return visitableChildren;
    }
}
