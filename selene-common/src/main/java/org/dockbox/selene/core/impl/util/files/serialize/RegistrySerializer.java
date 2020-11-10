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

package org.dockbox.selene.core.impl.util.files.serialize;

import com.google.common.reflect.TypeToken;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.selene.core.impl.objects.registry.Registry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class RegistrySerializer implements TypeSerializer<Registry<?>> {

    public Registry<?> deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode node) throws ObjectMappingException {
        try {
            byte[] ls = node.getValue(new TypeToken<byte[]>() {});
            ByteArrayInputStream bais = new ByteArrayInputStream(ls);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Registry<?>) ois.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new ObjectMappingException(e);
        }
    }

    public void serialize(@NonNull TypeToken<?> type, @Nullable Registry<?> obj, @NonNull ConfigurationNode node) throws ObjectMappingException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            node.setValue(new TypeToken<byte[]>() {
            }, baos.toByteArray());
        } catch (IOException e) {
            throw new ObjectMappingException(e);
        }
    }
}
