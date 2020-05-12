package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.types.math.AbstractVector2;
import com.darwinreforged.server.core.types.location.DarwinLocation;
import com.darwinreforged.server.core.types.location.DarwinWorld;
import com.darwinreforged.server.core.types.math.Vector3i;
import com.darwinreforged.server.core.init.AbstractUtility;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AbstractUtility("Location calculations and conversion methods")
public abstract class LocationUtils {

    public static final DarwinLocation getEmptyWorld() {
        DarwinWorld world = new DarwinWorld(UUID.fromString("00000000-0000-0000-0000-000000000000"), "None");
        Vector3i vector = new Vector3i(0, 0, 0);
        return new DarwinLocation(world, vector);
    }

    public abstract int getHighestPoint(AbstractVector2<? extends Number> vector2, DarwinWorld world);

    public abstract int getHighestPoint(DarwinLocation location);

    public abstract int getHighestPoint(Number x, Number z, UUID worldUUID);

    public abstract Optional<DarwinWorld> getWorld(UUID uuid);

    public abstract Optional<DarwinWorld> getWorld(String name);

    public abstract Collection<DarwinWorld> getAllWorlds();

    public Collection<DarwinWorld> getEmptyWorlds() {
        return getAllWorlds().stream().filter(w -> w.getPlayerCount() == 0).collect(Collectors.toList());
    }

    public abstract int getPlayerCountInWorld(DarwinWorld world);

    public abstract void unloadWorld(DarwinWorld world, boolean keepLoaded);
}
