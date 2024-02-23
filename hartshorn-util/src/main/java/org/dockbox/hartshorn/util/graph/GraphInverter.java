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

import java.util.function.Predicate;

/**
 * A function that inverts a {@link Graph} for all nodes matching a given rule. The inverted graph will have a new
 * root node, which is the first node that matches the given rule. The inverted graph will only contain all nodes
 * that are reachable from the new root node.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class GraphInverter {

    /**
     * Inverts the given graph using the node with the given value as the new root node. The inverted graph will only
     * contain all nodes that are reachable from the new root node. If there are multiple nodes with the given value,
     * the graph will contain the first of these nodes as a root node.
     *
     * @param graph the graph to invert
     * @param root the value of the node to use as the new root node
     * @return the inverted graph
     * @param <T> the type of the value of the node
     * @throws GraphException if the graph could not be inverted
     */
    public <T> Graph<T> invertGraphForValue(Graph<T> graph, T root) throws GraphException {
        return this.invertGraph(graph, node -> node.equals(root));
    }

    /**
     * Inverts the given graph using the node that matches the given rule as the new root node. The inverted graph will
     * only contain all nodes that are reachable from the new root node. If there are multiple nodes that match the
     * given rule, the graph will contain the first of these nodes as a root node.
     *
     * @param graph the graph to invert
     * @param rule the rule to match the new root node
     * @return the inverted graph
     * @param <T> the type of the value of the node
     * @throws GraphException if the graph could not be inverted
     */
    public <T> Graph<T> invertGraph(Graph<T> graph, Predicate<T> rule) throws GraphException {
        Graph<T> inverted = new SimpleGraph<>();
        GraphNodeLookupVisitor<T> visitor = new GraphNodeLookupVisitor<>(rule);
        visitor.iterate(graph);
        GraphNode<T> foundNode = visitor.foundNode();
        if (foundNode == null) {
            throw new GraphException("Could not find new root node for inverted graph");
        }

        // Create a parent-less copy of the found node, otherwise it is not a valid root node
        MutableGraphNode<T> invertedOrigin = new SimpleGraphNode<>(foundNode.value());
        GraphNode<T> graphNode = this.invertNode(foundNode, invertedOrigin);
        inverted.addRoot(graphNode);
        return inverted;
    }

    private <T> GraphNode<T> invertNode(GraphNode<T> originalNode, MutableGraphNode<T> invertedNode) {
        if (originalNode instanceof ContainableGraphNode<T> containable) {
            for (GraphNode<T> parent : containable.parents()) {
                MutableGraphNode<T> invertedParent = new SimpleGraphNode<>(parent.value());
                // Only set children, do not set parents, as this would create relations that are not relevant
                // considering the new graph root.
                invertedNode.addChild(invertedParent);
                this.invertNode(parent, invertedParent);
            }
        }
        return invertedNode;
    }
}
