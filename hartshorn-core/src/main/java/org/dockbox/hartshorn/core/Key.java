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

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.binding.NamedImpl;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import java.util.Objects;

import javax.inject.Named;

public class Key<C> {

    private final TypeContext<C> type;
    private final Named name;

    public Key(final TypeContext<C> type, final Named name) {
        this.type = type;
        this.name = name;
    }

    public TypeContext<C> type() {
        return this.type;
    }

    public Named name() {
        return this.name;
    }

    public static <C> Key<C> of(final TypeContext<C> type) {
        return new Key<>(type, null);
    }

    public static <C> Key<C> of(final Class<C> type) {
        return of(TypeContext.of(type));
    }

    public static <C> Key<C> of(final TypeContext<C> type, final Named name) {
        if (name != null && !StringUtilities.empty(name.value())) {
            return new Key<>(type, name);
        }
        return new Key<>(type, null);
    }

    public static <C> Key<C> of(final Class<C> type, final Named name) {
        return of(TypeContext.of(type), name);
    }

    public static <C> Key<C> of(final Class<C> type, final String name) {
        return of(TypeContext.of(type), new NamedImpl(name));
    }

    public static <C> Key<C> of(final TypeContext<C> type, final String name) {
        return of(type, new NamedImpl(name));
    }

    public Key<C> name(final String name) {
        return of(this.type(), name);
    }

    public Key<C> name(final Named name) {
        return of(this.type(), name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.name);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Key<?> key)) return false;
        return this.type.equals(key.type) && Objects.equals(this.name, key.name);
    }

    @Override
    public String toString() {
        if (this.name == null) return "Key<" + this.type.name() + ">";
        else return "Key<" + this.type.name() + ", " + this.name.value() + ">";
    }
}
