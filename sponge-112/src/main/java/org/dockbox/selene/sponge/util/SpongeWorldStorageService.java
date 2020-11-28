package org.dockbox.selene.sponge.util;

import org.dockbox.selene.core.objects.location.World;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.util.world.WorldStorageService;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;

import java.util.List;
import java.util.UUID;

public class SpongeWorldStorageService extends WorldStorageService {

    @NotNull
    @Override
    public List<World> getLoadedWorlds() {
        return Sponge.getServer().getWorlds().map(world -> SpongeConversionUtil.fromSponge(world));
    }

    @NotNull
    @Override
    public List<UUID> getAllWorldUUIDs() {
        return Sponge.getServer().getAllWorldProperties().map(world -> world.uniqueId);
    }

    @NotNull
    @Override
    public Exceptional<World> getWorld(@NotNull String name) {
        return Exceptional.of(Sponge.getServer().loadWorld(name)).map(world -> SpongeConversionUtil.fromSponge(world));
    }

    @NotNull
    @Override
    public Exceptional<World> getWorld(@NotNull UUID uuid) {
        return Exceptional.of(Sponge.getServer().loadWorld(uuid)).map(world -> SpongeConversionUtil.fromSponge(world));
    }
}
