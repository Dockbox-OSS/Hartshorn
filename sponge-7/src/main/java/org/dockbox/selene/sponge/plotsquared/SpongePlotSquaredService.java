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

package org.dockbox.selene.sponge.plotsquared;

import com.intellectualcrafters.plot.flag.Flag;
import com.intellectualcrafters.plot.flag.Flags;

import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.location.Location;
import org.dockbox.selene.api.objects.player.Player;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.plots.Plot;
import org.dockbox.selene.plots.PlotService;
import org.dockbox.selene.plots.flags.PlotFlag;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;

import java.util.Map;

public class SpongePlotSquaredService implements PlotService {

    private static final Map<String, PlotFlag<?>> flagRegistrations = SeleneUtils.emptyConcurrentMap();
    private static final Map<String, Flag<?>> plotSquaredFlagRegistrations = SeleneUtils.emptyConcurrentMap();

    @Override
    public Exceptional<Plot> getPlotAt(Location location) {
        com.intellectualcrafters.plot.object.Plot plot = com.intellectualcrafters.plot.object.Plot.getPlot(SpongeConversionUtil.toPlotSquared(location));
        return Exceptional.ofNullable(plot).map(SpongePlot::new);
    }

    @Override
    public Exceptional<Plot> getCurrentPlot(Player player) {
        com.intellectualcrafters.plot.object.Plot plot = SpongeConversionUtil.toPlotSquared(player).getCurrentPlot();
        return Exceptional.ofNullable(plot).map(SpongePlot::new);
    }

    @Override
    public void registerFlag(PlotFlag<?> flag) {
        Flag<?> plotSquaredFlag = new SpongePlotSquaredCustomFlag<>(flag);
        plotSquaredFlagRegistrations.put(flag.getId(), plotSquaredFlag);
        flagRegistrations.put(flag.getId(), flag);
        Flags.registerFlag(plotSquaredFlag);
    }

    protected static Exceptional<Flag<?>> getPlotSquaredFlag(String id) {
        return Exceptional.ofNullable(plotSquaredFlagRegistrations.getOrDefault(id, null));
    }

    protected static Exceptional<PlotFlag<?>> getFlag(String id) {
        return Exceptional.ofNullable(flagRegistrations.getOrDefault(id, null));
    }
}
