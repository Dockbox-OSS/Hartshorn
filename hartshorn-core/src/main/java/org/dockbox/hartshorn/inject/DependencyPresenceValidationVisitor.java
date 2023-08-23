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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.util.graph.BreadthFirstGraphVisitor;
import org.dockbox.hartshorn.util.graph.GraphException;
import org.dockbox.hartshorn.util.graph.GraphNode;

import java.util.HashSet;
import java.util.Set;

public class DependencyPresenceValidationVisitor extends BreadthFirstGraphVisitor<DependencyContext<?>> {

    private final Set<GraphNode<DependencyContext<?>>> missingDependencies = new HashSet<>();
    private final Set<GraphNode<DependencyContext<?>>> visitedDependencies;

    public DependencyPresenceValidationVisitor(final Set<GraphNode<DependencyContext<?>>> visitedDependencies) {
        this.visitedDependencies = visitedDependencies;
    }

    public Set<GraphNode<DependencyContext<?>>> missingDependencies() {
        return this.missingDependencies;
    }

    @Override
    protected boolean visit(final GraphNode<DependencyContext<?>> node) throws GraphException {
        if (!this.visitedDependencies.contains(node)) {
            this.missingDependencies.add(node);
        }
        return true;
    }
}
