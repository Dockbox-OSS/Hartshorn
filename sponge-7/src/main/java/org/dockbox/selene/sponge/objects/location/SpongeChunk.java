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

package org.dockbox.selene.sponge.objects.location;

import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.ReferencedWrapper;
import org.dockbox.selene.api.objects.location.dimensions.Chunk;
import org.dockbox.selene.api.objects.location.dimensions.World;
import org.dockbox.selene.api.objects.tuple.Vector3N;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.world.extent.Extent;

public class SpongeChunk extends ReferencedWrapper<org.spongepowered.api.world.Chunk> implements Chunk, SpongeDimension {

    public SpongeChunk(org.spongepowered.api.world.Chunk reference) {
        super(reference);
    }

    @Override
    public Exceptional<org.spongepowered.api.world.Chunk> constructInitialReference() {
        return this.getReference();
    }

    @Override
    public Vector3N getPosition() {
        return this.getReference()
                .map(chunk -> chunk.getPosition().toDouble())
                .map(SpongeConversionUtil::fromSponge)
                .or(Vector3N.empty());
    }

    @Override
    public World getWorld() {
        return this.getReference()
                .map(org.spongepowered.api.world.Chunk::getWorld)
                .map(SpongeConversionUtil::fromSponge)
                .or(World.empty());
    }

    @Override
    public Extent getExtent() {
        return this.getReference().get();
    }
}
