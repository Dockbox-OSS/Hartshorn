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

package org.dockbox.hartshorn.util.introspect.reflect;

import org.dockbox.hartshorn.util.graph.DepthFirstGraphVisitor;
import org.dockbox.hartshorn.util.graph.Graph;
import org.dockbox.hartshorn.util.graph.GraphException;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TypeParameterResolverGraphVisitor extends DepthFirstGraphVisitor<TypeView<?>> {

    private final TypeView<?> lookForParent;
    private final TypeParameterView[] parameters;

    public TypeParameterResolverGraphVisitor(final TypeView<?> lookForParent) {
        this.lookForParent = lookForParent;
        this.parameters = new TypeParameterView[lookForParent.typeParameters().count()];
    }

    public List<TypeParameterView> parameters() {
        // Do not use List.of, as it will throw an exception if the array contains nulls (unresolved parameters)
        return Arrays.asList(this.parameters);
    }

    @Override
    public Set<GraphNode<TypeView<?>>> iterate(final Graph<TypeView<?>> graph) throws GraphException {
        final Set<GraphNode<TypeView<?>>> roots = graph.roots();
        if (roots.size() != 1) {
            throw new GraphException("Expected exactly one root node, found " + roots.size());
        }
        final GraphNode<TypeView<?>> root = roots.iterator().next();
        // Compare type, not view, as the view is likely parameterized and thus a different non-equal instance
        if (root.value().type() != this.lookForParent.type()) {
            throw new GraphException("Expected root node to be " + this.lookForParent.type().getName() + ", found " + root.value().type().getName());
        }

        return super.iterate(graph);
    }

    @Override
    protected boolean visit(final GraphNode<TypeView<?>> node) throws GraphException {
        // TODO: Iterate through
        return false;
    }
}
