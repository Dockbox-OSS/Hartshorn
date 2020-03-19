package com.darwinreforged.servermodifications.util;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotArea;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class PlotManager {

    public org.spongepowered.api.world.Location<World> convertPlotsToSpongeLoc(com.intellectualcrafters.plot.object.Location p2Location) {
        Optional<World> worldOptional = Sponge.getServer().getWorld(p2Location.getWorld());
        World world = null;
        if (worldOptional.isPresent()) world = worldOptional.get();
        if (world == null) return null;
        else return new org.spongepowered.api.world.Location<>(world, p2Location.getX(), p2Location.getY(), p2Location.getZ());
    }

    public com.intellectualcrafters.plot.object.Location convertSpongeToPlotsLoc(org.spongepowered.api.world.Location spongeLocation) {
        World world = (World) spongeLocation.getExtent();
        int locX = ((Double) spongeLocation.getX()).intValue();
        int locY = ((Double) spongeLocation.getY()).intValue();
        int locZ = ((Double) spongeLocation.getZ()).intValue();
        return new Location(world.getName(), locX, locY, locZ);
    }

    public Plot getPlotFromP2Loc(Location location) {
        PS instance = PS.get();
        if (instance != null) {
            PlotArea plotArea = instance.getApplicablePlotArea(location);
            if (plotArea != null) return plotArea.getPlot(location);
        }
        return null;
    }

    public Plot getPlotFromSpongeLoc(org.spongepowered.api.world.Location<World> spongeLocation) {
        Location p2Location = convertSpongeToPlotsLoc(spongeLocation);
        return getPlotFromP2Loc(p2Location);
    }

}
