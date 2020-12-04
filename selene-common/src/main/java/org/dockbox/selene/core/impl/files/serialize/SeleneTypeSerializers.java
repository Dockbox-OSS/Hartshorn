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

import com.google.common.reflect.TypeToken;

import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.SeleneUtils;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

/**
 Serializer definitions which can be (globally) registered for all types using
 {@link ninja.leaping.configurate.objectmapping.serialize.TypeSerializers}. Within Selene, this targets
 {@link org.dockbox.selene.core.files.ConfigurateManager}.
 Due to the requirement of generic constraints, a static final transient {@link java.util.Collection} is used.
 Insances of {@link SerializerInformation} are automatically registered to {@link #serializerInformation}, and
 therefore do not need to be declared as fields.
 {@link #registerTypeSerializers()} registers all known serializers using their associated
 {@link java.util.function.BiConsumer} as seen in {@link SerializerInformation#getConsumer()}.
 */
@SuppressWarnings("rawtypes")
public final class SeleneTypeSerializers {

    /**
     The transient {@link Collection} holding all known {@link SerializerInformation} instances, which can be
     used to register the associated {@link ninja.leaping.configurate.objectmapping.serialize.TypeSerializer}s.
     */
    static final transient Collection<SerializerInformation<?>> serializerInformation = SeleneUtils.emptyConcurrentList();

    static {
        try {
            new SerializerInformation<>(Language.class, LanguageTypeSerializer::new);
            new SerializerInformation<>(byte[].class, ByteArrayTypeSerializer::new);
            new SerializerInformation<>(int[].class, IntArrayTypeSerializer::new);
            new SerializerInformation<>(short[].class, ShortArrayTypeSerializer::new);
            new SerializerInformation<>(Pattern.class, PatternTypeSerializer::new);
            new PredicateSerializerInformation<>(Set.class, SetTypeSerializer::new, new TypeToken<Set<?>>() {
            });
        } catch (Exception e) {
            Selene.getServer().except("Failed to initialize serializer information", e);
        }
    }

    /**
     Registers a new {@link SerializerInformation} instance, if the instance was already known nothing happens.

     @param information
     The {@link SerializerInformation} to register.
     */
    static void addSerializerInformation(SerializerInformation<?> information) {
        serializerInformation.add(information);
    }

    /**
     Registers all known {@link SerializerInformation} instances using their associated
     {@link java.util.function.BiConsumer}. Usually this means registering the
     {@link ninja.leaping.configurate.objectmapping.serialize.TypeSerializer} globally using
     {@link TypeSerializers#getDefaultSerializers()}'s {@link TypeSerializerCollection#registerType(TypeToken, TypeSerializer)}
     (in case of 'raw' types) or {@link TypeSerializerCollection#registerPredicate(Predicate, TypeSerializer)} (in case
     of generic types).
     */
    public static void registerTypeSerializers() {
        for (SerializerInformation serializer : serializerInformation) {
            serializer.getConsumer().accept(serializer.getTypeToken(), serializer.getTypeSerializer());
        }
    }

    /**
     Registers all known {@link SerializerInformation} instances using their associated
     {@link java.util.function.BiConsumer}. Usually this targets specific
     {@link ninja.leaping.configurate.objectmapping.ObjectMapper}s.

     @param tsc
     The collection to apply the serializers to, usually targets either {@link TypeSerializers#getDefaultSerializers()}
     (global) or a specific {@link ninja.leaping.configurate.objectmapping.ObjectMapper}'s collection.
     */
    public static void registerTypeSerializers(TypeSerializerCollection tsc) {
        for (SerializerInformation serializer : serializerInformation) {
            serializer.getConsumer(tsc).accept(serializer.getTypeToken(), serializer.getTypeSerializer());
        }
    }

}
