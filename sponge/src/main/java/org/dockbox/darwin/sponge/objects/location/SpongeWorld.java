package org.dockbox.darwin.sponge.objects.location;

import org.dockbox.darwin.core.objects.location.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpongeWorld extends World {

    private final ThreadLocal<Optional<org.spongepowered.api.world.World>> reference = new ThreadLocal<Optional<org.spongepowered.api.world.World>>();

    private void refreshReference() {
        if (reference.get().isPresent()) reference.set(Sponge.getServer().getWorld(getWorldUniqueId()));
    }

    public org.spongepowered.api.world.World getReference() {
        refreshReference();
        return reference.get().orElse(null);
    }

    private boolean referenceExists() {
        refreshReference();
        return reference.get().isPresent();
    }

    public SpongeWorld(@NotNull UUID worldUniqueId, @NotNull String name) {
        super(worldUniqueId, name);
    }

    @Override
    public int getPlayerCount() {
        if (referenceExists()) return getReference().getPlayers().size();
        else return 0;
    }

    @Override
    public boolean unload() {
        if (referenceExists()) {
            AtomicBoolean didUnload = new AtomicBoolean(false);
            Sponge.getServer().getWorld(getWorldUniqueId()).ifPresent(w -> {
                Sponge.getServer().unloadWorld(w);
                didUnload.set(true);
            });
            return didUnload.get();
        }
        return false;
    }
}
