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

package org.dockbox.hartshorn.sponge.dim;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.server.minecraft.dimension.Block;
import org.dockbox.hartshorn.server.minecraft.dimension.Chunk;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.sponge.SpongeContextCarrier;
import org.dockbox.hartshorn.sponge.util.SpongeAdapter;
import org.dockbox.hartshorn.sponge.util.SpongeUtil;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.gamerule.GameRule;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.WorldManager;
import org.spongepowered.math.vector.Vector3i;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import lombok.Getter;

public class SpongeWorld extends World implements SpongeDimension, SpongeContextCarrier {

    @Getter private final ResourceKey key;

    public SpongeWorld(final ResourceKey key) {
        this.key = key;
        final ServerWorld serverWorld = this.serverWorld();
        super.worldUniqueId = serverWorld.uniqueId();
        super.name = serverWorld.key().value();
    }

    // TODO: Override WorldProperties so they are synced

    @Override
    public boolean has(final Vector3N position) {
        return this.world()
                .map(world -> world.contains(SpongeAdapter.toSponge(position)))
                .or(false);
    }

    @Override
    public Exceptional<Block> block(final Vector3N position) {
        return Exceptional.of(Block.from(new SpongeLocation(position, this)));
    }

    @Override
    public boolean block(final Vector3N position, final Block block) {
        return block.place(new SpongeLocation(position, this));
    }

    private Exceptional<ServerWorld> world() {
        return Exceptional.of(Sponge.server().worldManager().world(this.key));
    }

    @Override
    public Exceptional<Chunk> chunk(final Location location) {
        return this.chunk(location.vector());
    }

    @Override
    public Exceptional<Chunk> chunk(final Vector3N position) {
        final Vector3i vector3i = SpongeAdapter.toSponge(position);
        final boolean hasChunk = this.world().map(world -> world.hasChunk(vector3i)).or(false);
        if (hasChunk) Exceptional.of(new SpongeChunk(this.key, vector3i));
        return Exceptional.empty();
    }

    @Override
    public Collection<Chunk> loadedChunks() {
        final Collection<Chunk> chunks = HartshornUtils.emptyList();
        this.world().present(world -> {
            for (final org.spongepowered.api.world.chunk.Chunk<?> chunk : world.loadedChunks()) {
                chunks.add(new SpongeChunk(this.key, chunk.chunkPosition()));
            }
        });
        return HartshornUtils.asUnmodifiableCollection(chunks);
    }

    @Override
    public ServerWorld serverWorld() {
        return this.world().orNull();
    }

    @Override
    public int playerCount() {
        return this.world().map(ServerWorld::players).map(Collection::size).or(0);
    }

    @Override
    public boolean unload() {
        return this.run(WorldManager::unloadWorld).or(false);
    }

    @Override
    public boolean load() {
        return this.run(WorldManager::loadWorld)
                .map(org.spongepowered.api.world.World::isLoaded)
                .or(false);
    }

    @Override
    public boolean loaded() {
        return this.world()
                .map(org.spongepowered.api.world.World::isLoaded)
                .or(false);
    }

    private <T> Exceptional<T> run(final BiFunction<WorldManager, ResourceKey, CompletableFuture<T>> function) {
        final CompletableFuture<T> future = function.apply(Sponge.server().worldManager(), this.key);
        return SpongeUtil.await(future);
    }

    @Override
    public void gamerule(final String key, final String value) {
        this.world().present(world -> {
            //noinspection unchecked
            final GameRule<String> rule = (GameRule<String>) SpongeUtil.spongeReference(RegistryTypes.GAME_RULE, value);
            world.properties().setGameRule(rule, value);
        });
    }

    @Override
    public Map<String, String> gamerules() {
        final Map<String, String> rules = HartshornUtils.emptyMap();
        this.world().present(world -> world.properties().gameRules().forEach((rule, value) -> rules.put(rule.name(), String.valueOf(value))));
        return null;
    }
}
