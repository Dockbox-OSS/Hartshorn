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
package org.dockbox.selene.core.impl.files.serialize;

import com.google.common.primitives.Shorts;
import com.google.common.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class ShortArrayTypeSerializer implements TypeSerializer<short[]> {

    private final TypeToken<Short> ttb = new TypeToken<Short>() {};
    private final TypeToken<List<Short>> ttlb = new TypeToken<List<Short>>() {};

    @Override public short[] deserialize(@NotNull TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        List<Short> list = value.getList(this.ttb);
        return Shorts.toArray(list);
    }

    @Override public void serialize(@NotNull TypeToken<?> type, short[] obj, ConfigurationNode value) throws ObjectMappingException {
        List<Short> bytes = Shorts.asList(obj);
        value.setValue(this.ttlb, bytes);
    }
}
