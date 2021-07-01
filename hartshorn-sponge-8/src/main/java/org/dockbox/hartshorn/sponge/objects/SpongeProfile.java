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

package org.dockbox.hartshorn.sponge.objects;

import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.server.minecraft.players.Profile;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.property.ProfileProperty;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpongeProfile implements Profile {

    private GameProfile profile;

    @Wired
    public SpongeProfile(UUID uuid) {
        this.profile = GameProfile.of(uuid);
    }

    @Override
    public UUID getUuid() {
        return this.profile.uuid();
    }

    @Override
    public void setUuid(UUID uuid) {
        final List<ProfileProperty> properties = this.profile.properties();
        this.profile = GameProfile.of(uuid).withProperties(properties);
    }

    @Override
    public Map<String, String> properties() {
        Map<String, String> properties = HartshornUtils.emptyMap();
        for (ProfileProperty property : this.profile.properties())
            properties.put(property.name(), property.value());
        return properties;
    }

    @Override
    public void setProperty(String key, String value) {
        this.profile = this.profile.withProperty(ProfileProperty.of(key, value));
    }

    @Override
    public void setProperties(Map<String, String> properties) {
        final List<ProfileProperty> profileProperties = properties.entrySet().stream()
                .map(property -> ProfileProperty.of(property.getKey(), property.getValue()))
                .collect(Collectors.toList());
        this.profile = this.profile.withProperties(profileProperties);
    }
}
