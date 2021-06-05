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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.di.annotations.Required;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.plots.flags.PlotFlag;

@Required
public interface PlotService {

    Exceptional<Plot> getPlotAt(Location location);

    Exceptional<Plot> getCurrentPlot(Player player);

    void registerFlag(PlotFlag<?> flag);

    Exceptional<Plot> getPlot(World world, int x, int y);

    void setFilling(Plot plot, Item item);

    void setFloor(Plot plot, Item item);

    void setAir(Plot plot, Item item);

    void setAll(Plot plot, Item item);

    void setWallBorder(Plot plot, Item item);

    void setWallFilling(Plot plot, Item item);

    void setOutline(Plot plot, Item item);

    void setMiddle(Plot plot, Item item);

    Integer getSize(Plot plot);

    Text getAlias(Plot plot);

    void setAlias(Plot plot, Text item);
}
