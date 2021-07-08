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
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.di.annotations.Binds;
import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.sponge.game.SpongeComposite;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.spongepowered.api.data.DataHolder.Mutable;
import org.spongepowered.api.world.server.ServerLocation;

@Binds(Location.class)
public class SpongeLocation extends Location implements SpongeComposite {

    private final Vector3N position;
    private final SpongeWorld world;

    @Wired
    public SpongeLocation(World world) {
        if (!(world instanceof SpongeWorld spongeWorld)) {
            throw new IllegalArgumentException("Provided world cannot be used as a Sponge reference");
        }
        this.world = spongeWorld;
        this.position = world.getSpawnPosition();
    }

    public SpongeLocation(Vector3N position, SpongeWorld world) {
        this.position = position;
        this.world = world;
    }

    @Override
    public Exceptional<? extends Mutable> getDataHolder() {
        return Exceptional.of(() -> ServerLocation.of(this.world.getKey(), SpongeConvert.toSpongeDouble(this.position)));
    }

    @Override
    public Location expand(Vector3N vector) {
        return new SpongeLocation(this.position.expand(vector), this.world);
    }

    @Override
    public Vector3N getVectorLoc() {
        return this.position;
    }

    @Override
    public World getWorld() {
        return this.world;
    }
}
