/*
 * Copyright 2019-2025 the original author or authors.
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

import org.dockbox.hartshorn.util.collections.CollectionUtilities;
import org.dockbox.hartshorn.util.graph.Graph;
import org.dockbox.hartshorn.util.graph.GraphException;
import org.dockbox.hartshorn.util.graph.GraphInverter;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.introspect.TypeParameterList;
import org.dockbox.hartshorn.util.introspect.reflect.view.TypeHierarchyGraph;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Resolver for type parameters of types in a type hierarchy. This allows you to trace the type
 * hierarchy of a base type, and resolve the input type parameters for a given parent type, even if
 * the concrete types are scattered across multiple levels of the hierarchy.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class TypeParameterResolver {

    /**
     * Attempts to resolve the input type parameters for the given parent, tracing the type
     * hierarchy of the base type. This includes tracing of any interfaces and superclasses of the
     * base type.
     *
     * <p>If there is no concrete type parameter for a given index, the type parameter at that index
     * in the result will
     * be {@code null}.
     *
     * <h2>Examples</h2>
     * <h3>All parameters defined at same level</h3>
     * <p>In simple scenarios, all type parameters may be defined at the same level in the
     * hierarchy. For example, if
     * the base type is {@code ArrayList<String>} and the parent is {@code Collection}, this method
     * will return a list containing a single type parameter view for {@code String}.
     *
     * <h3>Parameters defined at different levels</h3>
     * <p>Parameters may be defined in different levels of the hierarchy. For example, if the base
     * type is {@code
     * StringToNumberFunction}, which extends {@code NumberFunction<String>}, which in turn extends
     * {@code Function<T, Number>}, and the parent is {@code Function}, this method will return a
     * list containing both {@code String} and {@code Number} as type parameter views, in that
     * order.
     *
     * @param baseType the base type from which to resolve the input type parameters
     * @param forParent the parent type for which to resolve the input type parameters
     *
     * @return a list of type parameter views representing the input type parameters for the parent
     * type
     *
     * @throws TypeParameterResolutionException if the type parameters cannot be resolved, for
     * example if the parent type is not found in the hierarchy
     */
    public List<TypeParameterView> resolveInputForParent(
        TypeView<?> baseType,
        TypeView<?> forParent
    ) throws TypeParameterResolutionException {
        TypeHierarchyGraph baseTypeHierarchy = TypeHierarchyGraph.of(baseType);
        try {
            Graph<TypeView<?>> inverted = new GraphInverter().invertGraph(baseTypeHierarchy,
                node -> node.type() == forParent.type());
            return this.resolveFromHierarchy(forParent, inverted);
        }
        catch (GraphException e) {
            throw new TypeParameterResolutionException("Failed to resolve type parameters for "
                + forParent.type().getName()
                + " from hierarchy graph", e);
        }
    }

    private List<TypeParameterView> resolveFromHierarchy(
        TypeView<?> parent,
        Graph<TypeView<?>> graph
    ) throws TypeParameterResolutionException {
        TypeParameterView[] parameters =
            new TypeParameterView[parent.typeParameters().allInput().count()];
        Set<GraphNode<TypeView<?>>> roots = graph.roots();
        if (roots.size() != 1) {
            throw new TypeParameterResolutionException("Expected exactly one root node, found "
                + roots.size());
        }
        GraphNode<TypeView<?>> root = CollectionUtilities.first(roots);
        // Compare type, not view, as the view is likely parameterized and thus a different
        // non-equal instance
        if (root.value().type() != parent.type()) {
            throw new TypeParameterResolutionException("Expected root node to be " + parent.type()
                .getName() + ", found " + root.value().type().getName());
        }

        this.visit(root, parameters);
        // Do not use List.of, as it will throw an exception if the array contains nulls
        // (unresolved parameters)
        return Arrays.asList(parameters);
    }

    private void visit(GraphNode<TypeView<?>> node, TypeParameterView[] parameters) {
        TypeView<?> currentValue = node.value();
        List<TypeParameterView> inputParameters = currentValue.typeParameters().allInput().asList();
        for (int i = 0; i < inputParameters.size(); i++) {
            TypeParameterView parameter = inputParameters.get(i);
            parameters[i] = this.resolveParameter(node, parameter);
        }
    }

    private TypeParameterView resolveParameter(
        GraphNode<TypeView<?>> node,
        TypeParameterView parameter
    ) {
        int index = parameter.index();
        TypeView<?> typeView = node.value();
        Class<?> type = typeView.type();
        Set<GraphNode<TypeView<?>>> children = node.children();

        // If no definition exists, and we're at the end of the hierarchy, we can only attempt to
        // resolve the parameter from the generic parent.
        if (children.isEmpty() && parameter.definition().absent()) {
            // Only attempt to resolve from the generic parent if the type is parameterized, and it
            // is actually the parent of the type we're currently at.
            if (typeView.isParameterized() && parameter.declaredBy().is(typeView.type())) {
                return this.resolveFromGenericParent(parameter, typeView);
            }
        }
        else {
            // Definition is available, so we can resolve the parameter from the type definition.
            // This may still be a type variable, but we can recursively resolve it further down
            // the hierarchy.
            return this.resolveFromTypeDefinition(index, type, children);
        }
        // If we reach this point, it means we couldn't resolve the parameter by any means, so we
        // return null.
        return null;
    }

    private TypeParameterView resolveFromGenericParent(
        TypeParameterView parameter,
        TypeView<?> typeView
    ) {
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

    private TypeParameterView resolveFromTypeDefinition(
        int index,
        Class<?> type,
        Set<GraphNode<TypeView<?>>> children
    ) {
        for (GraphNode<TypeView<?>> child : children) {
            TypeParameterList outputParameters = child.value().typeParameters().outputFor(type);
            // If there are no output parameters for the given type, we don't have anything to
            // resolve for this child.
            if (outputParameters.isEmpty()) {
                continue;
            }
            Option<TypeParameterView> parameterAtIndex = outputParameters.atIndex(index);
            if (parameterAtIndex.present()) {
                TypeParameterView parameterView = parameterAtIndex.get();
                // If we're immediately at a concrete type parameter, return it.
                if (!parameterView.isVariable()) {
                    return parameterView;
                }
                else {
                    // If the parameter is a variable, we need to resolve it further down the
                    // hierarchy.
                    Option<TypeParameterView> definition = parameterView.definition();
                    if (definition.present()) {
                        TypeParameterView resolved = this.resolveParameter(child, definition.get());
                        if (resolved != null) {
                            return resolved;
                        }
                    }
                }
            }
        }
        // No definition found for the parameter at the given index, return null. This typically
        // indicates there's no non-variable type parameter defined for the given index, anywhere
        // in the hierarchy.
        // This is not an error, as the type parameter may simply not be defined for the given type.
        // However, it also means we cannot assume a concrete type for the parameter. In simple
        // cases, there'd only be one variable in the hierarchy, but in more complex cases, there
        // may be overlapping type parameters at different parallel levels.
        return null;
    }
}
