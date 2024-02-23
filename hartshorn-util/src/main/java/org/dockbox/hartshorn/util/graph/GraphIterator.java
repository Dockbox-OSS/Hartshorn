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
 * A function that iterates over a {@link Graph} and returns a set of all visited {@link GraphNode}s in the graph.
 * The order in which the nodes are visited is not guaranteed.
 *
 * @param <T> the type of the value of the node
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface GraphIterator<T> {

    /**
     * Iterates over the given {@link Graph} and returns a set of all visited {@link GraphNode}s in the graph.
     * It remains up to the implementation to determine the order in which the nodes are visited, and which
     * nodes are visited.
     *
     * @param graph the graph to iterate over
     * @return a set of all visited nodes
     * @throws GraphException if the graph could not be iterated
     */
    Set<GraphNode<T>> iterate(Graph<T> graph) throws GraphException;

}
