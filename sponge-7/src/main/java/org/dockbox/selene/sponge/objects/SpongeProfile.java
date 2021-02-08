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

package org.dockbox.selene.sponge.objects;

import com.google.common.collect.Multimap;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import org.dockbox.selene.api.objects.profile.Profile;
import org.dockbox.selene.api.objects.tuple.Tuple;
import org.dockbox.selene.api.util.SeleneUtils;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.property.ProfileProperty;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpongeProfile implements Profile
{

    private GameProfile gameProfile;

    @AssistedInject
    public SpongeProfile(@Assisted UUID uuid)
    {
        this.gameProfile = GameProfile.of(uuid);
    }

    @AssistedInject
    public SpongeProfile(@Assisted Profile initialValue)
    {
        if (initialValue instanceof SpongeProfile)
            this.gameProfile = ((SpongeProfile) initialValue).getGameProfile();
        else throw new IllegalStateException("Cannot convert [" + initialValue.getClass().getCanonicalName() + "] to SpongeProfile");
    }

    public GameProfile getGameProfile()
    {
        return this.gameProfile;
    }

    // Internal usage only
    public SpongeProfile(GameProfile gameProfile)
    {
        this.gameProfile = gameProfile;
    }

    @Override
    public UUID getUuid()
    {
        return this.gameProfile.getUniqueId();
    }

    @Override
    public void setUuid(UUID uuid)
    {
        Multimap<String, ProfileProperty> properties = this.gameProfile.getPropertyMap();
        this.gameProfile = GameProfile.of(uuid);
        properties.asMap().forEach((key, propertyCollection) ->
                propertyCollection
                        .forEach(property -> this.gameProfile.addProperty(key, property))
        );
    }

    @Override
    public Map<String, Collection<Tuple<String, String>>> getAdditionalProperties()
    {
        Map<String, Collection<ProfileProperty>> properties = this.gameProfile.getPropertyMap().asMap();
        Map<String, Collection<Tuple<String, String>>> convertedProperties = SeleneUtils.emptyMap();
        properties.forEach((key, propertyCollection) -> {
            List<Tuple<String, String>> collection = propertyCollection.stream()
                    .map(property -> new Tuple<>(property.getName(), property.getValue()))
                    .collect(Collectors.toList());
            convertedProperties.put(key, collection);
        });
        return SeleneUtils.asUnmodifiableMap(convertedProperties);
    }

    @Override
    public void setProperty(String property, String key, String value)
    {
        this.gameProfile.getPropertyMap().put(property, ProfileProperty.of(key, value));
    }

    @Override
    public void setProperties(Map<String, Collection<Tuple<String, String>>> properties)
    {
        properties.forEach((name, propertyCollection) ->
                propertyCollection.forEach(property ->
                        this.setProperty(name, property.getKey(), property.getValue())
                ));
    }
}
