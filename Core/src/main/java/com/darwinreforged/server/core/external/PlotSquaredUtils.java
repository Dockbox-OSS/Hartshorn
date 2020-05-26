package com.darwinreforged.server.core.external;

import com.darwinreforged.server.core.types.location.DarwinWorld;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.PlotArea;

public class PlotSquaredUtils {

    /**
     Instantiates a new Singleton.

     @throws InstantiationException
     the instantiation exception */
    public PlotSquaredUtils() throws InstantiationException {
    }

    /**
     Is plot world boolean.

     @param world
     the world

     @return the boolean
     */
    public static boolean isPlotWorld(DarwinWorld world) {
        PlotArea plotArea = getPlotArea(world);
        if (plotArea == null) return false;
        return (plotArea.getPlotCount() > 1);
    }

    private static PlotArea getPlotArea(DarwinWorld world) {
        Location location = new Location(world.getName(), 0, 0, 0);
        return location.getPlotArea();
    }

}
