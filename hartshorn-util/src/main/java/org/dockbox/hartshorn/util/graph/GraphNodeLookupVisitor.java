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

import java.util.function.Predicate;

public class GraphNodeLookupVisitor<T> extends BreadthFirstGraphVisitor<T> {

    private final Predicate<T> rule;
    private GraphNode<T> foundNode;

    public GraphNodeLookupVisitor(T valueToFind) {
        this(type -> type.equals(valueToFind));
    }

    public GraphNodeLookupVisitor(Predicate<T> rule) {
        this.rule = rule;
    }

    public GraphNode<T> foundNode() {
        return this.foundNode;
    }

    @Override
    protected boolean visit(GraphNode<T> node) throws GraphException {
        if (this.rule.test(node.value())) {
            this.foundNode = node;
            return false;
        }
        return true;
    }
}
