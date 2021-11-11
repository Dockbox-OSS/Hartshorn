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

package org.dockbox.hartshorn.core.keys;

import org.dockbox.hartshorn.core.domain.TypedOwner;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.jetbrains.annotations.NonNls;

import java.util.Objects;
import java.util.function.Function;

import lombok.Getter;

@Getter
public class TypedPersistentDataKey<T> implements PersistentDataKey<T> {

    @NonNls
    private final String id;
    private final Function<ApplicationContext, TypedOwner> owner;
    private final TypeContext<T> type;

    public TypedPersistentDataKey(final String id, final Function<ApplicationContext, TypedOwner> owner, final TypeContext<T> type) {
        this.id = id;
        this.owner = owner;
        this.type = type;
    }

    @Override
    public String ownerId(final ApplicationContext context) {
        return this.owner.apply(context).id();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.owner, this.type);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof TypedPersistentDataKey<?> that)) return false;

        if (!this.id.equals(that.id)) return false;
        return this.type.equals(that.type);
    }
}
