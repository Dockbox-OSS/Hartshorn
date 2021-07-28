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

package org.dockbox.hartshorn.regions;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.annotations.inject.Required;
import org.dockbox.hartshorn.regions.flags.RegionFlag;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.players.Player;

import java.util.Set;

@Required
public interface RegionService {

    <R extends Region> Exceptional<R> first(Location location, Class<R> type);

    <R extends Region> Exceptional<R> first(Player player, Class<R> type);

    <R extends Region> Exceptional<R> first(World world, int x, int y, Class<R> type);

    <R extends Region> Set<R> all(Location location, Class<R> type);

    <R extends Region> Set<R> all(Player player, Class<R> type);

    <R extends Region> Set<R> all(World world, int x, int y, Class<R> type);

    void register(RegionFlag<?> flag);

    Exceptional<RegionFlag<?>> flag(String id);

}
