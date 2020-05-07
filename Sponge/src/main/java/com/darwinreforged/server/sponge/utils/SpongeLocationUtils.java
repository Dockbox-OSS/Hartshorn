package com.darwinreforged.server.sponge.utils;

import com.darwinreforged.server.core.entities.AbstractVector2;
import com.darwinreforged.server.core.entities.DarwinLocation;
import com.darwinreforged.server.core.entities.DarwinWorld;
import com.darwinreforged.server.core.init.UtilityImplementation;
import com.darwinreforged.server.core.util.LocationUtils;
import com.flowpowered.math.vector.Vector3d;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public Collection<DarwinWorld> getAllWorlds() {
        return Sponge.getServer().getWorlds().stream()
                .map(sw -> new DarwinWorld(sw.getUniqueId(), sw.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public int getPlayerCountInWorld(DarwinWorld world) {
        return Sponge.getServer().getWorld(world.getWorldUUID()).map(value -> value.getPlayers().size()).orElse(0);
    }

    @Override
    public void unloadWorld(DarwinWorld world) {
        Optional<World> optionalWorld = Sponge.getServer().getWorld(world.getWorldUUID());
        // World isn't present if already unloaded
        optionalWorld.ifPresent(sw -> Sponge.getServer().unloadWorld(sw));
    }
}
