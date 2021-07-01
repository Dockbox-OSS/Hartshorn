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

import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.server.minecraft.dimension.Chunk;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3i;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SpongeChunk implements Chunk, SpongeDimension {

    private final ResourceKey world;
    private final Vector3i chunk;

    @Override
    public Vector3N getPosition() {
        return SpongeConvert.fromSponge(this.chunk);
    }

    @Override
    public World getWorld() {
        return SpongeConvert.fromSponge(this.serverWorld());
    }

    @Override
    public ServerWorld serverWorld() {
        return Sponge.server().worldManager().world(this.world).orElse(null);
    }
}
