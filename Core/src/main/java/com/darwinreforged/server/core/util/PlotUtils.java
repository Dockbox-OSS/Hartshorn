package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.entities.location.DarwinWorld;
import com.darwinreforged.server.core.init.AbstractUtility;

@AbstractUtility("Implementation-independent PlotSquared utilities")
public abstract class PlotUtils {

    public abstract boolean isPlotWorld(DarwinWorld world);

}
