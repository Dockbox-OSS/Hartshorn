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

import java.util.Collection;

/**
 * A specialized {@link GraphNode} that can have parents and children added to it, and have its
 * value be changed. This is a combination of {@link MutableGraphNode} and {@link ContainableGraphNode},
 * with added methods to allow the addition of parents.
 *
 * @param <T> the type of the value of the node
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface MutableContainableGraphNode<T> extends MutableGraphNode<T>, ContainableGraphNode<T> {

    /**
     * Adds a parent to this node. If the provided node is already a parent of this node, this
     * method does nothing. If the provided node is also a {@link MutableGraphNode}, this node
     * is added as a child to the provided node.
     *
     * @param parent the parent to add
     */
    void addParent(GraphNode<T> parent);

    /**
     * Adds a collection of parents to this node. If any of the provided nodes is already a parent
     * of this node, this method does nothing. If any of the provided nodes is also a {@link MutableGraphNode},
     * this node is added as a child to the provided node.
     *
     * @param parents the parents to add
     */
    void addParents(Collection<GraphNode<T>> parents);
}
