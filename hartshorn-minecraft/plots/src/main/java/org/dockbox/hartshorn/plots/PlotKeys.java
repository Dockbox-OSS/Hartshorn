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
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.api.keys.Key;
import org.dockbox.hartshorn.api.keys.Keys;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Collection;

public final class PlotKeys {

    public static final Key<Location, Plot> PLOT = Keys.ofGetter(loc -> Hartshorn.context().get(PlotService.class).getPlotAt(loc));
    public static final Key<Player, Plot> CURRENT_PLOT = Keys.ofGetter(player -> Hartshorn.context().get(PlotService.class).getCurrentPlot(player));

    // The filling of the plot between bedrock (if present) and the plot floor
    public static final Key<Plot, Item> FILLING = Keys.ofSetter(((plot, item) -> Hartshorn.context().get(PlotService.class).setFilling(plot, item)));
    // The plot floor
    public static final Key<Plot, Item> FLOOR = Keys.ofSetter(((plot, item) -> Hartshorn.context().get(PlotService.class).setFilling(plot, item)));
    // The filling of the plot between the plot floor and the build height limit
    public static final Key<Plot, Item> AIR = Keys.ofSetter(((plot, item) -> Hartshorn.context().get(PlotService.class).setFilling(plot, item)));
    // The filling of the entire plot
    public static final Key<Plot, Item> ALL = Keys.ofSetter(((plot, item) -> Hartshorn.context().get(PlotService.class).setFilling(plot, item)));
    // The filling of the top of the plot wall
    public static final Key<Plot, Item> WALL_BORDER = Keys.ofSetter(((plot, item) -> Hartshorn.context().get(PlotService.class).setFilling(plot, item)));
    // The filling of the plot wall between bedrock (if present) and the wall border
    public static final Key<Plot, Item> WALL_FILLING = Keys.ofSetter(((plot, item) -> Hartshorn.context().get(PlotService.class).setFilling(plot, item)));
    // The filling of the outer edges of the plot (including top) from the plot floor (inclusive)
    public static final Key<Plot, Item> OUTLINE = Keys.ofSetter(((plot, item) -> Hartshorn.context().get(PlotService.class).setFilling(plot, item)));
    // The size of the plot, excluding plot borders
    public static final Key<Plot, Integer> SIZE = Keys.ofGetter(plot -> Exceptional.of(Hartshorn.context().get(PlotService.class).getSize(plot)));
    // The name of the plot
    public static final Key<Plot, Text> ALIAS = Keys.of(
            (plot, text) -> Hartshorn.context().get(PlotService.class).setAlias(plot, text),
            plot -> Exceptional.of(Hartshorn.context().get(PlotService.class).getAlias(plot))
    );

    public static Collection<Key<Plot, ?>> persistentDataKeys() {
        return HartshornUtils.asUnmodifiableList(FILLING, FLOOR, AIR, ALL, WALL_BORDER, WALL_FILLING, OUTLINE, SIZE, ALIAS);
    }

    public static Collection<Key<Plot, ?>> getObtainablePersistentKeys() {
        return HartshornUtils.asUnmodifiableList(PlotKeys.ALIAS, PlotKeys.SIZE);
    }

    private PlotKeys() {
    }
}
