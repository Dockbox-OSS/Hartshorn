package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.types.location.DarwinWorld;
import com.darwinreforged.server.core.init.AbstractUtility;

/**
 The type Plot utils.
 */
@AbstractUtility("Implementation-independent PlotSquared utilities")
public abstract class PlotUtils {

    /**
     Is plot world boolean.

     @param world
     the world

     @return the boolean
     */
    public abstract boolean isPlotWorld(DarwinWorld world);

}
