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
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.regions.Region;
import org.dockbox.hartshorn.regions.RegionService;
import org.dockbox.hartshorn.regions.flags.RegionFlag;
import org.dockbox.hartshorn.regions.plots.Plot;
import org.dockbox.hartshorn.regions.plots.PlotService;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.toolbinding.ItemTool;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Set;

/**
 * Placeholder implementation of PlotService
 */
@Binds(RegionService.class)
@Binds(PlotService.class)
public class SpongePlotService implements PlotService, RegionService {

    @Override
    public <R extends Region> Exceptional<R> first(final Location location, final Class<R> type) {
        return Exceptional.empty();
    }

    @Override
    public <R extends Region> Exceptional<R> first(final Player player, final Class<R> type) {
        return Exceptional.empty();
    }

    @Override
    public <R extends Region> Exceptional<R> first(final World world, final int x, final int y, final Class<R> type) {
        return Exceptional.empty();
    }

    @Override
    public <R extends Region> Set<R> all(final Location location, final Class<R> type) {
        return HartshornUtils.emptySet();
    }

    @Override
    public <R extends Region> Set<R> all(final Player player, final Class<R> type) {
        return HartshornUtils.emptySet();
    }

    @Override
    public <R extends Region> Set<R> all(final World world, final int x, final int y, final Class<R> type) {
        return HartshornUtils.emptySet();
    }

    @Override
    public void register(final RegionFlag<?> flag) {
        // Nothing happens
    }

    @Override
    public Exceptional<RegionFlag<?>> flag(final String id) {
        return Exceptional.empty();
    }

    @Override
    public ItemTool tool() {
        return ItemTool.builder().build();
    }

    @Override
    public void filling(final Plot plot, final Item item) {
        // Nothing happens
    }

    @Override
    public void floor(final Plot plot, final Item item) {
        // Nothing happens
    }

    @Override
    public void air(final Plot plot, final Item item) {
        // Nothing happens
    }

    @Override
    public void all(final Plot plot, final Item item) {
        // Nothing happens
    }

    @Override
    public void wallBorder(final Plot plot, final Item item) {
        // Nothing happens
    }

    @Override
    public void wallFilling(final Plot plot, final Item item) {
        // Nothing happens
    }

    @Override
    public void outline(final Plot plot, final Item item) {
        // Nothing happens
    }

    @Override
    public void middle(final Plot plot, final Item item) {
        // Nothing happens
    }
}
