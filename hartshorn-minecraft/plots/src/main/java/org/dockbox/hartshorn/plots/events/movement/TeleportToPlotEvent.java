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

package org.dockbox.hartshorn.plots.events.movement;

import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.plots.Plot;
import org.dockbox.hartshorn.plots.events.CancellablePlotPlayerEvent;

public class TeleportToPlotEvent extends CancellablePlotPlayerEvent {

    private final Location from;
    private final Location to;

    public TeleportToPlotEvent(Plot plot, Player player, Location from, Location to) {
        super(plot, player);
        this.from = from;
        this.to = to;
    }

    public Location getFrom() {
        return this.from;
    }

    public Location getTo() {
        return this.to;
    }
}
