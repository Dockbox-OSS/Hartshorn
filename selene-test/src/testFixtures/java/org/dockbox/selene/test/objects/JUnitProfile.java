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

package org.dockbox.selene.test.objects;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.tuple.Tuple;
import org.dockbox.selene.di.annotations.Wired;
import org.dockbox.selene.server.minecraft.players.Profile;
import org.dockbox.selene.util.SeleneUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class JUnitProfile implements Profile {

    @Getter
    @Setter
    private UUID uuid;
    @Getter
    private Map<String, Collection<Tuple<String, String>>> properties = SeleneUtils.emptyMap();

    @Wired
    public JUnitProfile(UUID uuid) {
        this.uuid = uuid;
    }

    @Wired
    public JUnitProfile(Profile profile) {
        this(profile.getUuid());
        if (profile instanceof JUnitProfile) this.properties = new HashMap<>(((JUnitProfile) profile).properties);
        else Selene.log().warn("Could not copy profile properties as the provided profile is not an instance of JUnitProfile");
    }

    @Override
    public void setProperty(String property, String key, String value) {
        this.properties.put(property, SeleneUtils.asList(new Tuple<>(key, value)));
    }

    @Override
    public void setProperties(Map<String, Collection<Tuple<String, String>>> properties) {
        this.properties = new HashMap<>(properties);
    }
}
