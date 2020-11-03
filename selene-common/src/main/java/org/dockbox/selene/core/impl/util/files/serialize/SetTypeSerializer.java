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

/*
 * This file is part of Neutrino, licensed under the MIT License (MIT).
 */
package org.dockbox.selene.core.impl.util.files.serialize;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class SetTypeSerializer implements TypeSerializer<Set<?>> {

    @Override
    public Set<?> deserialize(@NotNull TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        return new HashSet<>(value.getList(this.getInnerToken(type)));
    }

    @Override
    public void serialize(@NotNull TypeToken<?> type, Set<?> obj, ConfigurationNode value) throws ObjectMappingException {
        value.setValue(this.getListTokenFromSet(type), new ArrayList<>(obj));
    }

    private TypeToken<?> getInnerToken(TypeToken<?> type) {
        return type.resolveType(Set.class.getTypeParameters()[0]);
    }

    @SuppressWarnings("unchecked")
    private <E> TypeToken<List<E>> getListTokenFromSet(TypeToken<?> type) {
        // Get the inner type out of the type token
        TypeToken<?> innerType = this.getInnerToken(type);

        // Put it into the new list token
        return new TypeToken<List<E>>() {}.where(new TypeParameter<E>() {}, (TypeToken<E>)innerType);
    }
}
