package com.darwinreforged.servermodifications.util;

import com.flowpowered.math.vector.Vector3d;
import com.intellectualcrafters.plot.object.Plot;
import com.sk89q.worldedit.Vector2D;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class LocationUtils {

    public static int getHighestPoint(Vector2D vector2D, World world) {
        return vector2D != null ? getHighestPoint(vector2D.getBlockX(), vector2D.getBlockZ(), world) : -1;
    }

    public static int getHighestPoint(Location<World> location) {
        return location != null ? getHighestPoint(location.getBlockX(), location.getBlockZ(), location.getExtent()) : -1;
    }

    public static int getHighestPoint(com.intellectualcrafters.plot.object.Location location) {
        return location != null ? getHighestPoint(convertPlotsToSpongeLoc(location)) : -1;
    }

    public static int getHighestPoint(int x, int z, World world) {
        if (world != null) {
            final int MAX_BUILD_HEIGHT = 256;

            BlockRay<World> blockRay = BlockRay.from(world, new Vector3d(x, MAX_BUILD_HEIGHT, z))
                    .to(new Vector3d(x, 0, z))
                    .build();

            while (blockRay.hasNext()) {
                BlockRayHit<World> hit = blockRay.next();
                //Air has a y and z value of 0
                if (hit.getPosition().getFloorY() != 0) {
                    return hit.getBlockY();
                }
            }
        }
        //End block wasn't found (May have been been air all the way down to the void)
        return -1;
    }

    public static Optional<World> getWorld(UUID uuid) {
        return Sponge.getServer().loadWorld(uuid);
    }

    public static Optional<World> getWorld(String name) {
        return Sponge.getServer().loadWorld(name);
    }

    public static org.spongepowered.api.world.Location<World> convertPlotsToSpongeLoc(com.intellectualcrafters.plot.object.Location p2Location) {
        Optional<World> worldOptional = LocationUtils.getWorld(p2Location.getWorld());
        World world = null;
        if (worldOptional.isPresent()) world = worldOptional.get();
        if (world == null) return null;
        else return new org.spongepowered.api.world.Location<>(world, p2Location.getX(), p2Location.getY(), p2Location.getZ());
    }

    public static com.intellectualcrafters.plot.object.Location convertSpongeToPlotsLoc(org.spongepowered.api.world.Location spongeLocation) {
        World world = (World) spongeLocation.getExtent();
        int locX = ((Double) spongeLocation.getX()).intValue();
        int locY = ((Double) spongeLocation.getY()).intValue();
        int locZ = ((Double) spongeLocation.getZ()).intValue();
        return new com.intellectualcrafters.plot.object.Location(world.getName(), locX, locY, locZ);
    }

    public static Plot getPlotFromSpongeLoc(org.spongepowered.api.world.Location<World> spongeLocation) {
        com.intellectualcrafters.plot.object.Location p2Location = convertSpongeToPlotsLoc(spongeLocation);
        return Plot.getPlot(p2Location);
    }
}
