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

package org.dockbox.hartshorn.oldplots;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.annotations.Entity;
import org.dockbox.hartshorn.server.minecraft.dimension.Worlds;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(value = "plot-world-modelType")
@AllArgsConstructor
@NoArgsConstructor
public class PlotWorldModel {

    @Getter
    private String name;
    @Getter
    private int height;
    private int size;
    private int road;
    private int zeroX;
    private int zeroZ;

    public Exceptional<Location> location(int plotX, int plotZ) {
        return Hartshorn.context().get(Worlds.class)
                .world(this.name())
                .map(world -> Location.of(this.x(plotX), this.height(), this.z(plotZ), world));
    }

    public int x(int plotX) {
        return this.zeroX + (plotX * (this.size + this.road));
    }

    public int z(int plotZ) {
        return this.zeroZ + (plotZ * (this.size + this.road));
    }
}
