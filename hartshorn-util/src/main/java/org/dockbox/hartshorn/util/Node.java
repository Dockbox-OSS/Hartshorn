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

package org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.util.graph.GraphNode;

/**
 * A node in a property structure. A node has a name and a value. The name of a node is
 * commonly used to identify the node, but is not required to be unique.
 *
 * <p><b>Note</b>: This interface is intended to be used for reporting purposes. It is not
 * intended to be used as a vertex in a graph. For that purpose, use {@link GraphNode}.
 *
 * @param <T> the type of the value of the node
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface Node<T> {

    /**
     * Returns the name of the node. This is commonly used to identify the node.
     *
     * <p>The name of a node is not required to be unique, but it is recommended. Typically,
     * the name of a node is equal to a property name.
     *
     * @return the name of the node
     */
    String name();

    /**
     * Returns the value of the node.
     *
     * @return the value of the node
     */
    T value();

    /**
     * Accepts a {@link NodeVisitor} and returns the result of the visit.
     *
     * @param visitor the visitor to accept
     * @return the result of the visit
     * @param <R> the type of the result
     */
    <R> R accept(NodeVisitor<R> visitor);
}
