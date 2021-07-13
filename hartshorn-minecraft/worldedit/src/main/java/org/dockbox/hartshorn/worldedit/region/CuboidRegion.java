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

package org.dockbox.hartshorn.worldedit.region;

import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.util.HartshornUtils;

import lombok.Getter;

public class CuboidRegion implements Region {

    @Getter
    private final World world;
    private final Vector3N from;
    private final Vector3N to;

    public CuboidRegion(World world, Vector3N from, Vector3N to) {
        this.world = world;
        this.from = from;
        this.to = to;
    }

    @Override
    public Vector3N minimum() {
        return HartshornUtils.minimumPoint(this.from, this.to);
    }

    @Override
    public Vector3N maximum() {
        return HartshornUtils.maximumPoint(this.from, this.to);
    }

    @Override
    public Vector3N center() {
        return HartshornUtils.centerPoint(this.from, this.to);
    }

    @Override
    public int area() {
        return this.width() * this.width() * this.length();
    }

    @Override
    public int width() {
        return this.maximum().xI() - this.minimum().xI();
    }

    @Override
    public int height() {
        return this.maximum().yI() - this.minimum().yI();
    }

    @Override
    public int length() {
        return this.maximum().zI() - this.minimum().zI();
    }
}
