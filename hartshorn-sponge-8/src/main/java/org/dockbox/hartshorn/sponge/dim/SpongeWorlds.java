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

package org.dockbox.hartshorn.sponge.dim;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.server.minecraft.dimension.Worlds;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.dockbox.hartshorn.sponge.util.SpongeUtil;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.util.Identifiable;
import org.spongepowered.api.world.server.storage.ServerWorldProperties;

import java.util.List;
import java.util.UUID;

public class SpongeWorlds implements Worlds {

    @Override
    public List<World> getLoadedWorlds() {
        return Sponge.server().worldManager().worlds().stream()
                .map(SpongeConvert::fromSponge)
                .map(World.class::cast)
                .toList();
    }

    @Override
    public List<UUID> getAllWorldUUIDs() {
        return this.worlds().stream().map(Identifiable::uniqueId).toList();
    }

    @Override
    public Exceptional<World> getWorld(String name) {
        return Exceptional.of(this.worlds().stream()
                .filter(world -> world.key().value().equals(name)).findFirst())
                .map(SpongeConvert::fromSponge);
    }

    @Override
    public Exceptional<World> getWorld(UUID uuid) {
        return Exceptional.of(this.worlds().stream()
                .filter(world -> world.uniqueId().equals(uuid)).findFirst())
                .map(SpongeConvert::fromSponge);
    }

    @Override
    public boolean hasWorld(String name) {
        return this.getWorld(name).present();
    }

    @Override
    public boolean hasWorld(UUID uuid) {
        return this.getWorld(uuid).present();
    }

    @Override
    public UUID getRootWorldId() {
        return Sponge.server().worldManager().defaultWorld().uniqueId();
    }

    private List<ServerWorldProperties> worlds() {
        List<ServerWorldProperties> uuids = HartshornUtils.emptyList();
        for (ResourceKey worldKey : Sponge.server().worldManager().worldKeys()) {
            final Exceptional<ServerWorldProperties> properties = SpongeUtil.awaitOption(Sponge.server().worldManager().loadProperties(worldKey));
            properties.present(uuids::add);
        }
        return HartshornUtils.asUnmodifiableList(uuids);
    }
}
