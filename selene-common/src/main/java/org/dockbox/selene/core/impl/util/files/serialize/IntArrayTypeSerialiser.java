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

import com.google.common.primitives.Ints;
import com.google.common.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class IntArrayTypeSerialiser implements TypeSerializer<int[]> {

    private final TypeToken<Integer> ttb = new TypeToken<Integer>() {};
    private final TypeToken<List<Integer>> ttlb = new TypeToken<List<Integer>>() {};

    @Override public int[] deserialize(@NotNull TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        List<Integer> list = value.getList(this.ttb);
        return Ints.toArray(list);
    }

    @Override public void serialize(@NotNull TypeToken<?> type, int[] obj, ConfigurationNode value) throws ObjectMappingException {
        List<Integer> bytes = Ints.asList(obj);
        value.setValue(ttlb, bytes);
    }
}
