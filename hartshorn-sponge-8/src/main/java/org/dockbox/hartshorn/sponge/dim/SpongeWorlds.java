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
    public List<World> loadedWorlds() {
        return Sponge.server().worldManager().worlds().stream()
                .map(SpongeConvert::fromSponge)
                .map(World.class::cast)
                .toList();
    }

    @Override
    public List<UUID> loadedUniqueIds() {
        return this.worlds().stream().map(Identifiable::uniqueId).toList();
    }

    @Override
    public Exceptional<World> world(String name) {
        return Exceptional.of(this.worlds().stream()
                .filter(world -> world.key().value().equals(name)).findFirst())
                .map(SpongeConvert::fromSponge);
    }

    @Override
    public Exceptional<World> world(UUID uuid) {
        return Exceptional.of(this.worlds().stream()
                .filter(world -> world.uniqueId().equals(uuid)).findFirst())
                .map(SpongeConvert::fromSponge);
    }

    @Override
    public boolean has(String name) {
        return this.world(name).present();
    }

    @Override
    public boolean has(UUID uuid) {
        return this.world(uuid).present();
    }

    @Override
    public UUID rootUniqueId() {
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
