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

/**
 * A visitor for {@link Node}s.
 *
 * @param <T> the return type of the visitor
 */
public interface NodeVisitor<T> {

    /**
     * Visits a node that is neither a {@link GroupNode} nor an {@link ArrayNode}. This method is
     * called for all nodes that are not explicitly handled by other methods.
     *
     * @param node the node to visit
     * @return the result of the visit
     */
    T visit(Node<?> node);

    /**
     * Visits a {@link GroupNode}.
     *
     * @param node the node to visit
     * @return the result of the visit
     */
    T visit(GroupNode node);

    /**
     * Visits an {@link ArrayNode}.
     *
     * @param node the node to visit
     * @return the result of the visit
     */
    T visit(ArrayNode<?> node);
}
