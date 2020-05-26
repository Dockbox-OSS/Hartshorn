package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.math.AbstractVector2;
import com.darwinreforged.server.core.types.location.DarwinLocation;
import com.darwinreforged.server.core.types.location.DarwinWorld;
import com.darwinreforged.server.core.math.Vector3i;
import com.darwinreforged.server.core.internal.Utility;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 The type Location utils.
 */
@Utility("Location calculations and conversion methods")
public interface LocationUtils {

    /**
     Gets empty world.

     @return the empty world
     */
    default DarwinLocation getEmptyWorld() {
        DarwinWorld world = new DarwinWorld(UUID.fromString("00000000-0000-0000-0000-000000000000"), "None");
        Vector3i vector = new Vector3i(0, 0, 0);
        return new DarwinLocation(world, vector);
    }

    /**
     Gets highest point.

     @param vector2
     the vector 2
     @param world
     the world

     @return the highest point
     */
    default int getHighestPoint(AbstractVector2<? extends Number> vector2, DarwinWorld world) {
        return vector2 != null ? getHighestPoint(vector2.getX(), vector2.getZ(), world.getWorldUUID()) : -1;
    }

    /**
     Gets highest point.

     @param location
     the location

     @return the highest point
     */
    default int getHighestPoint(DarwinLocation location) {
        return location != null ? getHighestPoint(location.getX(), location.getZ(), location.getWorld().getWorldUUID()) : -1;
    }

    /**
     Gets highest point.

     @param x
     the x
     @param z
     the z
     @param worldUUID
     the world uuid

     @return the highest point
     */
    int getHighestPoint(Number x, Number z, UUID worldUUID);

    /**
     Gets world.

     @param uuid
     the uuid

     @return the world
     */
    Optional<DarwinWorld> getWorld(UUID uuid);

    /**
     Gets world.

     @param name
     the name

     @return the world
     */
    Optional<DarwinWorld> getWorld(String name);

    /**
     Gets all worlds.

     @return the all worlds
     */
    Collection<DarwinWorld> getAllWorlds();

    /**
     Gets empty worlds.

     @return the empty worlds
     */
    default Collection<DarwinWorld> getEmptyWorlds() {
        return getAllWorlds().stream().filter(w -> w.getPlayerCount() == 0).collect(Collectors.toList());
    }

    /**
     Gets player count in world.

     @param world
     the world

     @return the player count in world
     */
    int getPlayerCountInWorld(DarwinWorld world);

    /**
     Unload world.

     @param world
     the world
     @param keepLoaded
     the keep loaded
     */
    void unloadWorld(DarwinWorld world, boolean keepLoaded);
}
