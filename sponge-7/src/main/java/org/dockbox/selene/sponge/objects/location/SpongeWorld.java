/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.sponge.objects.location;

import com.flowpowered.math.vector.Vector3i;

import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.Wrapper;
import org.dockbox.selene.api.objects.location.World;
import org.dockbox.selene.api.objects.player.Gamemode;
import org.dockbox.selene.api.objects.tuple.Vector3N;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;

public class SpongeWorld extends World implements Wrapper<org.spongepowered.api.world.World> {

    private WeakReference<org.spongepowered.api.world.World> reference = new WeakReference<>(null);

    public SpongeWorld(
            @NotNull UUID worldUniqueId,
            @NotNull String name,
            boolean loadOnStartup,
            @NotNull Vector3N spawnPosition,
            long seed,
            @NotNull Gamemode defaultGamemode) {
        super(worldUniqueId, name, loadOnStartup, spawnPosition, seed, defaultGamemode);
        this.setReference(this.constructInitialReference());
    }

    @Override
    public int getPlayerCount() {
        if (this.referenceExists()) return this.getReference().get().getPlayers().size();
        else return 0;
    }

    @Override
    public boolean unload() {
        if (this.referenceExists()) {
            return Sponge.getServer().unloadWorld(this.getReference().get());
        }
        else return false;
    }

    @Override
    public boolean load() {
        if (!this.isLoaded()) {
            return Sponge.getServer().loadWorld(this.getWorldUniqueId()).isPresent();
        }
        else return this.isLoaded();
    }

    @Override
    public boolean isLoaded() {
        if (this.referenceExists()) {
            return this.getReference().get().isLoaded();
        }
        else return false;
    }

    @Override
    public Exceptional<org.spongepowered.api.world.World> getReference() {
        // Do NOT load the world here as this reference is also used for several methods where the world
        // does
        // not have to be loaded, or even _should_ not be loaded due to the performance impact of
        // loading a world.
        if (null == this.reference.get())
            this.setReference(Exceptional.of(Sponge.getServer().getWorld(this.getWorldUniqueId())));
        return Exceptional.ofNullable(this.reference.get());
    }

    @Override
    public void setReference(@NotNull Exceptional<org.spongepowered.api.world.World> reference) {
        reference.ifPresent(world -> this.reference = new WeakReference<>(world));
    }

    @Override
    public Exceptional<org.spongepowered.api.world.World> constructInitialReference() {
        return Exceptional.of(Sponge.getServer().getWorld(this.getWorldUniqueId()));
    }

    public org.spongepowered.api.world.World getReferenceWorld() {
        return this.getReference().orNull();
    }

    @Override
    public void setGamerule(String key, String value) {
        if (this.referenceExists()) {
            this.getReference().get().getProperties().setGameRule(key, value);
        }
    }

    @Override
    public boolean getLoadOnStartup() {
        if (this.referenceExists()) {
            return this.getReference().get().getProperties().loadOnStartup();
        }
        else return false;
    }

    @Override
    public void setLoadOnStartup(boolean loadOnStartup) {
        if (this.referenceExists()) {
            this.getReference().get().getProperties().setLoadOnStartup(loadOnStartup);
        }
    }

    @NotNull
    @Override
    public Vector3N getSpawnPosition() {
        if (this.referenceExists()) {
            Vector3i vector3i = this.getReference().get().getProperties().getSpawnPosition();
            return new Vector3N(vector3i.getX(), vector3i.getY(), vector3i.getZ());
        }
        else return new Vector3N(0, 0, 0);
    }

    @Override
    public void setSpawnPosition(@NotNull Vector3N spawnPosition) {
        if (this.referenceExists()) {
            this.getReference()
                    .get()
                    .getProperties()
                    .setSpawnPosition(
                            new Vector3i(spawnPosition.getXi(), spawnPosition.getYi(), spawnPosition.getZi()));
        }
    }

    @Override
    public long getSeed() {
        if (this.referenceExists()) {
            return this.getReference().get().getProperties().getSeed();
        }
        else return 0;
    }

    @Override
    public void setSeed(long seed) {
        if (this.referenceExists()) {
            this.getReference().get().getProperties().setSeed(seed);
        }
    }

    @NotNull
    @Override
    public Gamemode getDefaultGamemode() {
        if (this.referenceExists()) {
            return SpongeConversionUtil.fromSponge(
                    this.getReference().get().getProperties().getGameMode());
        }
        else return Gamemode.OTHER;
    }

    @Override
    public void setDefaultGamemode(@NotNull Gamemode defaultGamemode) {
        if (this.referenceExists()) {
            this.getReference()
                    .get()
                    .getProperties()
                    .setGameMode(SpongeConversionUtil.toSponge(defaultGamemode));
        }
    }

    @Override
    public Map<String, String> getGamerules() {
        if (this.referenceExists()) {
            return this.getReference().get().getProperties().getGameRules();
        }
        return SeleneUtils.emptyMap();
    }
}
