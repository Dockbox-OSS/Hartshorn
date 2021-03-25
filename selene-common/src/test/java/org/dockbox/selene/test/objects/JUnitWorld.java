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

import org.dockbox.selene.api.entities.Entity;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.objects.location.dimensions.Chunk;
import org.dockbox.selene.api.objects.location.dimensions.World;
import org.dockbox.selene.api.objects.location.position.BlockFace;
import org.dockbox.selene.api.objects.location.position.Location;
import org.dockbox.selene.api.objects.player.Gamemode;
import org.dockbox.selene.api.objects.profile.Profile;
import org.dockbox.selene.api.objects.tuple.Vector3N;
import org.dockbox.selene.api.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class JUnitWorld extends World {

    private final Map<Vector3N, Item> blocks = SeleneUtils.emptyMap();
    private final Map<UUID, Entity<?>> entities = SeleneUtils.emptyMap();
    private boolean isLoaded;

    public JUnitWorld(UUID worldUniqueId, String name, boolean loadOnStartup, @NotNull Vector3N spawnPosition, long seed, Gamemode defaultGamemode) {
        super(worldUniqueId, name, loadOnStartup, spawnPosition, seed, defaultGamemode);
        this.isLoaded = loadOnStartup;
    }

    @Override
    public Vector3N minimumPosition() {
        return Vector3N.of(-10, 0, -10);
    }

    @Override
    public Vector3N maximumPosition() {
        return Vector3N.of(10, 256, 10);
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
        return Exceptional.ofNullable(this.blocks.getOrDefault(position, null));
    }

    @Override
    public boolean setBlock(Vector3N position, Item item, BlockFace direction, Profile placer) {
        this.blocks.put(position, item);
        return true;
    }

    // TODO: Finish implementation

    @Override
    public Exceptional<Chunk> getChunk(Location location) {
        return null;
    }

    @Override
    public Exceptional<Chunk> getChunk(int x, int y) {
        return null;
    }

    @Override
    public Collection<Chunk> getLoadedChunks() {
        return null;
    }

    @Override
    public Collection<Entity<?>> getEntities() {
        return this.entities.values();
    }

    @Override
    public Collection<Entity<?>> getEntities(Predicate<Entity<?>> predicate) {
        return null;
    }

    @Override
    public int getPlayerCount() {
        return 0;
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

    }

    @Override
    public Map<String, String> getGamerules() {
        return null;
    }

    public void addEntity(Entity<?> entity) {
        this.entities.put(entity.getUniqueId(), entity);
    }

    public void destroyEntity(UUID uuid) {
        this.entities.remove(uuid);
    }
}
