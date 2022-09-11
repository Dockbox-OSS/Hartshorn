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

package org.dockbox.hartshorn.util.introspect.reflect;

import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.SynchronizedMultiMap.SynchronizedArrayListMultiMap;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.TypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.introspect.view.wildcard.WildcardTypeView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectionTypeParametersIntrospector<T> implements TypeParametersIntrospector {

    private final TypeView<T> type;
    private final ParameterizedType parameterizedType;
    private final Introspector introspector;

    private List<TypeView<?>> typeParameters;
    private MultiMap<Class<?>, TypeView<?>> interfaceTypeParameters;

    public ReflectionTypeParametersIntrospector(final TypeView<T> type, final ParameterizedType parameterizedType, final Introspector introspector) {
        this.type = type;
        this.parameterizedType = parameterizedType;
        if (parameterizedType != null && parameterizedType.getRawType() != type.type()) {
            throw new IllegalArgumentException("Type " + type.qualifiedName() + " is not the raw type of " + parameterizedType.getTypeName());
        }
        this.introspector = introspector;
    }

    @Override
    public Result<TypeView<?>> at(final int index) {
        final List<TypeView<?>> parameters = this.all();
        if (parameters.size() > index) return Result.of(parameters.get(index));
        return Result.empty();
    }

    @Override
    public List<TypeView<?>> from(final Class<?> fromInterface) {
        if (!fromInterface.isInterface()) throw new IllegalArgumentException("Provided type " + fromInterface.getSimpleName() + " is not a interface");
        if (!this.type.isChildOf(fromInterface)) throw new IllegalArgumentException("Provided interface " + fromInterface.getSimpleName() + " is not a super type of " + this.type.name());

        if (this.interfaceTypeParameters == null) {
            this.interfaceTypeParameters = new SynchronizedArrayListMultiMap<>();
        }

        if (!this.interfaceTypeParameters.containsKey(fromInterface)) {
            for (final Type genericSuper : this.type.type().getGenericInterfaces()) {
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

    @Override
    public List<TypeView<?>> all() {
        if (this.typeParameters == null) {
            if (this.parameterizedType != null) {
                this.typeParameters = this.contextsFromParameterizedType(this.parameterizedType);
            }
            else {
                final Type genericSuper = this.type.type().getGenericSuperclass();
                if (genericSuper instanceof ParameterizedType parameterized) {
                    this.typeParameters = this.contextsFromParameterizedType(parameterized);
                } else {
                    this.typeParameters = List.of();
                }
            }
        }
        return this.typeParameters;
    }

    @Override
    public int count() {
        return this.all().size();
    }

    private List<TypeView<?>> contextsFromParameterizedType(final ParameterizedType parameterizedType) {
        final Type[] arguments = parameterizedType.getActualTypeArguments();

        return Arrays.stream(arguments)
                .filter(type -> type instanceof Class || type instanceof WildcardType || type instanceof ParameterizedType)
                .map(type -> {
                    if (type instanceof Class<?> clazz) return this.introspector.introspect(clazz);
                    else if (type instanceof WildcardType wildcard) {
                        if (wildcard.getUpperBounds() != null && wildcard.getUpperBounds().length > 0) {
                            return this.introspector.introspect(wildcard.getUpperBounds()[0]);
                        }
                        return new WildcardTypeView();
                    }
                    else if (type instanceof ParameterizedType parameterized) return this.introspector.introspect(parameterized);
                    else return this.introspector.introspect(Void.class);
                })
                .collect(Collectors.toList());
    }
}
