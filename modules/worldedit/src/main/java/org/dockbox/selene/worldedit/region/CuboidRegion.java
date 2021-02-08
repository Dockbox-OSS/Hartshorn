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

package org.dockbox.selene.worldedit.region;

import org.dockbox.selene.api.objects.location.World;
import org.dockbox.selene.api.objects.tuple.Vector3N;
import org.dockbox.selene.api.util.SeleneUtils;

public class CuboidRegion implements Region
{

    private final World world;
    private final Vector3N from;
    private final Vector3N to;

    public CuboidRegion(World world, Vector3N from, Vector3N to)
    {
        this.world = world;
        this.from = from;
        this.to = to;
    }

    @Override
    public Vector3N getMinimumPoint()
    {
        return SeleneUtils.getMinimumPoint(this.from, this.to);
    }

    @Override
    public Vector3N getMaximumPoint()
    {
        return SeleneUtils.getMaximumPoint(this.from, this.to);
    }

    @Override
    public Vector3N getCenter()
    {
        return SeleneUtils.getCenterPoint(this.from, this.to);
    }

    @Override
    public int getArea()
    {
        return this.getWidth() * this.getWidth() * this.getLength();
    }

    @Override
    public int getWidth()
    {
        return this.getMaximumPoint().getXi() - this.getMinimumPoint().getXi();
    }

    @Override
    public int getHeight()
    {
        return this.getMaximumPoint().getYi() - this.getMinimumPoint().getYi();
    }

    @Override
    public int getLength()
    {
        return this.getMaximumPoint().getZi() - this.getMinimumPoint().getZi();
    }

    @Override
    public World getWorld()
    {
        return this.world;
    }

}
