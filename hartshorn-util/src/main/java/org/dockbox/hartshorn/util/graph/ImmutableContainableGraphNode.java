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
 * An immutable implementation of {@link ContainableGraphNode}. This implementation is used to prevent
 * modification of the graph structure.
 *
 * @param <T> the type of the node
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ImmutableContainableGraphNode<T> extends ImmutableGraphNode<T> implements ContainableGraphNode<T> {

    private final Set<GraphNode<T>> parents;

    public ImmutableContainableGraphNode(GraphNode<T> node) {
        super(node);
        if (node instanceof ContainableGraphNode<T> containable) {
            this.parents = Set.copyOf(containable.parents());
        }
        else {
            this.parents = Set.of();
        }
    }

    @Override
    public Set<GraphNode<T>> parents() {
        return this.parents;
    }
}
