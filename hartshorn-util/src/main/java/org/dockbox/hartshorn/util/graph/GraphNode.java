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
 * A node in a {@link Graph}. A node has a value, and may have children. A node that has no children is
 * considered a leaf node.
 *
 * @param <T> the type of the value of the node
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface GraphNode<T> {

    /**
     * Returns the value of this node.
     *
     * @return the value of this node
     */
    T value();

    /**
     * Returns a set of all children of this node. If this node is a leaf node, this method returns an
     * empty set.
     *
     * @return a set of all children of this node
     */
    Set<GraphNode<T>> children();

    /**
     * Indicates if this node is a leaf node. A leaf node is a node that has no children.
     *
     * @return {@code true} if this node is a leaf node, {@code false} otherwise
     */
    default boolean isLeaf() {
        return this.children().isEmpty();
    }
}
