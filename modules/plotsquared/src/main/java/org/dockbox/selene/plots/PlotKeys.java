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

import org.dockbox.selene.api.objects.keys.Key;
import org.dockbox.selene.api.objects.keys.Keys;
import org.dockbox.selene.api.objects.location.Location;
import org.dockbox.selene.api.objects.player.Player;
import org.dockbox.selene.api.server.Selene;

public class PlotKeys {

    public static final Key<Location, Plot> PLOT = Keys.getterKey(loc -> Selene.provide(PlotService.class).getPlotAt(loc));

    public static final Key<Player, Plot> CURRENT_PLOT = Keys.getterKey(player -> Selene.provide(PlotService.class).getCurrentPlot(player));

}
