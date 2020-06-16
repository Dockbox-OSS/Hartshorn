package org.dockbox.darwin.sponge.objects.location;

import org.dockbox.darwin.core.objects.location.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpongeWorld extends World {

    public SpongeWorld(@NotNull UUID worldUniqueId, @NotNull String name) {
        super(worldUniqueId, name);
    }

    @Override
    public int getPlayerCount() {
        return Sponge.getServer().getWorld(getWorldUniqueId()).map(w -> w.getPlayers().size()).orElse(0);
    }

    @Override
    public boolean unload() {
        AtomicBoolean didUnload = new AtomicBoolean(false);
        Sponge.getServer().getWorld(getWorldUniqueId()).ifPresent(w -> {
            Sponge.getServer().unloadWorld(w);
            didUnload.set(true);
        });
        return didUnload.get();
    }
}
