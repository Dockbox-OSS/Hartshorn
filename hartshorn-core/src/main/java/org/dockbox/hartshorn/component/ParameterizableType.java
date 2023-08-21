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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ParameterizableType<T> {

    private final Class<T> type;
    private List<ParameterizableType<?>> parameters;

    public ParameterizableType(final Class<T> type, final List<ParameterizableType<?>> parameters) {
        this.type = type;
        this.parameters = parameters;
    }

    public ParameterizableType(final Class<T> type) {
        this(type, List.of());
    }

    public ParameterizableType(final TypeView<T> type) {
        this.type = type.type();
        this.parameters = type.typeParameters().allInput()
                .asList()
                .stream()
                .flatMap(parameter -> parameter.resolvedType().stream())
                .map(ParameterizableType::new)
                .collect(Collectors.toList());
    }

    public void parameters(final List<ParameterizableType<?>> parameters) {
        this.parameters = parameters;
    }

    public Class<T> type() {
        return this.type;
    }

    public List<ParameterizableType<?>> parameters() {
        return this.parameters;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ParameterizableType<?> that = (ParameterizableType<?>) o;
        return Objects.equals(this.type, that.type) && Objects.equals(this.parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.parameters);
    }

    @Override
    public String toString() {
        final String parameters = this.parameters.stream()
                .map(ParameterizableType::toString)
                .collect(Collectors.joining(", "));
        return this.type.getSimpleName() + (parameters.isEmpty() ? "" : "<" + parameters + ">");
    }

}
