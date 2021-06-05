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

package org.dockbox.hartshorn.oldplots;

import org.dockbox.hartshorn.api.i18n.annotations.Resource;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.di.annotations.Service;
import org.jetbrains.annotations.NotNull;

@Service(owner = OldPlotsService.class)
public interface OldPlotsResources {

    @Resource(value = "$3 - $1{0}$2, $1{1}, {2}", key = "oldplots.list.single")
    ResourceEntry getSinglePlotListItem(@NotNull String world, @NotNull Integer idX, @NotNull Integer idZ);

    @Resource(value = "$2Teleport to $1{0}$2, $1{1}, {2}", key = "oldplots.list.hover")
    ResourceEntry getSinglePlotListItemHover(@NotNull String world, @NotNull Integer idX, @NotNull Integer idZ);

    @Resource(value = "$1OldPlots for $2{0}", key = "oldplots.list.title")
    ResourceEntry getListTitle(String name);

    @Resource(value = "$4Worlds are not stored as OldPlots", key = "oldplots.caught.worlds")
    ResourceEntry getCaughtError();

    @Resource(value = "$4Could not calculate plot location", key = "oldplots.caught.calculation")
    ResourceEntry getCalculationError();

    @Resource(value = "$4No world location configured for '{0}'", key = "oldplots.caught.location")
    ResourceEntry getLocationError(@NotNull String world);

    @Resource(value = "$4No plot with that ID found", key = "oldplots.caught.plot")
    ResourceEntry getPlotError();

    @Resource(value = "$4No valid player provided", key = "oldplots.caught.player")
    ResourceEntry getPlayerError();
}
