package org.dockbox.hartshorn.application.context.validate;

import org.dockbox.hartshorn.application.context.DependencyGraph;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.graph.ContainableGraphNode;
import org.dockbox.hartshorn.util.graph.GraphNode;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class CyclicDependencyGraphValidator implements DependencyGraphValidator {

    @Override
    public void validateBeforeConfiguration(DependencyGraph dependencyGraph) throws ApplicationException {
        Set<GraphNode<DependencyContext<?>>> nodes = dependencyGraph.nodes();
        for (GraphNode<DependencyContext<?>> node : nodes) {
            if (node.isLeaf()) {
                continue;
            }
            if (node instanceof ContainableGraphNode<DependencyContext<?>> contextContainableGraphNode && contextContainableGraphNode.isRoot()) {
                continue;
            }
            this.checkNodeNotCyclic(node);
        }
    }

    private void checkNodeNotCyclic(GraphNode<DependencyContext<?>> node) throws ApplicationException {
        Set<GraphNode<DependencyContext<?>>> knownNodes = new HashSet<>();
        knownNodes.add(node);
        Queue<GraphNode<DependencyContext<?>>> queue = new ArrayDeque<>();
        queue.add(node);
        while(!queue.isEmpty()) {
            GraphNode<DependencyContext<?>> current = queue.poll();
            for (GraphNode<DependencyContext<?>> child : current.children()) {
                if (knownNodes.contains(child)) {
                    // TODO: CyclicComponentException, for path reporting
                    throw new ApplicationException("Found cyclic dependency: %s".formatted(child));
                }
                knownNodes.add(child);
                queue.add(child);
            }
        }
    }
}
