/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.objects.keys.data;

import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.objects.keys.PersistentDataKey;
import org.jetbrains.annotations.NonNls;

public class TypedPersistentDataKey<T> implements PersistentDataKey<T> {


    @NonNls
    private final String name;
    @NonNls
    private final String id;
    private final Extension extension;
    private final Class<T> type;

    public TypedPersistentDataKey(String name, String id, Extension extension, Class<T> type) {
        this.name = name;
        this.id = id;
        this.extension = extension;
        this.type = type;
    }

    @Override
    public Class<T> getDataType() {
        return this.type;
    }

    @Override
    public String getRegisteringExtensionId() {
        return this.extension.id();
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
        int result = this.id.hashCode();
        result = 31 * result + this.extension.hashCode();
        result = 31 * result + this.type.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypedPersistentDataKey)) return false;

        TypedPersistentDataKey<?> that = (TypedPersistentDataKey<?>) o;

        if (!this.id.equals(that.id)) return false;
        if (!this.extension.equals(that.extension)) return false;
        return this.type.equals(that.type);
    }
}
