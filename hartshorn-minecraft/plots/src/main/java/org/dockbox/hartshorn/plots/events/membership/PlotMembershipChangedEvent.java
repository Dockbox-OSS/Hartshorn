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

package org.dockbox.hartshorn.plots.events.membership;

import org.dockbox.hartshorn.plots.Plot;
import org.dockbox.hartshorn.plots.PlotMembership;
import org.dockbox.hartshorn.plots.events.PlotPlayerEvent;
import org.dockbox.hartshorn.server.minecraft.players.Player;

import lombok.Getter;

@Getter
public class PlotMembershipChangedEvent extends PlotPlayerEvent {

    private final Player initiator;
    private final PlotMembership membership;

    public PlotMembershipChangedEvent(Plot plot, Player player, Player initiator, PlotMembership membership) {
        super(plot, player);
        this.initiator = initiator;
        this.membership = membership;
    }
}
