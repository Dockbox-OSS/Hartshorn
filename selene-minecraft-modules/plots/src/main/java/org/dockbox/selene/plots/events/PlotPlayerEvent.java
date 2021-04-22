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

package org.dockbox.selene.plots.events;

import org.dockbox.selene.api.domain.Target;
import org.dockbox.selene.api.events.parents.Targetable;
import org.dockbox.selene.minecraft.players.Player;
import org.dockbox.selene.plots.Plot;

public abstract class PlotPlayerEvent extends PlotEvent implements Targetable {

    private final Player player;

    public PlotPlayerEvent(Plot plot, Player player) {
        super(plot);
        this.player = player;
    }

    @Override
    public Target getTarget() {
        return this.player;
    }

    @Override
    public void setTarget(Target target) {
        throw new UnsupportedOperationException();
    }
}
