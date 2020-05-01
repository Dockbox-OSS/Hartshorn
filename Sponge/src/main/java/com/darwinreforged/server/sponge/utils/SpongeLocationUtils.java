package com.darwinreforged.server.sponge.utils;

import com.darwinreforged.server.core.entities.AbstractVector2;
import com.darwinreforged.server.core.entities.DarwinLocation;
import com.darwinreforged.server.core.entities.DarwinWorld;
import com.darwinreforged.server.core.entities.Vector3i;
import com.darwinreforged.server.core.init.UtilityImplementation;
import com.darwinreforged.server.core.util.LocationUtils;
import com.flowpowered.math.vector.Vector3d;
import com.intellectualcrafters.plot.object.Location;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

@UtilityImplementation(LocationUtils.class)
public class SpongeLocationUtils extends LocationUtils {


    @Override
    public int getHighestPoint(AbstractVector2<? extends Number> vector2, DarwinWorld world) {
        return vector2 != null ? getHighestPoint(vector2.getX(), vector2.getZ(), world.getWorldUUID()) : -1;
    }

    @Override
    public int getHighestPoint(DarwinLocation location) {
        return location != null ? getHighestPoint(location.getX(), location.getZ(), location.getWorld().getWorldUUID()) : -1;
    }

    @Override
    public int getHighestPoint(Location location) {
        return location != null ? getHighestPoint(fromPlotsLocation(location).orElse(null)) : -1;
    }

    @Override
    public int getHighestPoint(Number x, Number z, UUID worldUUID) {
        Optional<World> worldCandidate = Sponge.getServer().getWorld(worldUUID);
        if (!worldCandidate.isPresent()) return -1;
        World world = worldCandidate.get();
        final int MAX_BUILD_HEIGHT = 256;

        BlockRay<World> blockRay = BlockRay.from(world, new Vector3d(x.intValue(), MAX_BUILD_HEIGHT, z.intValue()))
                .to(new Vector3d(x.intValue(), 0, z.intValue()))
                .build();

        while (blockRay.hasNext()) {
            BlockRayHit<World> hit = blockRay.next();
            //Air has a y and z value of 0
            if (hit.getPosition().getFloorY() != 0) {
                return hit.getBlockY();
            }
        }
        //End block wasn't found (May have been been air all the way down to the void)
        return -1;
    }

    @Override
    public Optional<DarwinWorld> getWorld(UUID uuid) {
        Optional<World> optionalWorld = Sponge.getServer().loadWorld(uuid);
        return optionalWorld.map(world -> new DarwinWorld(world.getUniqueId(), world.getName()));
    }

    @Override
    public Optional<DarwinWorld> getWorld(String name) {
        Optional<World> optionalWorld = Sponge.getServer().loadWorld(name);
        return optionalWorld.map(world -> new DarwinWorld(world.getUniqueId(), world.getName()));
    }

    @Override
    public Optional<DarwinLocation> fromPlotsLocation(Location location) {
        Optional<DarwinWorld> darwinWorldOptional = getWorld(location.getWorld());
        if (!darwinWorldOptional.isPresent()) return Optional.empty();
        Vector3i vector3i = new Vector3i(location.getX(), location.getY(), location.getZ());
        return Optional.of(new DarwinLocation(darwinWorldOptional.get(), vector3i));
    }

    @Override
    public Optional<Location> fromDarwinLocation(DarwinLocation location) {
        String worldName = location.getWorld().getName();
        return Optional.of(new Location(worldName, location.getX().intValue(), location.getY().intValue(), location.getZ().intValue()));
    }
}
