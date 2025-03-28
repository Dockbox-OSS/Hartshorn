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

import org.dockbox.hartshorn.util.ApplicationRuntimeException;

import java.util.HashSet;
import java.util.Set;

/**
 * A {@link Graph} implementation that tracks all nodes that have been added to the graph. This
 * implementation is not thread-safe.
 *
 * <p>This implementation assumes nodes are not modified after they've been added to the graph, and
 * that no other visitors are applied to the graph.
 *
 * @param <T> the type of the content of the graph
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class SimpleContentAwareGraph<T> extends SimpleGraph<T> implements ContentAwareGraph<T>, BreadthFirstGraphVisitor<T> {

    private final Set<GraphNode<T>> nodes = new HashSet<>();

    @Override
    public Set<GraphNode<T>> nodes() {
        return Set.copyOf(this.nodes);
    }

    @Override
    public void addRoot(GraphNode<T> root) {
        Set<GraphNode<T>> collected = new HashSet<>();
        try {
            this.visitSingle(collected, root);
            this.nodes.addAll(collected);
        }
        catch (GraphException e) {
            throw new ApplicationRuntimeException(e);
        }
        super.addRoot(root);
    }

    @Override
    public Set<GraphNode<T>> iterate(Graph<T> graph) throws GraphException {
        throw new UnsupportedOperationException("Visiting is only for internal tracking.");
    }

    @Override
    public boolean visit(GraphNode<T> node) throws GraphException {
        // Always continue, as children may have changed.
        return true;
    }
}
