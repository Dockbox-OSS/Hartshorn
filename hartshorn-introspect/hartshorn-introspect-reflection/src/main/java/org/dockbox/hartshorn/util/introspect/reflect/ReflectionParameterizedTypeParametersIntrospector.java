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

import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.SynchronizedMultiMap.SynchronizedArrayListMultiMap;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.reflect.view.ReflectionTypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.introspect.view.wildcard.WildcardTypeView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectionParameterizedTypeParametersIntrospector<T> extends AbstractReflectionTypeParametersIntrospector {

    private final ParameterizedType parameterizedType;

    private MultiMap<Class<?>, TypeView<?>> interfaceTypeParameters;
    private List<TypeParameterView> parameters;

    public ReflectionParameterizedTypeParametersIntrospector(final TypeView<T> type, final ParameterizedType parameterizedType, final Introspector introspector) {
        super(type, introspector);
        this.parameterizedType = parameterizedType;
        if (parameterizedType != null && parameterizedType.getRawType() != type.type()) {
            throw new IllegalArgumentException("Type " + type.qualifiedName() + " is not the raw type of " + parameterizedType.getTypeName());
        }
    }

    @Override
    public List<TypeParameterView> allInput() {
        if (this.parameters == null) {
            final Type[] actualTypeArguments = this.parameterizedType.getActualTypeArguments();
            this.parameters = Arrays.stream(actualTypeArguments)
                    .map(argument -> new ReflectionTypeParameterView(argument, this.type(), this.introspector()))
                    .collect(Collectors.toList());
        }
        return this.parameters;
    }

    @Override
    @Deprecated(forRemoval = true, since = "23.1")
    public List<TypeView<?>> from(final Class<?> fromInterface) {
        if (!fromInterface.isInterface()) throw new IllegalArgumentException("Provided type " + fromInterface.getSimpleName() + " is not a interface");
        if (!this.type().isChildOf(fromInterface)) throw new IllegalArgumentException("Provided interface " + fromInterface.getSimpleName() + " is not a super type of " + this.type().name());

        if (this.interfaceTypeParameters == null) {
            this.interfaceTypeParameters = new SynchronizedArrayListMultiMap<>();
        }

        if (!this.interfaceTypeParameters.containsKey(fromInterface)) {
            for (final Type genericSuper : this.type().type().getGenericInterfaces()) {
                if (genericSuper instanceof ParameterizedType parameterized) {
                    final Type raw = parameterized.getRawType();
                    if (raw instanceof Class<?> clazz && fromInterface.equals(clazz)) {
                        this.interfaceTypeParameters.putAll(fromInterface, this.contextsFromParameterizedType(parameterized));
                    }
                }
            }
        }

        return List.copyOf(this.interfaceTypeParameters.get(fromInterface));
    }

    private List<TypeView<?>> contextsFromParameterizedType(final ParameterizedType parameterizedType) {
        final Type[] arguments = parameterizedType.getActualTypeArguments();

        return Arrays.stream(arguments)
                .filter(type -> type instanceof Class || type instanceof WildcardType || type instanceof ParameterizedType)
                .map(type -> {
                    if (type instanceof Class<?> clazz) return this.introspector().introspect(clazz);
                    else if (type instanceof WildcardType wildcard) {
                        if (wildcard.getUpperBounds() != null && wildcard.getUpperBounds().length > 0) {
                            return this.introspector().introspect(wildcard.getUpperBounds()[0]);
                        }
                        return new WildcardTypeView();
                    }
                    else if (type instanceof ParameterizedType parameterized) return this.introspector().introspect(parameterized);
                    else return this.introspector().introspect(Void.class);
                })
                .collect(Collectors.toList());
    }
}
