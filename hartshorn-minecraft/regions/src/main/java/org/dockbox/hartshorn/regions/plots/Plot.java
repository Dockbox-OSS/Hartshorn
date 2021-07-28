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

package org.dockbox.hartshorn.regions.plots;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.regions.MembershipRegion;
import org.dockbox.hartshorn.regions.RegionService;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Direction;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;

public interface Plot extends MembershipRegion {

    int x();

    int y();

    Location home();

    Exceptional<Plot> relative(Direction direction);

    static Exceptional<Plot> from(World world, int x, int y) {
        return Hartshorn.context().get(RegionService.class).first(world, x, y, Plot.class);
    }

}
