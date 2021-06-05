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

package org.dockbox.hartshorn.sponge.external;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.sponge.util.SpongeConversionUtil;
import org.dockbox.hartshorn.util.Wrapper;
import org.dockbox.hartshorn.worldedit.region.Region;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class WrappedRegion implements Region, Wrapper<com.sk89q.worldedit.regions.Region> {

    private com.sk89q.worldedit.regions.Region region;

    public WrappedRegion(com.sk89q.worldedit.regions.Region region) {
        this.region = region;
    }

    @Override
    public Vector3N getMinimumPoint() {
        return SpongeConversionUtil.fromWorldEdit(this.region.getMinimumPoint());
    }

    @Override
    public Vector3N getMaximumPoint() {
        return SpongeConversionUtil.fromWorldEdit(this.region.getMaximumPoint());
    }

    @Override
    public Vector3N getCenter() {
        return SpongeConversionUtil.fromWorldEdit(this.region.getCenter());
    }

    @Override
    public int getArea() {
        return this.region.getArea();
    }

    @Override
    public int getWidth() {
        return this.region.getWidth();
    }

    @Override
    public int getHeight() {
        return this.region.getHeight();
    }

    @Override
    public int getLength() {
        return this.region.getHeight();
    }

    @Override
    public World getWorld() {
        return SpongeConversionUtil.fromWorldEdit(Objects.requireNonNull(this.region.getWorld()));
    }

    @Override
    public Exceptional<com.sk89q.worldedit.regions.Region> getReference() {
        return Exceptional.of(this.region);
    }

    @Override
    public void setReference(@NotNull Exceptional<com.sk89q.worldedit.regions.Region> reference) {
        reference.present(region -> this.region = region);
    }

    @Override
    public Exceptional<com.sk89q.worldedit.regions.Region> constructInitialReference() {
        return Exceptional.none();
    }
}