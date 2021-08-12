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

package org.dockbox.hartshorn.test.objects;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.di.annotations.inject.Bound;
import org.dockbox.hartshorn.server.minecraft.players.Profile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class JUnitProfile implements Profile {

    @Getter @Setter private UUID uniqueId;
    private Map<String, String> properties;

    @Bound
    public JUnitProfile(Profile profile) {
        this(profile.uniqueId());
        if (profile instanceof JUnitProfile) this.properties = new HashMap<>(((JUnitProfile) profile).properties);
        else Hartshorn.log().warn("Could not copy profile properties as the provided profile is not an instance of JUnitProfile");
    }

    @Bound
    public JUnitProfile(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public Map<String, String> properties() {
        return this.properties;
    }

    @Override
    public void property(String key, String value) {
        this.properties.put(key, value);
    }

    @Override
    public JUnitProfile properties(Map<String, String> properties) {
        this.properties.putAll(properties);
        return this;
    }
}
