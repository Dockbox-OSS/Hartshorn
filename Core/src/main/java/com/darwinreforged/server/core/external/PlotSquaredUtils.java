package com.darwinreforged.server.core.external;

import com.darwinreforged.server.core.types.location.DarwinWorld;
import com.darwinreforged.server.core.internal.Utility;

/**
 The type Plot utils.
 */
@Utility("Implementation-independent PlotSquared utilities")
public abstract class PlotSquaredUtils {

    /**
     Is plot world boolean.

     @param world
     the world

     @return the boolean
     */
    public abstract boolean isPlotWorld(DarwinWorld world);

}
