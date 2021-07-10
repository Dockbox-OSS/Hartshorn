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

package org.dockbox.hartshorn.sponge.util;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.di.annotations.Binds;
import org.dockbox.hartshorn.plots.Plot;
import org.dockbox.hartshorn.plots.PlotService;
import org.dockbox.hartshorn.plots.flags.PlotFlag;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.Player;

/**
 * Placeholder implementation of PlotService
 */
@Binds(PlotService.class)
public class SpongePlotService implements PlotService {
    @Override
    public Exceptional<Plot> getPlotAt(Location location) {
        return Exceptional.empty();
    }

    @Override
    public Exceptional<Plot> getCurrentPlot(Player player) {
        return Exceptional.empty();
    }

    @Override
    public void registerFlag(PlotFlag<?> flag) {
        // Nothing happens
    }

    @Override
    public Exceptional<Plot> getPlot(World world, int x, int y) {
        return Exceptional.empty();
    }

    @Override
    public void setFilling(Plot plot, Item item) {
        // Nothing happens
    }

    @Override
    public void setFloor(Plot plot, Item item) {
        // Nothing happens
    }

    @Override
    public void setAir(Plot plot, Item item) {
        // Nothing happens
    }

    @Override
    public void setAll(Plot plot, Item item) {
        // Nothing happens
    }

    @Override
    public void setWallBorder(Plot plot, Item item) {
        // Nothing happens
    }

    @Override
    public void setWallFilling(Plot plot, Item item) {
        // Nothing happens
    }

    @Override
    public void setOutline(Plot plot, Item item) {
        // Nothing happens
    }

    @Override
    public void setMiddle(Plot plot, Item item) {
        // Nothing happens
    }

    @Override
    public Integer getSize(Plot plot) {
        return -1;
    }

    @Override
    public Text getAlias(Plot plot) {
        return Text.of();
    }

    @Override
    public void setAlias(Plot plot, Text item) {
        // Nothing happens
    }
}
