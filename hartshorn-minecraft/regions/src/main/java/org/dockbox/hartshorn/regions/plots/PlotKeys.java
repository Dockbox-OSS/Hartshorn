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
import org.dockbox.hartshorn.api.keys.Key;
import org.dockbox.hartshorn.api.keys.Keys;
import org.dockbox.hartshorn.regions.RegionService;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.Player;

public final class PlotKeys {

    public static final Key<Location, Plot> PLOT = Keys.builder(Location.class, Plot.class)
            .withGetterSafe(loc -> Hartshorn.context().get(RegionService.class).first(loc, Plot.class))
            .build();

    public static final Key<Player, Plot> CURRENT_PLOT = Keys.builder(Player.class, Plot.class)
            .withGetterSafe(player -> Hartshorn.context().get(RegionService.class).first(player, Plot.class))
            .build();

    // The filling of the plot between bedrock (if present) and the plot floor
    public static final Key<Plot, Item> FILLING = Keys.builder(Plot.class, Item.class)
            .withSetter((plot, item) -> Hartshorn.context().get(PlotService.class).filling(plot, item))
            .build();

    // The plot floor
    public static final org.dockbox.hartshorn.api.keys.Key<Plot, Item> FLOOR = Keys.builder(Plot.class, Item.class)
            .withSetter(((plot, item) -> Hartshorn.context().get(PlotService.class).filling(plot, item)))
            .build();

    // The filling of the plot between the plot floor and the build height limit
    public static final Key<Plot, Item> AIR = Keys.builder(Plot.class, Item.class)
            .withSetter(((plot, item) -> Hartshorn.context().get(PlotService.class).filling(plot, item)))
            .build();

    // The filling of the entire plot
    public static final Key<Plot, Item> ALL = Keys.builder(Plot.class, Item.class)
            .withSetter(((plot, item) -> Hartshorn.context().get(PlotService.class).filling(plot, item)))
            .build();

    // The filling of the top of the plot wall
    public static final Key<Plot, Item> WALL_BORDER = Keys.builder(Plot.class, Item.class)
            .withSetter(((plot, item) -> Hartshorn.context().get(PlotService.class).filling(plot, item)))
            .build();

    // The filling of the plot wall between bedrock (if present) and the wall border
    public static final Key<Plot, Item> WALL_FILLING = Keys.builder(Plot.class, Item.class)
            .withSetter(((plot, item) -> Hartshorn.context().get(PlotService.class).filling(plot, item)))
            .build();

    // The filling of the outer edges of the plot (including top) from the plot floor (inclusive)
    public static final Key<Plot, Item> OUTLINE = Keys.builder(Plot.class, Item.class)
            .withSetter(((plot, item) -> Hartshorn.context().get(PlotService.class).filling(plot, item)))
            .build();

}
