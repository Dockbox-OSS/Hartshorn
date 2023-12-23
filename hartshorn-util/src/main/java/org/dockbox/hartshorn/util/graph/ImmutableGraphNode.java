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
 * A simple immutable implementation of a {@link GraphNode}.
 *
 * @param <T> the type of the value of the node
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ImmutableGraphNode<T> implements GraphNode<T> {

    private final T value;
    private final Set<GraphNode<T>> children;

    public ImmutableGraphNode(GraphNode<T> node) {
        this.value = node.value();
        this.children = Set.copyOf(node.children());
    }

    @Override
    public T value() {
        return this.value;
    }

    @Override
    public Set<GraphNode<T>> children() {
        return this.children;
    }
}
