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

import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.graph.Graph;
import org.dockbox.hartshorn.util.graph.GraphException;
import org.dockbox.hartshorn.util.graph.GraphInverter;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.SimpleTypeParameterList;
import org.dockbox.hartshorn.util.introspect.TypeParameterList;
import org.dockbox.hartshorn.util.introspect.TypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.view.TypeHierarchyGraph;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;

public abstract class AbstractReflectionTypeParametersIntrospector implements TypeParametersIntrospector {

    private final TypeView<?> type;
    private final Introspector introspector;

    private TypeHierarchyGraph typeHierarchy;
    private TypeParameterList outputParameters;

    protected AbstractReflectionTypeParametersIntrospector(final TypeView<?> type, final Introspector introspector) {
        this.type = type;
        this.introspector = introspector;
    }

    protected TypeView<?> type() {
        return this.type;
    }

    protected Introspector introspector() {
        return this.introspector;
    }

    @Override
    public Option<TypeParameterView> atIndex(final int index) {
        final TypeParameterList parameters = this.allInput();
        return parameters.atIndex(index);
    }

    @Override
    public TypeParameterList resolveInputFor(final Class<?> fromParentType) {
        if (this.type().is(fromParentType)) {
            return this.allInput();
        }
        else if (this.type().isChildOf(fromParentType)) {
            final TypeView<?> parentType = this.introspector().introspect(fromParentType);
            final TypeParametersIntrospector typeParameters = parentType.typeParameters();
            // No point in resolving if there are no type parameters
            if (typeParameters.count() > 0) {
                return this.tryResolveInputForParent(parentType);
            }
        }
        return new SimpleTypeParameterList(List.of());
    }

    private TypeParameterList tryResolveInputForParent(final TypeView<?> parent) {
        if (this.type.isChildOf(parent.type())) {
            final TypeHierarchyGraph typeHierarchy = this.getTypeHierarchy();
            try {
                final Graph<TypeView<?>> inverted = new GraphInverter().invertGraph(typeHierarchy, node -> node.type() == parent.type());
                final TypeParameterResolverGraphVisitor visitor = new TypeParameterResolverGraphVisitor(parent);
                visitor.iterate(inverted);
                return new SimpleTypeParameterList(visitor.parameters());
            }
            catch (final GraphException e) {
                // TypeParameterResolverGraphVisitor doesn't throw any exceptions, so this should never happen. If it does,
                // it indicates something was unexpectedly modified in the implementation.
                throw new IllegalStateException("Unexpected graph exception while resolving type parameters", e);
            }
        }
        else {
            return new SimpleTypeParameterList(List.of());
        }
    }

    private TypeHierarchyGraph getTypeHierarchy() {
        if (this.typeHierarchy == null) {
            this.typeHierarchy = TypeHierarchyGraph.of(this.type());
        }
        return this.typeHierarchy;
    }

    @Override
    public int count() {
        return this.all().count();
    }

    @Override
    public Option<TypeView<?>> at(final int index) {
        return this.atIndex(index).flatMap(TypeParameterView::resolvedType);
    }

    @Override
    public TypeParameterList all() {
        final List<TypeParameterView> allParameters = CollectionUtilities.mergeList(this.allInput().asList(), this.allOutput().asList());
        return new SimpleTypeParameterList(allParameters);
    }

    @Override
    public TypeParameterList allOutput() {
        if (this.outputParameters == null) {
            final TypeView<?> genericSuperClass = this.type().genericSuperClass();
            final List<TypeParameterView> superInput = genericSuperClass.typeParameters().allInput().asList();
            final List<TypeParameterView> interfacesInput = this.type().genericInterfaces().stream()
                    .flatMap(genericInterface -> genericInterface.typeParameters().allInput().stream())
                    .toList();

            final List<TypeParameterView> allInputParameters = CollectionUtilities.mergeList(superInput, interfacesInput);
            this.outputParameters = new SimpleTypeParameterList(allInputParameters);
        }
        return this.outputParameters;
    }

    @Override
    public TypeParameterList outputFor(final Class<?> fromParentType) {
        if (this.type().isChildOf(fromParentType)) {
            final List<TypeParameterView> consumedByParent = this.allOutput().stream()
                    .filter(parameter -> parameter.consumedBy().is(fromParentType))
                    .toList();
            return new SimpleTypeParameterList(consumedByParent);
        }
        return new SimpleTypeParameterList(List.of());
    }
}
