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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.selene.core.i18n.common.Language;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

/**
 The type Language type serializer.
 */
public class LanguageTypeSerializer implements TypeSerializer<Language> {

    @Override
    public @Nullable Language deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        try {
            String code = value.getNode("code").getString();
            if (null == code) throw new ObjectMappingException("Missing code attribute");
            return Language.valueOf(code.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new ObjectMappingException("Invalid language code", e);
        }
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable Language obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        if (null == obj) throw new ObjectMappingException("Language cannot be null");
        value.getNode("code").setValue(obj.getCode());
    }
}
