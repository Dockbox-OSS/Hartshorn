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

package org.dockbox.selene.core.impl.files.serialize;

import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.SeleneUtils;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Serializer definitions which can be (globally) registered for all types accepting {@link TypeSerializer}. Within 
 * Selene, this targets {@link org.dockbox.selene.core.impl.files.DefaultConfigurateManager}. Due to the requirement of
 * generic constraints, a static final transient {@link java.util.Collection} is used. Instances of
 * {@link SerializerInformation} are automatically registered to {@link SeleneTypeSerializers#serializerInformation},
 * and therefore do not need to be declared as fields.
 */
@SuppressWarnings("rawtypes")
public final class SeleneTypeSerializers {

    private static final TypeSerializerCollection.Builder serializerBuilder = TypeSerializerCollection.builder();

    /**
     * The transient {@link Collection} holding all known {@link SerializerInformation} instances, which can be
     * used to register the associated {@link TypeSerializer}s.
     */
    static final transient Collection<SerializerInformation<?>> serializerInformation = SeleneUtils.COLLECTION.emptyConcurrentList();

    static {
        try {
            new SerializerInformation<>(Language.class, LanguageTypeSerializer::new);
            new SerializerInformation<>(byte[].class, ByteArrayTypeSerializer::new);
            new SerializerInformation<>(int[].class, IntArrayTypeSerializer::new);
            new SerializerInformation<>(short[].class, ShortArrayTypeSerializer::new);
            new SerializerInformation<>(Pattern.class, PatternTypeSerializer::new);
        } catch (Exception e) {
            Selene.handle("Failed to initialize serializer information", e);
        }
    }

    private SeleneTypeSerializers() {}

    /**
     * Registers a new {@link SerializerInformation} instance, if the instance was already known nothing happens.
     *
     * @param information
     *         The {@link SerializerInformation} to register.
     */
    static void addSerializerInformation(SerializerInformation<?> information) {
        serializerInformation.add(information);
    }

    /**
     * Registers all known {@link SerializerInformation} instances using their associated
     * {@link java.util.function.BiConsumer}. Usually this means registering the
     * {@link TypeSerializer} globally using {@link TypeSerializerCollection.Builder#register(Class, TypeSerializer)}
     */
    @SuppressWarnings("unchecked")
    private static void registerTypeSerializers() {
        for (SerializerInformation serializer : serializerInformation) {
            serializerBuilder.register(serializer.getType(), serializer.getTypeSerializer());
        }
    }

    public static TypeSerializerCollection collection() {
        registerTypeSerializers();
        return serializerBuilder.build();
    }

}
