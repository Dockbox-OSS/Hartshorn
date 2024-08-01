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

import java.util.ArrayList;
import java.util.List;

/**
 * A complex node that can contain multiple other nodes. This does not constrain
 * the present names to be unique, but it is recommended to ensure that they are.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class GroupNode extends SimpleNode<List<Node<?>>> {

    public GroupNode(String name) {
        super(name, new ArrayList<>());
    }

    /**
     * Adds the given node to the group.
     *
     * @param value the node to add
     */
    public void add(Node<?> value) {
        this.value().add(value);
    }

    /**
     * Returns whether the group contains a node with the given name.
     *
     * @param name the name to check
     * @return {@code true} if the group contains a node with the given name, {@code false} otherwise
     */
    public boolean has(String name) {
        return this.value().stream()
                .anyMatch(node -> node.name().equals(name));
    }

    /**
     * Returns the node with the given name, or {@code null} if no such node exists. If multiple nodes
     * with the same name exist, the first one is returned.
     *
     * @param name the name of the node to return
     * @return the node with the given name, or {@code null} if no such node exists
     */
    public Node<?> get(String name) {
        return this.value().stream()
                .filter(node -> node.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public <R> R accept(NodeVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
