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

import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.di.ContextCarrier;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.server.minecraft.dimension.BlockDimension;
import org.dockbox.hartshorn.server.minecraft.dimension.ChunkHolder;
import org.dockbox.hartshorn.server.minecraft.dimension.EntityHolding;
import org.dockbox.hartshorn.server.minecraft.players.Gamemode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public abstract class World extends WorldProperties implements BlockDimension, EntityHolding, ChunkHolder, ContextCarrier {

    protected UUID worldUniqueId;
    protected String name;

    public World(
            final UUID worldUniqueId,
            final String name,
            final boolean loadOnStartup,
            @NotNull final Vector3N spawnPosition,
            final long seed,
            final Gamemode defaultGamemode
    ) {
        super(loadOnStartup, spawnPosition, seed, defaultGamemode);
        this.worldUniqueId = worldUniqueId;
        this.name = name;
    }

    public static World empty(final ApplicationContext context) {
        return new EmptyWorld(context);
    }

    public static WorldBuilder builder(final ApplicationContext context) {
        return context.get(WorldBuilder.class);
    }

    public abstract int playerCount();

    public abstract boolean unload();

    public abstract boolean load();

    public abstract boolean loaded();

    @Override
    public int hashCode() {
        return Objects.hash(this.worldUniqueId(), this.name());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof World world)) return false;
        return this.worldUniqueId().equals(world.worldUniqueId()) && this.name().equals(world.name());
    }

}
