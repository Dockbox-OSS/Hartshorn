package com.darwinreforged.server.sponge.implementations;

import com.darwinreforged.server.core.types.location.DarwinWorld;
import com.darwinreforged.server.core.external.PlotSquaredUtils;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.PlotArea;

public class SpongePlotSquaredUtils implements PlotSquaredUtils {

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
