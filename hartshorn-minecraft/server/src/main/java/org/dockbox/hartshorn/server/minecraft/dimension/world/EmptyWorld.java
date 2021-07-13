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

package org.dockbox.hartshorn.server.minecraft.dimension.world;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.server.minecraft.dimension.Block;
import org.dockbox.hartshorn.server.minecraft.dimension.Chunk;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.entities.Entity;
import org.dockbox.hartshorn.server.minecraft.players.Gamemode;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

final class EmptyWorld extends World {
    EmptyWorld() {
        super(HartshornUtils.EMPTY_UUID, "Empty", false, Vector3N.empty(), -1, Gamemode.OTHER);
    }

    @Override
    public int playerCount() {
        return 0;
    }

    @Override
    public boolean unload() {
        return false;
    }

    @Override
    public boolean load() {
        return false;
    }

    @Override
    public boolean loaded() {
        return false;
    }

    @Override
    public Vector3N minimumPosition() {
        return Vector3N.empty();
    }

    @Override
    public Vector3N maximumPosition() {
        return Vector3N.empty();
    }

    @Override
    public Vector3N floor(Vector3N position) {
        return Vector3N.empty();
    }

    @Override
    public boolean has(Vector3N position) {
        return false;
    }

    @Override
    public Exceptional<Block> block(Vector3N position) {
        return Exceptional.empty();
    }

    @Override
    public boolean block(Vector3N position, Block item) {
        return false;
    }

    @Override
    public void gamerule(String key, String value) {}

    @Override
    public Map<String, String> gamerules() {
        return HartshornUtils.emptyMap();
    }

    @Override
    public Collection<Entity> entities() {
        return HartshornUtils.emptyList();
    }

    @Override
    public Collection<Entity> entities(Predicate<Entity> predicate) {
        return HartshornUtils.emptyList();
    }

    @Override
    public Exceptional<Chunk> chunk(Location location) {
        return Exceptional.empty();
    }

    @Override
    public Exceptional<Chunk> chunk(Vector3N position) {
        return Exceptional.empty();
    }

    @Override
    public Collection<Chunk> loadedChunks() {
        return HartshornUtils.emptyList();
    }
}
