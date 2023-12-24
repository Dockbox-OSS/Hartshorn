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
 * A {@link GraphNode} that is aware of its parents. This can be used to resolve a graph in a bottom-up manner, for
 * example to reverse a graph.
 *
 * @param <T> the type of the value of the node
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ContainableGraphNode<T> extends GraphNode<T> {

    /**
     * Returns the parents of this node.
     *
     * @return the parents of this node
     */
    Set<GraphNode<T>> parents();

    /**
     * Returns whether this node is a root node. A root node is a node that has no parents.
     *
     * @return whether this node is a root node
     */
    default boolean isRoot() {
        return this.parents().isEmpty();
    }
}
