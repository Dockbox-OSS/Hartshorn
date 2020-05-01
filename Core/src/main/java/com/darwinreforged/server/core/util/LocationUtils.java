package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.entities.AbstractVector2;
import com.darwinreforged.server.core.entities.DarwinLocation;
import com.darwinreforged.server.core.entities.DarwinWorld;
import com.darwinreforged.server.core.init.AbstractUtility;
import com.intellectualcrafters.plot.object.Location;

import java.util.Optional;
import java.util.UUID;

@AbstractUtility("Location calculations and conversion methods")
public abstract class LocationUtils {

    public abstract int getHighestPoint(AbstractVector2<? extends Number> vector2, DarwinWorld world);

    public abstract int getHighestPoint(DarwinLocation location);

    public abstract int getHighestPoint(Location location);

    public abstract int getHighestPoint(Number x, Number z, UUID worldUUID);

    public abstract Optional<DarwinWorld> getWorld(UUID uuid);

    public abstract Optional<DarwinWorld> getWorld(String name);

    public abstract Optional<DarwinLocation> fromPlotsLocation(Location location);

    public abstract Optional<Location> fromDarwinLocation(DarwinLocation location);

}
