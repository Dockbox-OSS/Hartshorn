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

/*
 * This file is part of Neutrino, licensed under the MIT License (MIT).
 */
package org.dockbox.selene.core.impl.files.serialize;

import com.google.common.primitives.Shorts;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.List;

import io.leangen.geantyref.TypeToken;

@SuppressWarnings("AnonymousInnerClassMayBeStatic")
public class ShortArrayTypeSerializer implements TypeSerializer<short[]>
{

    private final TypeToken<Short> ttb = new TypeToken<Short>()
    {
    };
    private final TypeToken<List<Short>> ttlb = new TypeToken<List<Short>>()
    {
    };

    @Override
    public short[] deserialize(Type type, ConfigurationNode node)
            throws SerializationException
    {
        List<Short> list = node.getList(this.ttb);
        return Shorts.toArray(list);
    }

    @Override
    public void serialize(Type type, short @Nullable [] obj, ConfigurationNode node)
            throws SerializationException
    {
        List<Short> bytes = Shorts.asList(obj);
        node.set(this.ttlb, bytes);
    }
}
