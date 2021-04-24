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

package org.dockbox.selene.api.keys;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.OwnerLookup;
import org.dockbox.selene.api.domain.TypedOwner;
import org.dockbox.selene.di.Provider;
import org.jetbrains.annotations.NonNls;

import java.util.Objects;

public class TypedPersistentDataKey<T> implements PersistentDataKey<T> {

    @NonNls
    private final String name;
    @NonNls
    private final String id;
    private final TypedOwner owner;
    private final Class<T> type;

    public TypedPersistentDataKey(String name, String id, TypedOwner owner, Class<T> type) {
        this.name = name;
        this.id = id;
        this.owner = owner;
        this.type = type;
    }

    @Override
    public Class<T> getDataType() {
        return this.type;
    }

    @Override
    public String getOwnerId() {
        return this.owner.id();
    }

    @Override
    public String getDataKeyId() {
        return this.id;
    }

    @Override
    public String getDataKeyName() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.id, this.owner, this.type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypedPersistentDataKey)) return false;

        TypedPersistentDataKey<?> that = (TypedPersistentDataKey<?>) o;

        if (!this.id.equals(that.id)) return false;
        if (!this.owner.equals(that.owner) &&
                !this.owner.equals(Provider.provide(OwnerLookup.class).lookup(Selene.class)))
            return false;
        return this.type.equals(that.type);
    }
}
