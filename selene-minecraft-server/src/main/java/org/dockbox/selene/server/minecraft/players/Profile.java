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

package org.dockbox.selene.server.minecraft.players;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.tuple.Tuple;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface Profile {

    static Profile of(UUID uuid) {
        return Selene.context().get(Profile.class, uuid);
    }

    static Profile of(Profile profile) {
        return Selene.context().get(Profile.class, profile);
    }

    UUID getUuid();

    void setUuid(UUID uuid);

    Map<String, Collection<Tuple<String, String>>> getProperties();

    void setProperty(String property, String key, String value);

    void setProperties(Map<String, Collection<Tuple<String, String>>> properties);
}
