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

package org.dockbox.hartshorn.util.introspect;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * A wrapper for parameterized types that allows for the retrieval of the type and its parameters. This is a
 * simple representation of a {@link ParameterizedType} that allows for easy comparison, which is especially
 * useful for keys such as the {@code org.dockbox.hartshorn.component.ComponentKey}.
 *
 * <p>{@link ParameterizableType}s can be introspected with {@link Introspector introspectors}, retaining
 * complete type information.
 *
 * @param <T> the type of the parameterized type
 *
 * @see TypeView
 * @see Introspector
 * @see ParameterizedType
 * @see ParameterizableParameterizedTypeWrapper
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ParameterizableType<T> {

    private final Class<T> type;
    private List<ParameterizableType<?>> parameters;

    public ParameterizableType(Class<T> type, List<ParameterizableType<?>> parameters) {
        this.type = type;
        this.parameters = parameters;
    }

    public ParameterizableType(Class<T> type) {
        this(type, List.of());
    }

    public ParameterizableType(TypeView<T> type) {
        this.type = type.type();
        this.parameters = type.typeParameters().allInput()
                .asList()
                .stream()
                .flatMap(parameter -> parameter.resolvedType().stream())
                .map(ParameterizableType::new)
                .collect(Collectors.toList());
    }

    /**
     * Sets the parameters of this type, overriding all existing parameters. The number of parameters must
     * match the number of type parameters of the type. If the type has no type parameters, the given
     * parameters must be empty.
     *
     * @param parameters the new parameters
     *
     * @throws IllegalArgumentException if the number of parameters does not match the number of type parameters
     */
    public void parameters(List<ParameterizableType<?>> parameters) {
        int expectedSize = this.type().getTypeParameters().length;
        if(parameters.size() != expectedSize) {
            throw new IllegalArgumentException("Expected " + expectedSize + " parameters, but got " + parameters.size());
        }
        this.parameters = parameters;
    }

    /**
     * Returns the type of this parameterized type. This is only the raw type, and does not include any
     * type parameters.
     *
     * @return the type of this parameterized type
     */
    public Class<T> type() {
        return this.type;
    }

    /**
     * Returns the parameters of this parameterized type. If the type has no type parameters, this list
     * will be empty, but never {@code null}.
     *
     * @return the parameters of this parameterized type
     */
    public List<ParameterizableType<?>> parameters() {
        return List.copyOf(this.parameters);
    }

    /**
     * Returns a {@link ParameterizedType} representation of this parameterized type. This can be used for
     * reflective operations.
     *
     * @return a {@link ParameterizedType} representation of this parameterized type
     */
    public ParameterizedType asParameterizedType() {
        return new ParameterizableParameterizedTypeWrapper<>(this);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ParameterizableType<?> that = (ParameterizableType<?>) o;
        return Objects.equals(this.type, that.type) && Objects.equals(this.parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.parameters);
    }

    @Override
    public String toString() {
        String parameters = this.parameters.stream()
                .map(ParameterizableType::toString)
                .collect(Collectors.joining(", "));
        return this.type.getSimpleName() + (parameters.isEmpty() ? "" : "<" + parameters + ">");
    }
}
