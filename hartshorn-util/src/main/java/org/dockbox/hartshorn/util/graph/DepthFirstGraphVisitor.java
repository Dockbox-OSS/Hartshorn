package org.dockbox.hartshorn.util.graph;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public abstract class DepthFirstGraphVisitor<T> extends AbstractGraphVisitor<T> {

    @Override
    public void iterate(final Graph<T> graph) throws GraphException {
        final Set<GraphNode<T>> visited = new HashSet<>();
        for (final GraphNode<T> root : graph.roots()) {
            this.visitSingleRoot(visited, root);
        }
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
            if (visited.containsAll(child.parents())) {
                visitableChildren.add(child);
            }
        }
        return visitableChildren;
    }
}
