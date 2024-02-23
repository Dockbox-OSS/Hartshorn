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
 * A mutable implementation of a {@link GraphNode}, which is a node that can have children added
 * to it.
 *
 * @param <T> the type of the value of the node
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface MutableGraphNode<T> extends GraphNode<T> {

    /**
     * Adds a child to this node. If the provided node is already a child of this node, this
     * method does nothing. If the provided node is a {@link MutableContainableGraphNode}, this
     * node is added as a parent to the provided node.
     *
     * @param child the child to add
     */
    void addChild(GraphNode<T> child);

    /**
     * Adds a collection of children to this node. If any of the provided nodes is already a child
     * of this node, this method does nothing. If any of the provided nodes is a {@link
     * MutableContainableGraphNode}, this node is added as a parent to the provided node.
     *
     * @param children the children to add
     */
    void addChildren(Collection<GraphNode<T>> children);
}
