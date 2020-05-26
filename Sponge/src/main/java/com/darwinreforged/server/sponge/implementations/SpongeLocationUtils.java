package com.darwinreforged.server.sponge.implementations;

import com.darwinreforged.server.core.types.location.DarwinWorld;
import com.darwinreforged.server.core.util.LocationUtils;
import com.flowpowered.math.vector.Vector3d;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpongeLocationUtils implements LocationUtils {

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
    public void unloadWorld(DarwinWorld world, boolean keepLoaded) {
        Optional<World> optionalWorld = Sponge.getServer().getWorld(world.getWorldUUID());
        // World isn't present if already unloaded
        optionalWorld.ifPresent(sw -> {
            Sponge.getServer().unloadWorld(sw);
            if (!keepLoaded) {
                sw.setKeepSpawnLoaded(false);
                Optional<WorldProperties> worldPropertiesOpt = Sponge.getServer().getWorldProperties(sw.getUniqueId());
                worldPropertiesOpt.ifPresent(props -> props.setLoadOnStartup(false));
            }
        });
    }
}
