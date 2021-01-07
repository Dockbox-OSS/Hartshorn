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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.regex.Pattern;

import io.leangen.geantyref.TypeToken;


public class PatternTypeSerializer implements TypeSerializer<Pattern> {

    @Override
    public Pattern deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return Pattern.compile(Objects.requireNonNull(node.getString()));
    }

    @Override
    public void serialize(Type type, @Nullable Pattern obj, ConfigurationNode node) throws SerializationException {
        assert null != obj : "Pattern object is required";
        node.set(TypeToken.get(String.class), obj.pattern());
    }
}
