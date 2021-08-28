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

import org.dockbox.hartshorn.events.annotations.Posting;
import org.dockbox.hartshorn.regions.events.flags.RegionFlagAddedEvent;
import org.dockbox.hartshorn.regions.events.flags.RegionFlagRemovedEvent;
import org.dockbox.hartshorn.regions.events.membership.ClaimRegionEvent;
import org.dockbox.hartshorn.regions.events.membership.MembershipChangedEvent;
import org.dockbox.hartshorn.regions.events.movement.EnterRegionEvent;
import org.dockbox.hartshorn.regions.events.movement.LeaveRegionEvent;
import org.dockbox.hartshorn.regions.events.movement.TeleportToRegionEvent;
import org.dockbox.hartshorn.regions.plots.events.ClearPlotEvent;
import org.dockbox.hartshorn.regions.plots.events.DeletePlotEvent;
import org.dockbox.hartshorn.regions.plots.events.PlotChangePropertyEvent;
import org.dockbox.hartshorn.regions.plots.events.merge.PlotMergeEvent;
import org.dockbox.hartshorn.regions.plots.events.merge.PlotUnlinkEvent;
import org.dockbox.hartshorn.regions.plots.events.merge.RegionAutoMergeEvent;

@Posting(value = {
        LeaveRegionEvent.class,
        RegionFlagRemovedEvent.class,
        RegionAutoMergeEvent.class,
        PlotUnlinkEvent.class,
        TeleportToRegionEvent.class,
        ClaimRegionEvent.class,
        PlotMergeEvent.class,
        MembershipChangedEvent.class,
        PlotChangePropertyEvent.class,
        RegionFlagAddedEvent.class,
        EnterRegionEvent.class,
        ClearPlotEvent.class,
        DeletePlotEvent.class
})
public class PlotEventBridge extends EventBridge {
}
