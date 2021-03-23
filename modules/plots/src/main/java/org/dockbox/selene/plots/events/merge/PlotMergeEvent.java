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

package org.dockbox.selene.plots.events.merge;

import org.dockbox.selene.api.objects.location.position.Direction;
import org.dockbox.selene.plots.Plot;
import org.dockbox.selene.plots.events.CancellablePlotEvent;

public class PlotMergeEvent extends CancellablePlotEvent {

    private final Direction direction;
    private final Plot otherPlot;

    public PlotMergeEvent(Plot plot, Direction direction) {
        super(plot);
        this.direction = direction;
        this.otherPlot = plot.getRelative(direction).orNull();
    }

    public Direction getDirection() {
        return direction;
    }

    public Plot getOtherPlot() {
        return otherPlot;
    }
}
