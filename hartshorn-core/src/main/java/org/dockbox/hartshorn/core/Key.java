/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.binding.NamedImpl;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import java.util.Objects;

import javax.inject.Named;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Key<C> {
    private final TypeContext<C> type;
    private final Named name;

    public static <C> Key<C> of(final TypeContext<C> type) {
        return new Key<>(type, null);
    }

    public static <C> Key<C> of(final Class<C> type) {
        return of(TypeContext.of(type));
    }

    public static <C> Key<C> of(final TypeContext<C> type, final Named name) {
        if (name != null && !HartshornUtils.empty(name.value())) {
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
        return Key.of(this.type(), name);
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
