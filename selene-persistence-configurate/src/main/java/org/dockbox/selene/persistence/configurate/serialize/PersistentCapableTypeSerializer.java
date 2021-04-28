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
package org.dockbox.selene.persistence.configurate.serialize;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.selene.persistence.PersistentCapable;
import org.dockbox.selene.persistence.PersistentModel;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

import io.leangen.geantyref.TypeToken;

@SuppressWarnings("AnonymousInnerClassMayBeStatic")
public class PersistentCapableTypeSerializer implements TypeSerializer<PersistentCapable<?>> {

    private final TypeToken<PersistentModel<?>> ttpm = new TypeToken<PersistentModel<?>>() {
    };

    public static final TypeToken<PersistentCapable<?>> TOKEN = new TypeToken<PersistentCapable<?>>() {
    };

    @Override
    public PersistentCapable<?> deserialize(Type type, ConfigurationNode node) throws SerializationException {
        @Nullable PersistentModel<?> list = node.get(this.ttpm);
        return list.toPersistentCapable();
    }

    @Override
    public void serialize(Type type, @Nullable PersistentCapable<?> obj, ConfigurationNode node) throws SerializationException {
        node.set(obj.toPersistentModel());
    }
}
