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

import org.dockbox.hartshorn.util.graph.ContainableGraphNode;
import org.dockbox.hartshorn.util.graph.DepthFirstGraphVisitor;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.introspect.TypeParameterList;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TypeParameterResolverGraphVisitor extends DepthFirstGraphVisitor<TypeView<?>> {

    private final TypeView<?> lookForParent;
    private final TypeParameterView[] parameters;
    private final int total;
    private int discovered = 0;

    public TypeParameterResolverGraphVisitor(final TypeView<?> lookForParent) {
        this.lookForParent = lookForParent;
        this.total = lookForParent.typeParameters().count();
        this.parameters = new TypeParameterView[this.total];
    }

    public List<TypeParameterView> parameters() {
        // Do not use List.of, as it will throw an exception if the array contains nulls (unresolved parameters)
        return Arrays.asList(this.parameters);
    }

    @Override
    protected boolean visit(final GraphNode<TypeView<?>> node) {
        final TypeView<?> type = node.value();
        if (!type.isChildOf(this.lookForParent.type())) {
            // Type is not a child of the parent type, no need to continue
            return false;
        }

        final TypeParameterList outputParameters = type.typeParameters().allOutput();
        final Set<TypeParameterView> consumedByParent = outputParameters.stream()
                .filter(parameter -> parameter.consumedBy().is(this.lookForParent.type()))
                .collect(Collectors.toSet());

        if (consumedByParent.isEmpty()) {
            // Haven't found the type parameter yet, continue searching the graph
            return true;
        }

        for (TypeParameterView parameterView : consumedByParent) {
            // Don't waste time looking up the same parameter twice
            if (this.parameters[parameterView.index()] == null) {
                if (parameterView.isVariable()) {
                    final Option<TypeParameterView> inputParameter = type.typeParameters().atIndex(parameterView.index());
                    if (inputParameter.present()) {
                        parameterView = inputParameter.get();
                    }
                }
                final Option<TypeView<?>> resolvedType = parameterView.resolvedType();
                if (resolvedType.present()) {
                    this.parameters[parameterView.index()] = parameterView;
                    this.discovered++;
                }
                else {
                    final ContainableGraphNode<TypeView<?>> mutableContainableGraphNode = (ContainableGraphNode<TypeView<?>>) node;
                    Set<GraphNode<TypeView<?>>> parents = mutableContainableGraphNode.parents();
                    assert parents.size() == 1; // Can be more :) ...
                    // TODO: Resolve first concrete parent parameter (output->input already possible, needs input->output...)
                    // Resolve to child, as the type parameter is not declared by the current type
                }
            }
        }

        // If all parameters have been resolved, we can stop searching
        return this.discovered < this.total;
    }
}
