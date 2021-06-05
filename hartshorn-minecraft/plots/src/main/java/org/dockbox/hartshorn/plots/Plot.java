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

package org.dockbox.hartshorn.plots;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.keys.KeyHolder;
import org.dockbox.hartshorn.plots.flags.PlotFlag;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Direction;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.players.Player;

import java.util.Collection;
import java.util.Map;

public interface Plot extends KeyHolder<Plot> {

    Exceptional<Player> getOwner();

    Collection<Player> getPlayers(PlotMembership membership);

    boolean hasMembership(Player player, PlotMembership membership);

    boolean hasAnyMembership(Player player, PlotMembership... membership);

    Map<PlotFlag<?>, ?> getFlags();

    <T> void addFlag(PlotFlag<T> flag, T value);

    void removeFlag(PlotFlag<?> flag);

    <T> Exceptional<T> getFlag(PlotFlag<T> flag);

    int getPlotX();

    int getPlotY();

    Location getHome();

    Location getCenter();

    Exceptional<Plot> getRelative(Direction direction);

    static Exceptional<Plot> getById(World world, int x, int y) {
        return Hartshorn.context().get(PlotService.class).getPlot(world, x, y);
    }

    boolean isWorld();

}
