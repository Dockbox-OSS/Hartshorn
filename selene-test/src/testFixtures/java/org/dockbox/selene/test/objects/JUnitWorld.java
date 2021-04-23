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

package org.dockbox.selene.test.objects;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.domain.tuple.Vector3N;
import org.dockbox.selene.server.minecraft.dimension.Chunk;
import org.dockbox.selene.server.minecraft.dimension.position.BlockFace;
import org.dockbox.selene.server.minecraft.dimension.position.Location;
import org.dockbox.selene.server.minecraft.dimension.world.World;
import org.dockbox.selene.server.minecraft.entities.Entity;
import org.dockbox.selene.server.minecraft.item.Item;
import org.dockbox.selene.server.minecraft.players.Gamemode;
import org.dockbox.selene.server.minecraft.players.Player;
import org.dockbox.selene.server.minecraft.players.Profile;
import org.dockbox.selene.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JUnitWorld extends World {

    private final Map<Vector3N, Item> blocks = SeleneUtils.emptyMap();
    private final Map<UUID, Entity> entities = SeleneUtils.emptyMap();
    private final Map<String, String> gamerules = SeleneUtils.emptyMap();
    private boolean isLoaded;

    public JUnitWorld(UUID worldUniqueId, String name, boolean loadOnStartup, @NotNull Vector3N spawnPosition, long seed, Gamemode defaultGamemode) {
        super(worldUniqueId, name, loadOnStartup, spawnPosition, seed, defaultGamemode);
        this.isLoaded = loadOnStartup;
    }

    @Override
    public Vector3N minimumPosition() {
        return Vector3N.of(-16, 0, -16);
    }

    @Override
    public Vector3N maximumPosition() {
        return Vector3N.of(16, 256, 16);
    }

    @Override
    public Vector3N floor(Vector3N position) {
        return Vector3N.of(position.getXd(), 64, position.getZd());
    }

    @Override
    public boolean hasBlock(Vector3N position) {
        return this.blocks.containsKey(position);
    }

    @Override
    public Exceptional<Item> getBlock(Vector3N position) {
        return Exceptional.of(this.blocks.getOrDefault(position, null));
    }

    @Override
    public boolean setBlock(Vector3N position, Item item, BlockFace direction, Profile placer) {
        this.blocks.put(position, item);
        return true;
    }

    @Override
    public Exceptional<Chunk> getChunk(Location location) {
        return Exceptional.none();
    }

    @Override
    public Exceptional<Chunk> getChunk(int x, int y) {
        return Exceptional.none();
    }

    @Override
    public Collection<Chunk> getLoadedChunks() {
        return SeleneUtils.emptyList();
    }

    @Override
    public Collection<Entity> getEntities() {
        return this.entities.values();
    }

    @Override
    public Collection<Entity> getEntities(Predicate<Entity> predicate) {
        return this.getEntities().stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public int getPlayerCount() {
        return this.getEntities(entity -> entity instanceof Player).size();
    }

    @Override
    public boolean unload() {
        this.isLoaded = false;
        return true;
    }

    @Override
    public boolean load() {
        this.isLoaded = true;
        return true;
    }

    @Override
    public boolean isLoaded() {
        return this.isLoaded;
    }

    @Override
    public void setGamerule(String key, String value) {
        this.gamerules.put(key, value);
    }

    @Override
    public Map<String, String> getGamerules() {
        return this.gamerules;
    }

    public void addEntity(Entity entity) {
        this.entities.put(entity.getUniqueId(), entity);
    }

    public void destroyEntity(UUID uuid) {
        this.entities.remove(uuid);
    }
}
