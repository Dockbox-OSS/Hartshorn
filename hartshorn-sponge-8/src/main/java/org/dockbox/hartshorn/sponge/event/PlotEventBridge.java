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

package org.dockbox.hartshorn.sponge.event;

import org.dockbox.hartshorn.api.events.annotations.Posting;
import org.dockbox.hartshorn.plots.events.ClearPlotEvent;
import org.dockbox.hartshorn.plots.events.DeletePlotEvent;
import org.dockbox.hartshorn.plots.events.PlotChangePropertyEvent;
import org.dockbox.hartshorn.plots.events.flags.PlotFlagAddedEvent;
import org.dockbox.hartshorn.plots.events.flags.PlotFlagRemovedEvent;
import org.dockbox.hartshorn.plots.events.membership.ClaimPlotEvent;
import org.dockbox.hartshorn.plots.events.membership.PlotMembershipChangedEvent;
import org.dockbox.hartshorn.plots.events.merge.PlotAutoMergeEvent;
import org.dockbox.hartshorn.plots.events.merge.PlotMergeEvent;
import org.dockbox.hartshorn.plots.events.merge.PlotUnlinkEvent;
import org.dockbox.hartshorn.plots.events.movement.EnterPlotEvent;
import org.dockbox.hartshorn.plots.events.movement.LeavePlotEvent;
import org.dockbox.hartshorn.plots.events.movement.TeleportToPlotEvent;

@Posting(value = {
        LeavePlotEvent.class,
        PlotFlagRemovedEvent.class,
        PlotAutoMergeEvent.class,
        PlotUnlinkEvent.class,
        TeleportToPlotEvent.class,
        ClaimPlotEvent.class,
        PlotMergeEvent.class,
        PlotMembershipChangedEvent.class,
        PlotChangePropertyEvent.class,
        PlotFlagAddedEvent.class,
        EnterPlotEvent.class,
        ClearPlotEvent.class,
        DeletePlotEvent.class
})
public class PlotEventBridge implements EventBridge {
}
