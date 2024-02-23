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

/**
 * A {@link GraphIterator} that can observe the traversal of a {@link Graph}. This iterator
 * can be used to implement algorithms that require knowledge of the traversal of a graph.
 *
 * <p>Supported observations relate to the visiting of new paths. A path is a sequence of nodes
 * that are visited in a single traversal. It remains up to the implementation to determine when
 * a new traversal is started. Observations are made before and after a path is visited. Single
 * nodes are not considered paths, as they are already observable through {@link #visit(GraphNode)}.
 *
 * @param <T> the type of the value of the node
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ObservableGraphIterator<T> extends GraphIterator<T> {

    /**
     * Visits a node in the graph. This method is invoked for every node that is visited in a
     * traversal of the graph.
     *
     * @param node the node that is visited
     * @return {@code true} if the traversal should continue, {@code false} otherwise
     * @throws GraphException if an error occurs while visiting the node
     */
    boolean visit(GraphNode<T> node) throws GraphException;

    /**
     * Invoked before a path is visited. A path is a sequence of nodes that are visited in a
     * single traversal.
     */
    default void beforePathVisited() {
        // Do nothing by default, can be overridden
    }

    /**
     * Invoked after a path is visited. A path is a sequence of nodes that are visited in a
     * single traversal.
     */
    default void afterPathVisited() {
        // Do nothing by default, can be overridden
    }
}
