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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Objects;

import jakarta.inject.Named;

public record ComponentKey<T>(Class<T> type, String name, Scope scope, boolean enable) {

    public static <T> Builder<T> builder(final Class<T> type) {
        return new Builder<>(type);
    }

    public static <T> ComponentKey<T> of(final TypeView<T> type) {
        return ComponentKey.of(type.type());
    }

    public static <T> ComponentKey<T> of(final TypeView<T> type, final String named) {
        return ComponentKey.of(type.type(), named);
    }

    public static <T> ComponentKey<T> of(final Class<T> type) {
        return ComponentKey.builder(type).build();
    }

    public static <T> ComponentKey<T> of(final Class<T> key, final String name) {
        return ComponentKey.builder(key).name(name).build();
    }

    public Builder<T> mutable() {
        return new Builder<>(this);
    }

    public ComponentKeyView<T> view() {
        return new ComponentKeyView<>(this);
    }

    @Override
    public String toString() {
        final String nameSuffix = StringUtilities.empty(this.name) ? "" : ":" + this.name;
        return "ComponentKey<" + this.type.getSimpleName() + nameSuffix + "> (" + this.scope.installableScopeType().getSimpleName() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final ComponentKey<?> that = (ComponentKey<?>) o;
        return this.enable == that.enable
                && this.type.equals(that.type)
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.scope, that.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.name, this.scope, this.enable);
    }

    public static class Builder<T> {

        private Class<T> type;
        private String name;
        private Scope scope = Scope.DEFAULT_SCOPE;
        private boolean enable = true;

        private Builder(final ComponentKey<T> key) {
            this.type = key.type;
            this.name = key.name;
            this.scope = key.scope;
            this.enable = key.enable;
        }

        private Builder(final Class<T> type) {
            this.type = type;
        }

        public Builder<T> type(final Class<T> type) {
            this.type = type;
            return this;
        }

        public Builder<T> name(final String name) {
            if (StringUtilities.empty(name)) this.name = null;
            else this.name = name;
            return this;
        }

        public Builder<T> name(final Named named) {
            if (named != null) {
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

    public static class ComponentKeyView<T> {

        private final Class<T> type;
        private final String name;

        private ComponentKeyView(final ComponentKey<T> key) {
            this.type = key.type;
            this.name = key.name;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            final ComponentKeyView<?> that = (ComponentKeyView<?>) o;
            return Objects.equals(this.type, that.type) && Objects.equals(this.name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.type, this.name);
        }
    }

}
