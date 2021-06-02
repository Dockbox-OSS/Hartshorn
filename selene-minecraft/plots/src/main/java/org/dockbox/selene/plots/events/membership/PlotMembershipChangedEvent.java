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

package org.dockbox.selene.plots.events.membership;

import org.dockbox.selene.server.minecraft.players.Player;
import org.dockbox.selene.plots.Plot;
import org.dockbox.selene.plots.PlotMembership;
import org.dockbox.selene.plots.events.PlotPlayerEvent;

public class PlotMembershipChangedEvent extends PlotPlayerEvent {

    private final Player initiator;
    private final PlotMembership membership;

    public PlotMembershipChangedEvent(Plot plot, Player player, Player initiator, PlotMembership membership) {
        super(plot, player);
        this.initiator = initiator;
        this.membership = membership;
    }

    public Player getInitiator() {
        return this.initiator;
    }

    public PlotMembership getMembership() {
        return this.membership;
    }
}
