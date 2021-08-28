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

package org.dockbox.hartshorn.test.services;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.server.minecraft.dimension.Worlds;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.players.Gamemode;
import org.dockbox.hartshorn.test.objects.JUnitWorld;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

@Service
public class JUnitWorlds implements Worlds {

    private final World root;
    private final Set<World> worlds = HartshornUtils.emptyConcurrentSet();

    @Inject
    public JUnitWorlds(final ApplicationContext context) {
        this.root = new JUnitWorld(context, UUID.randomUUID(), "world",
                true, Vector3N.empty(), -906021310L, Gamemode.SURVIVAL
        );
    }

    @Override
    public List<World> loadedWorlds() {
        return this.worlds.stream().filter(World::loaded).toList();
    }

    @Override
    public List<UUID> loadedUniqueIds() {
        return this.loadedWorlds().stream().map(World::worldUniqueId).toList();
    }

    @Override
    public Exceptional<World> world(final String name) {
        for (final World world : this.worlds) if (world.name().equalsIgnoreCase(name)) return Exceptional.of(world);
        return Exceptional.empty();
    }

    @Override
    public Exceptional<World> world(final UUID uuid) {
        for (final World world : this.worlds) if (world.worldUniqueId().equals(uuid)) return Exceptional.of(world);
        return Exceptional.empty();
    }

    @Override
    public boolean has(final String name) {
        return this.world(name).present();
    }

    @Override
    public boolean has(final UUID uuid) {
        return this.world(uuid).present();
    }

    @Override
    public UUID rootUniqueId() {
        return this.root.worldUniqueId();
    }
}
