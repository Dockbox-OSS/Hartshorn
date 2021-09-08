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

package org.dockbox.hartshorn.di;

import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Objects;

import javax.inject.Named;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Key<C> {
    private final TypeContext<C> contract;
    private final Named named;

    public static <C> Key<C> of(final TypeContext<C> contract) {
        return of(contract, null);
    }

    public static <C> Key<C> of(final Class<C> contract) {
        return of(TypeContext.of(contract));
    }

    public static <C> Key<C> of(final TypeContext<C> contract, final Named named) {
        if (named != null && !HartshornUtils.empty(named.value())) {
            return new Key<>(contract, named);
        }
        return new Key<>(contract, null);
    }

    public static <C> Key<C> of(final Class<C> contract, final Named named) {
        return of(TypeContext.of(contract), named);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.contract, this.named);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Key<?> key)) return false;
        return this.contract.equals(key.contract) && Objects.equals(this.named, key.named);
    }
}
