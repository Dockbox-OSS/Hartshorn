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
 * A specialized {@link Graph} that tracks all nodes that have been added to the graph. This
 * may assume that nodes are not added to the graph outside of the {@link #addRoot(GraphNode)},
 * and that nodes are treated as immutable.
 *
 * @param <T> the type of the content of the graph
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ContentAwareGraph<T> extends Graph<T> {

    /**
     * Returns a set of all nodes that have been added to the graph.
     *
     * @return a set of all nodes that have been added to the graph
     */
    Set<GraphNode<T>> nodes();
}
