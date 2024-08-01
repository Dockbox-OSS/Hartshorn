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

import java.util.Set;

/**
 * A graph is a collection of {@link GraphNode}s. A graph may have multiple roots, which are
 * nodes that have no parent nodes. A graph may be empty, in which case it has no roots.
 *
 * <p>Graphs may be traversed using a {@link GraphIterator}. The order in which nodes are
 * visited is not guaranteed, unless a specific visitor is used that guarantees a specific
 * order.
 *
 * @param <T> the type of the content of the graph
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface Graph<T> {

    /**
     * Returns a set of all root nodes in the graph. If the graph is empty, an empty set is
     * returned.
     *
     * <p>Note that if the graph is cyclic, this method may return an empty set, as there are
     * no nodes without parents.
     *
     * @return a set of all root nodes in the graph
     */
    Set<GraphNode<T>> roots();

    /**
     * Adds a root node to the graph. If the node is already present in the graph, it is not
     * added again. If the node and graph implementation are aware of parent-child relations,
     * and the node is already a child of another node, this method may default to not adding
     * the node to the graph.
     *
     * @param root the root node to add to the graph
     */
    void addRoot(GraphNode<T> root);

    /**
     * Adds a set of root nodes to the graph. If any of the nodes are already present in the
     * graph, they are not added again. If any of the nodes and graph implementation are aware
     * of parent-child relations, and any of the nodes are already a child of another node,
     * this method may default to not adding the node to the graph.
     *
     * @param roots the root nodes to add to the graph
     */
    void addRoots(Set<GraphNode<T>> roots);

    /**
     * Removes all nodes from the graph.
     */
    void clear();

    /**
     * Returns {@code true} if the graph contains no root nodes. This may still return
     * {@code true} if the graph contains nodes, but they are all children of other nodes.
     *
     * @return {@code true} if the graph contains no root nodes
     */
    boolean isEmpty();
}
