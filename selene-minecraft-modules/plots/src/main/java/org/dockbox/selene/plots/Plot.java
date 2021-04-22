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

package org.dockbox.selene.plots;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.keys.KeyHolder;
import org.dockbox.selene.minecraft.dimension.position.Direction;
import org.dockbox.selene.minecraft.dimension.position.Location;
import org.dockbox.selene.minecraft.dimension.world.World;
import org.dockbox.selene.minecraft.players.Player;
import org.dockbox.selene.plots.flags.PlotFlag;

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
        return Selene.provide(PlotService.class).getPlot(world, x, y);
    }

    boolean isWorld();

}
