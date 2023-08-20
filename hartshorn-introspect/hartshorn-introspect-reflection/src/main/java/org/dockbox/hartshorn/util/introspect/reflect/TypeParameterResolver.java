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

import org.dockbox.hartshorn.util.graph.Graph;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.introspect.TypeParameterList;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TypeParameterResolver {

    private final TypeView<?> lookForParent;
    private final TypeParameterView[] parameters;

    public TypeParameterResolver(final TypeView<?> lookForParent) {
        this.lookForParent = lookForParent;
        this.parameters = new TypeParameterView[lookForParent.typeParameters().count()];
    }

    public List<TypeParameterView> parameters() {
        // Do not use List.of, as it will throw an exception if the array contains nulls (unresolved parameters)
        return Arrays.asList(this.parameters);
    }

    public List<TypeParameterView> tryResolveFromGraph(final Graph<TypeView<?>> graph) throws TypeParameterResolutionException {
        final Set<GraphNode<TypeView<?>>> roots = graph.roots();
        if (roots.size() != 1) {
            throw new TypeParameterResolutionException("Expected exactly one root node, found " + roots.size());
        }
        final GraphNode<TypeView<?>> root = roots.iterator().next();
        // Compare type, not view, as the view is likely parameterized and thus a different non-equal instance
        if (root.value().type() != this.lookForParent.type()) {
            throw new TypeParameterResolutionException("Expected root node to be " + this.lookForParent.type().getName() + ", found " + root.value().type().getName());
        }

        this.visit(root);

        return this.parameters();
    }

    private void visit(final GraphNode<TypeView<?>> node) throws TypeParameterResolutionException {
        final TypeView<?> currentValue = node.value();
        final TypeParameterList inputParameters = currentValue.typeParameters().allInput();
        final List<TypeParameterView> parameters = inputParameters.asList();

        for (int i = 0; i < parameters.size(); i++) {
            final TypeParameterView parameter = parameters.get(i);
            this.parameters[i] = this.tryResolve(node, parameter);
        }

        if (Arrays.stream(this.parameters).anyMatch(Objects::isNull)) {
            throw new TypeParameterResolutionException("Failed to resolve all type parameters");
        }
    }

    private TypeParameterView tryResolve(final GraphNode<TypeView<?>> node, final TypeParameterView parameter) {
        final int index = parameter.index();
        final TypeView<?> typeView = node.value();
        final Class<?> type = typeView.type();
        final Set<GraphNode<TypeView<?>>> children = node.children();

        // Reached original input, but no definition found indicates that the input is a generic parameterized
        // type (not part of class definition, but of method or field definition)
        if (children.isEmpty() && parameter.definition().absent()) {
            return this.tryResolveFromGenericType(parameter, typeView);
        }
        else {
            return this.tryResolveFromTypeDefinition(index, type, children);
        }
    }

    private TypeParameterView tryResolveFromGenericType(final TypeParameterView parameter, final TypeView<?> typeView) {
        final TypeParameterList parameterViews = typeView.typeParameters().allInput();
        final Option<TypeParameterView> parameterAtIndex = parameterViews.atIndex(parameter.index());
        if (parameterAtIndex.present()) {
            final TypeParameterView parameterView = parameterAtIndex.get();
            if (!parameterView.isVariable()) {
                return parameterView;
            }
        }
        return null;
    }

    private TypeParameterView tryResolveFromTypeDefinition(final int index, final Class<?> type, final Set<GraphNode<TypeView<?>>> children) {
        for (final GraphNode<TypeView<?>> child : children) {
            final TypeParameterList outputParameters = child.value().typeParameters().outputFor(type);
            if (outputParameters.isEmpty()) {
                continue;
            }
            final Option<TypeParameterView> parameterAtIndex = outputParameters.atIndex(index);
            if (parameterAtIndex.present()) {
                final TypeParameterView parameterView = parameterAtIndex.get();
                if (!parameterView.isVariable()) {
                    return parameterView;
                }
                else {
                    final Option<TypeParameterView> definition = parameterView.definition();
                    if (definition.present()) {
                        final TypeParameterView resolved = this.tryResolve(child, definition.get());
                        if (resolved != null) {
                            return resolved;
                        }
                    }
                }
            }
        }
        return null;
    }
}
