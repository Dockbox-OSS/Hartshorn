package com.darwinreforged.server.sponge.utils;

import com.darwinreforged.server.core.entities.location.DarwinWorld;
import com.darwinreforged.server.core.init.UtilityImplementation;
import com.darwinreforged.server.core.util.PlotUtils;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.PlotArea;

@UtilityImplementation(PlotUtils.class)
public class SpongePlotUtils extends PlotUtils {
    @Override
    public boolean isPlotWorld(DarwinWorld world) {
        PlotArea plotArea = getPlotArea(world);
        if (plotArea == null) return false;
        return (plotArea.getPlotCount() > 1);
    }

    private PlotArea getPlotArea(DarwinWorld world) {
        Location location = new Location(world.getName(), 0, 0, 0);
        return location.getPlotArea();
    }
}
