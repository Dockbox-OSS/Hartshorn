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

package org.dockbox.hartshorn.sponge.util;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.server.minecraft.dimension.Worlds;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpongeWorlds implements Worlds {

    @NotNull
    @Override
    public List<World> getLoadedWorlds() {
        return Sponge.getServer().getWorlds().stream()
                .map(SpongeConversionUtil::fromSponge)
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public List<UUID> getAllWorldUUIDs() {
        return Sponge.getServer().getAllWorldProperties().stream()
                .map(WorldProperties::getUniqueId)
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Exceptional<World> getWorld(@NotNull String name) {
        return Exceptional.of(Sponge.getServer().getWorldProperties(name)).map(SpongeConversionUtil::fromSponge);
    }

    @NotNull
    @Override
    public Exceptional<World> getWorld(@NotNull UUID uuid) {
        return Exceptional.of(Sponge.getServer().getWorldProperties(uuid)).map(SpongeConversionUtil::fromSponge);
    }

    @Override
    public boolean hasWorld(String name) {
        return Sponge.getServer().getAllWorldProperties().stream()
                .anyMatch(properties -> properties.getWorldName().equalsIgnoreCase(name));
    }

    @Override
    public boolean hasWorld(UUID uuid) {
        return Sponge.getServer().getAllWorldProperties().stream()
                .anyMatch(properties -> properties.getUniqueId().equals(uuid));
    }

    @Override
    public UUID getRootWorldId() {
        return Sponge.getServer().getDefaultWorld().map(WorldProperties::getUniqueId).orElse(HartshornUtils.EMPTY_UUID);
    }
}