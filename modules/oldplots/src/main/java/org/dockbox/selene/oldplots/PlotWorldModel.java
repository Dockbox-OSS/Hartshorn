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

package org.dockbox.selene.oldplots;

import org.dockbox.selene.core.WorldStorageService;
import org.dockbox.selene.core.annotations.entity.Metadata;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.location.Location;
import org.dockbox.selene.core.server.Selene;

@Metadata(alias = "plot-world-model")
public class PlotWorldModel
{

    private String name;
    private int size;
    private int road;
    private int zeroX;
    private int zeroZ;
    private int height;

    public PlotWorldModel()
    {
    }

    public PlotWorldModel(String name, int size, int road, int zeroX, int zeroZ, int height)
    {
        this.name = name;
        this.size = size;
        this.road = road;
        this.zeroX = zeroX;
        this.zeroZ = zeroZ;
        this.height = height;
    }

    public Exceptional<Location> getLocation(int plotX, int plotZ)
    {
        return Selene.provide(WorldStorageService.class).getWorld(this.getName()).map(world -> {
            return new Location(this.getHomeX(plotX), this.getHeight(), this.getHomeZ(plotZ), world);
        });
    }

    public String getName()
    {
        return this.name;
    }

    public int getHomeX(int plotX)
    {
        return this.zeroX + (plotX * (this.size + this.road));
    }

    public int getHeight()
    {
        return this.height;
    }

    public int getHomeZ(int plotZ)
    {
        return this.zeroZ + (plotZ * (this.size + this.road));
    }
}
