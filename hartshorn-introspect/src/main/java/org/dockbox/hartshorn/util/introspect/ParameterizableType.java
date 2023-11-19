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
import java.util.Set;
import java.util.stream.Collectors;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A wrapper for parameterized types that allows for the retrieval of the type and its parameters. This is a
 * simple representation of a {@link ParameterizedType} that allows for easy comparison, which is especially
 * useful for keys such as the {@code org.dockbox.hartshorn.component.ComponentKey}.
 *
 * <p>{@link ParameterizableType}s can be introspected with {@link Introspector introspectors}, retaining
 * complete type information.
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
public final class ParameterizableType {

    private final Class<?> type;
    private final List<ParameterizableType> parameters;

    private ParameterizableType(Class<?> type, List<ParameterizableType> parameters) {
        this.type = type;
        this.parameters = parameters;
    }

    public static ParameterizableType create(Class<?> type) {
        return builder(type).build();
    }

    public static ParameterizableType create(TypeView<?> type) {
        return builder(type).build();
    }

    public static Builder builder(Class<?> type) {
        return new Builder(type);
    }

    public static Builder builder(TypeView<?> type) {
        List<ParameterizableType> parameters = type.typeParameters()
            .allInput()
            .asList()
            .stream()
            .flatMap(parameter -> parameter.resolvedType()
                .filter(typeView -> !typeView.isWildcard())
                .orComputeFlat(() -> {
                    Set<TypeView<?>> bounds = parameter.upperBounds();
                    if (bounds.size() == 1) {
                        return Option.of(bounds.iterator().next());
                    }
                    return Option.empty();
                }).map(ParameterizableType::create)
                .orCompute(() -> ParameterizableType.create(Object.class))
                .stream()
            )
            .collect(Collectors.toList());
        return new Builder(type.type()).parameters(parameters);
    }

    /**
     * Returns the type of this parameterized type. This is only the raw type, and does not include any
     * type parameters.
     *
     * @return the type of this parameterized type
     */
    public Class<?> type() {
        return this.type;
    }

    /**
     * Returns the parameters of this parameterized type. If the type has no type parameters, this list
     * will be empty, but never {@code null}.
     *
     * @return the parameters of this parameterized type
     */
    public List<ParameterizableType> parameters() {
        return List.copyOf(this.parameters);
    }

    /**
     * Returns a {@link ParameterizedType} representation of this parameterized type. This can be used for
     * reflective operations.
     *
     * @return a {@link ParameterizedType} representation of this parameterized type
     */
    public ParameterizedType asParameterizedType() {
        return new ParameterizableParameterizedTypeWrapper(this);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ParameterizableType that = (ParameterizableType) o;
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

    public static class Builder {

        private final Class<?> type;
        private List<ParameterizableType> parameters = List.of();

        public Builder(Class<?> type) {
            this.type = type;
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
        public Builder parameters(List<ParameterizableType> parameters) {
            int expectedSize = this.type.getTypeParameters().length;
            if(parameters.size() != expectedSize) {
                throw new IllegalArgumentException("Expected " + expectedSize + " parameters, but got " + parameters.size());
            }
            this.parameters = parameters;
            return this;
        }

        public Builder parameters(ParameterizableType... parameters) {
            return this.parameters(List.of(parameters));
        }

        public ParameterizableType build() {
            return new ParameterizableType(this.type, this.parameters);
        }
    }
}
