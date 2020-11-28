package org.dockbox.selene.sponge.objects.location;

import com.flowpowered.math.vector.Vector3i;

import org.dockbox.selene.core.objects.FieldReferenceHolder;
import org.dockbox.selene.core.objects.location.World;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.objects.tuple.Vector3N;
import org.dockbox.selene.core.objects.user.Gamemode;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;

import java.util.Map;
import java.util.UUID;

public class SpongeWorld extends World {

    private final FieldReferenceHolder<org.spongepowered.api.world.World> worldReference = new FieldReferenceHolder<>(
            Exceptional.of(Sponge.getServer().getWorld(this.getWorldUniqueId())), world -> {
        // Do NOT load the world here as this reference is also used for several methods where the world does
        // not have to be loaded, or even _should_ not be loaded due to the performance impact of loading a world.
        if (null == world) return Exceptional.of(Sponge.getServer().getWorld(this.getWorldUniqueId()));
        else return Exceptional.empty();
    }, org.spongepowered.api.world.World.class);

    public SpongeWorld(@NotNull UUID worldUniqueId, @NotNull String name, boolean loadOnStartup, @NotNull Vector3N spawnPosition, long seed, @NotNull Gamemode defaultGamemode, @NotNull Map<String, String> gamerules) {
        super(worldUniqueId, name, loadOnStartup, spawnPosition, seed, defaultGamemode, gamerules);
    }

    @Override
    public int getPlayerCount() {
        if (this.worldReference.referenceExists()) return this.worldReference.getReference().get().getPlayers().size();
        else return 0;
    }

    @Override
    public boolean unload() {
        if (this.worldReference.referenceExists()) {
            return Sponge.getServer().unloadWorld(this.worldReference.getReference().get());
        } else return false;
    }

    @Override
    public boolean load() {
        if (!this.isLoaded()) {
            return Sponge.getServer().loadWorld(this.getWorldUniqueId()).isPresent();
        } else return this.isLoaded();
    }

    @Override
    public boolean isLoaded() {
        if (this.worldReference.referenceExists()) {
            return this.worldReference.getReference().get().isLoaded();
        } else return false;
    }

    @Override
    public boolean getLoadOnStartup() {
        if (this.worldReference.referenceExists()) {
            return this.worldReference.getReference().get().getProperties().loadOnStartup();
        } else return false;
    }

    @Override
    public void setLoadOnStartup(boolean loadOnStartup) {
        if (this.worldReference.referenceExists()) {
            this.worldReference.getReference().get().getProperties().setLoadOnStartup(loadOnStartup);
        }
    }

    @NotNull
    @Override
    public Vector3N getSpawnPosition() {
        if (this.worldReference.referenceExists()) {
            Vector3i vector3i = this.worldReference.getReference().get().getProperties().getSpawnPosition();
            return new Vector3N(vector3i.getX(), vector3i.getY(), vector3i.getZ());
        } else return new Vector3N(0, 0, 0);
    }

    @Override
    public void setSpawnPosition(@NotNull Vector3N spawnPosition) {
        if (this.worldReference.referenceExists()) {
            this.worldReference.getReference().get().getProperties().setSpawnPosition(
                    new Vector3i(spawnPosition.getXi(),
                            spawnPosition.getYi(),
                            spawnPosition.getZi()
                    ));
        }
    }

    @Override
    public long getSeed() {
        if (this.worldReference.referenceExists()) {
            return this.worldReference.getReference().get().getProperties().getSeed();
        } else return 0;
    }

    @Override
    public void setSeed(long seed) {
        if (this.worldReference.referenceExists()) {
            this.worldReference.getReference().get().getProperties().setSeed(seed);
        }
    }

    @NotNull
    @Override
    public Gamemode getDefaultGamemode() {
        if (this.worldReference.referenceExists()) {
            return SpongeConversionUtil.fromSponge(this.worldReference.getReference().get().getProperties().getGameMode());
        } else return Gamemode.OTHER;
    }

    @Override
    public void setDefaultGamemode(@NotNull Gamemode defaultGamemode) {
        if (this.worldReference.referenceExists()) {
            this.worldReference.getReference().get().getProperties()
                    .setGameMode(SpongeConversionUtil.toSponge(defaultGamemode));
        }
    }

    public org.spongepowered.api.world.World getReferenceWorld() {
        return this.worldReference.getReference().orNull();
    }
}
