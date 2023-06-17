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

import java.util.HashSet;
import java.util.Set;

public class SimpleGraph<T> implements Graph<T> {

    private final Set<GraphNode<T>> nodes;

    public SimpleGraph() {
        this(new HashSet<>());
    }

    public SimpleGraph(final Set<GraphNode<T>> nodes) {
        this.nodes = new HashSet<>();
        this.addRoots(nodes);
    }

    @Override
    public Set<GraphNode<T>> roots() {
        // Node could have been modified to no longer be a root, so filter out any non-root nodes
        this.nodes.removeIf(node -> !node.isRoot());
        return Set.copyOf(this.nodes);
    }

    @Override
    public void addRoot(final GraphNode<T> root) {
        if (root.isRoot()) {
            this.nodes.add(root);
        }
    }

    @Override
    public void addRoots(final Set<GraphNode<T>> roots) {
        for (final GraphNode<T> root : roots) {
            this.addRoot(root);
        }
    }

    @Override
    public void clear() {
        this.nodes.clear();
    }

    @Override
    public boolean isEmpty() {
        return this.nodes.isEmpty();
    }
}
