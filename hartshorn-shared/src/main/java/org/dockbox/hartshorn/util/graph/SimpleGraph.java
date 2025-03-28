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

/**
 * A simple implementation of a {@link Graph}, which is a collection of nodes that are not
 * necessarily connected.
 *
 * @param <T> the type of the content of the graph
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class SimpleGraph<T> implements Graph<T> {

    private final Set<GraphNode<T>> roots;

    public SimpleGraph() {
        this(new HashSet<>());
    }

    public SimpleGraph(Set<GraphNode<T>> roots) {
        this.roots = new HashSet<>();
        this.addRoots(Set.copyOf(roots));
    }

    @Override
    public Set<GraphNode<T>> roots() {
        // Node could have been modified to no longer be a root, so filter out any non-root nodes
        this.roots.removeIf(node -> {
            if (node instanceof ContainableGraphNode<T> containable) {
                return !containable.isRoot();
            }
            return false;
        });
        return Set.copyOf(this.roots);
    }

    @Override
    public void addRoot(GraphNode<T> root) {
        if (root instanceof ContainableGraphNode<T> containable) {
            if (containable.isRoot()) {
                this.roots.add(containable);
            }
        }
        else {
            // If the node is not a ContainableGraphNode, then it is a root by default
            this.roots.add(root);
        }
    }

    @Override
    public void addRoots(Set<GraphNode<T>> roots) {
        for (GraphNode<T> root : roots) {
            this.addRoot(root);
        }
    }

    @Override
    public void clear() {
        this.roots.clear();
    }

    @Override
    public boolean isEmpty() {
        return this.roots.isEmpty();
    }
}
