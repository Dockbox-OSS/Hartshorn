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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.selene.core.i18n.common.Language;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 The type Language type serializer.
 */
public class LanguageTypeSerializer implements TypeSerializer<Language> {

    @Override
    public Language deserialize(Type type, ConfigurationNode node) throws SerializationException {
        try {
            String code = node.node("code").getString();
            if (null == code) throw new SerializationException("Missing code attribute");
            return Language.valueOf(code.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public void serialize(Type type, @Nullable Language obj, ConfigurationNode node) throws SerializationException {
        if (null == obj) throw new SerializationException("Language cannot be null");
        node.node("code").set(obj.getCode());
    }
}
