/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.reporting.collect;

import java.util.ArrayList;
import java.util.List;

public class GroupNode extends SimpleNode<List<Node<?>>> {

    public GroupNode(final String name) {
        super(name, new ArrayList<>());
    }

    public void add(final Node<?> value) {
        this.value().add(value);
    }

    public boolean has(final String name) {
        return this.value().stream()
                .anyMatch(node -> node.name().equals(name));
    }

    public Node<?> get(final String name) {
        return this.value().stream()
                .filter(node -> node.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public <R> R accept(final NodeVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
