/*
 * Copyright 2019-2024 the original author or authors.
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

import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.graph.Graph;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.introspect.TypeParameterList;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * TODO: #1059 Add documentation
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class TypeParameterResolver {

    private final TypeView<?> lookForParent;
    private final TypeParameterView[] parameters;

    public TypeParameterResolver(TypeView<?> lookForParent) {
        this.lookForParent = lookForParent;
        this.parameters = new TypeParameterView[lookForParent.typeParameters().allInput().count()];
    }

    public List<TypeParameterView> parameters() {
        // Do not use List.of, as it will throw an exception if the array contains nulls (unresolved parameters)
        return Arrays.asList(this.parameters);
    }

    public List<TypeParameterView> tryResolveFromGraph(Graph<TypeView<?>> graph) throws TypeParameterResolutionException {
        Set<GraphNode<TypeView<?>>> roots = graph.roots();
        if (roots.size() != 1) {
            throw new TypeParameterResolutionException("Expected exactly one root node, found " + roots.size());
        }
        GraphNode<TypeView<?>> root = CollectionUtilities.first(roots);
        // Compare type, not view, as the view is likely parameterized and thus a different non-equal instance
        if (root.value().type() != this.lookForParent.type()) {
            throw new TypeParameterResolutionException("Expected root node to be " + this.lookForParent.type().getName() + ", found " + root.value().type().getName());
        }

        this.visit(root);

        return this.parameters();
    }

    private void visit(GraphNode<TypeView<?>> node) {
        TypeView<?> currentValue = node.value();
        TypeParameterList inputParameters = currentValue.typeParameters().allInput();
        List<TypeParameterView> parameters = inputParameters.asList();

        for (int i = 0; i < parameters.size(); i++) {
            TypeParameterView parameter = parameters.get(i);
            this.parameters[i] = this.tryResolve(node, parameter);
        }
    }

    private TypeParameterView tryResolve(GraphNode<TypeView<?>> node, TypeParameterView parameter) {
        int index = parameter.index();
        TypeView<?> typeView = node.value();
        Class<?> type = typeView.type();
        Set<GraphNode<TypeView<?>>> children = node.children();

        // Reached original input, but no definition found indicates that the input is a generic parameterized
        // type (not part of class definition, but of method or field definition)
        if (children.isEmpty() && parameter.definition().absent()) {
            return this.tryResolveFromGenericType(parameter, typeView);
        }
        else {
            return this.tryResolveFromTypeDefinition(index, type, children);
        }
    }

    private TypeParameterView tryResolveFromGenericType(TypeParameterView parameter, TypeView<?> typeView) {
        TypeParameterList parameterViews = typeView.typeParameters().allInput();
        Option<TypeParameterView> parameterAtIndex = parameterViews.atIndex(parameter.index());
        if (parameterAtIndex.present()) {
            TypeParameterView parameterView = parameterAtIndex.get();
            if (!parameterView.isVariable()) {
                return parameterView;
            }
        }
        return null;
    }

    private TypeParameterView tryResolveFromTypeDefinition(int index, Class<?> type, Set<GraphNode<TypeView<?>>> children) {
        for (GraphNode<TypeView<?>> child : children) {
            TypeParameterList outputParameters = child.value().typeParameters().outputFor(type);
            if (outputParameters.isEmpty()) {
                continue;
            }
            Option<TypeParameterView> parameterAtIndex = outputParameters.atIndex(index);
            if (parameterAtIndex.present()) {
                TypeParameterView parameterView = parameterAtIndex.get();
                if (!parameterView.isVariable()) {
                    return parameterView;
                }
                else {
                    Option<TypeParameterView> definition = parameterView.definition();
                    if (definition.present()) {
                        TypeParameterView resolved = this.tryResolve(child, definition.get());
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
