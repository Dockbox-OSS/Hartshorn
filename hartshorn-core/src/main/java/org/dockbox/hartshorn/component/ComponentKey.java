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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.dockbox.hartshorn.inject.Enable;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import jakarta.inject.Named;

public final class ComponentKey<T> {

    private final ParameterizableType<T> type;
    private final String name;
    private final Scope scope;
    private final boolean enable;

    private ComponentKey(ParameterizableType<T> type, String name, Scope scope, boolean enable) {
        this.type = type;
        this.name = name;
        this.scope = scope;
        this.enable = enable;
    }

    public static <T> Builder<T> builder(final Class<T> type) {
        return new Builder<>(new ParameterizableType<>(type));
    }

    public static <T> Builder<T> builder(final TypeView<T> type) {
        return new Builder<>(new ParameterizableType<>(type));
    }

    public static <T> Builder<T> builder(final ParameterizableType<T> type) {
        return new Builder<>(type);
    }

    public static <T> Builder<T> builder(ParameterView<T> parameter) {
        Builder<T> builder = builder(parameter.genericType());
        ElementAnnotationsIntrospector annotations = parameter.annotations();
        annotations.get(Named.class).peek(builder::name);
        annotations.get(Enable.class).peek(enable -> builder.enable(enable.value()));
        return builder;
    }

    public static <T> ComponentKey<T> of(final Class<T> type) {
        return ComponentKey.builder(type).build();
    }

    public static <T> ComponentKey<T> of(final TypeView<T> type) {
        return ComponentKey.builder(type).build();
    }

    public static <T> ComponentKey<T> of(final ParameterizableType<T> type) {
        return ComponentKey.builder(type).build();
    }

    public static <T> ComponentKey<T> of(final TypeView<T> type, final String named) {
        return ComponentKey.of(type.type(), named);
    }

    public static <T> ComponentKey<T> of(final Class<T> key, final String name) {
        return ComponentKey.builder(key).name(name).build();
    }

    public static <T> ComponentKey<T> of(final ParameterView<T> parameter) {
        return ComponentKey.builder(parameter).build();
    }

    public Builder<T> mutable() {
        return new Builder<>(this);
    }

    public ComponentKeyView<T> view() {
        return new ComponentKeyView<>(this);
    }

    public String qualifiedName(boolean qualifyType) {
        String nameSuffix = StringUtilities.empty(this.name) ? "" : ":" + this.name;
        String scopeName = this.scope.installableScopeType().getSimpleName();
        String typeName = qualifyType ? this.type.type().getCanonicalName() : this.type.type().getSimpleName();
        return typeName + nameSuffix + " @ " + scopeName;
    }

    @Override
    public String toString() {
        return "ComponentKey<" + this.qualifiedName(false) + ">";
    }

    @Override
    public boolean equals(final Object other) {
        if(this == other) {
            return true;
        }
        if(other == null || this.getClass() != other.getClass()) {
            return false;
        }
        final ComponentKey<?> otherComponentKey = (ComponentKey<?>) other;
        return this.enable == otherComponentKey.enable
                && this.type.equals(otherComponentKey.type)
                && Objects.equals(this.name, otherComponentKey.name)
                && Objects.equals(this.scope, otherComponentKey.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.name, this.scope, this.enable);
    }

    public Class<T> type() {
        return type.type();
    }

    public ParameterizableType<T> parameterizedType() {
        return this.type;
    }

    public String name() {
        return name;
    }

    public Scope scope() {
        return scope;
    }

    public boolean enable() {
        return enable;
    }

    public static final class Builder<T> {

        private ParameterizableType<T> type;
        private String name;
        private Scope scope = Scope.DEFAULT_SCOPE;
        private boolean enable = true;

        private Builder(final ComponentKey<T> key) {
            this.type = key.type;
            this.name = key.name;
            this.scope = key.scope;
            this.enable = key.enable;
        }

        private Builder(final ParameterizableType<T> type) {
            this.type = type;
        }

        public <U> Builder<U> type(final Class<U> type) {
            return this.type(new ParameterizableType<>(type));
        }

        public <U> Builder<U> type(final TypeView<U> type) {
            return this.type(new ParameterizableType<>(type));
        }

        public <U> Builder<U> type(final ParameterizableType<U> type) {
            return builder(type)
                    .name(name)
                    .scope(scope)
                    .enable(enable);
        }

        public Builder<T> parameterClasses(final Class<?>... parameterTypes) {
            return this.parameterClasses(List.of(parameterTypes));
        }

        public Builder<T> parameterClasses(final List<Class<?>> parameterTypes) {
            List<ParameterizableType<?>> types = new ArrayList<>();
            for (Class<?> parameterType : parameterTypes) {
                types.add(new ParameterizableType<>(parameterType));
            }
            return this.parameterTypes(types);
        }

        public Builder<T> parameterTypes(final ParameterizableType<?>... parameterTypes) {
            return this.parameterTypes(List.of(parameterTypes));
        }

        public Builder<T> parameterTypes(final List<ParameterizableType<?>> parameterTypes) {
            this.type.parameters(parameterTypes);
            return this;
        }

        public Builder<T> name(final String name) {
            this.name = StringUtilities.nullIfEmpty(name);
            return this;
        }

        public Builder<T> name(final Named named) {
            if(named != null) {
                return this.name(named.value());
            }
            return this;
        }

        public Builder<T> scope(final Scope scope) {
            this.scope = scope;
            return this;
        }

        public Builder<T> enable(final boolean enable) {
            this.enable = enable;
            return this;
        }

        public ComponentKey<T> build() {
            return new ComponentKey<>(this.type, this.name, this.scope, this.enable);
        }
    }

    public static final class ComponentKeyView<T> {

        private final ParameterizableType<T> type;
        private final String name;

        private ComponentKeyView(final ComponentKey<T> key) {
            this.type = key.type;
            this.name = key.name;
        }

        @Override
        public boolean equals(final Object other) {
            if(this == other) {
                return true;
            }
            if(other == null || this.getClass() != other.getClass()) {
                return false;
            }
            final ComponentKeyView<?> otherKeyView = (ComponentKeyView<?>) other;
            return Objects.equals(this.type, otherKeyView.type) && Objects.equals(this.name, otherKeyView.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.type, this.name);
        }
    }

}
