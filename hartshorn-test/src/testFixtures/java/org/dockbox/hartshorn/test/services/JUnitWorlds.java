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
import org.dockbox.hartshorn.server.minecraft.dimension.Worlds;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.players.Gamemode;
import org.dockbox.hartshorn.test.objects.JUnitWorld;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JUnitWorlds implements Worlds {

    // Seed is equal to 'hartshorn' represented by a long
    public static World ROOT_WORLD = new JUnitWorld(UUID.randomUUID(), "world",
            true, Vector3N.empty(), -906021310L, Gamemode.SURVIVAL
    );

    // Seed is equal to 'junit' represented by a long
    public static World SECOND_WORLD = new JUnitWorld(UUID.randomUUID(), "second",
            false,Vector3N.empty(), 101487854L, Gamemode.CREATIVE);

    @Override
    public List<World> getLoadedWorlds() {
        return HartshornUtils.asList(World::isLoaded, ROOT_WORLD, SECOND_WORLD);
    }

    @Override
    public List<UUID> getAllWorldUUIDs() {
        return HartshornUtils.asList(ROOT_WORLD, SECOND_WORLD).stream()
                .map(World::getWorldUniqueId)
                .collect(Collectors.toList());
    }

    @Override
    public Exceptional<World> getWorld(String name) {
        if (ROOT_WORLD.getName().equals(name)) return Exceptional.of(ROOT_WORLD);
        else if (SECOND_WORLD.getName().equals(name)) return Exceptional.of(SECOND_WORLD);
        else return Exceptional.empty();
    }

    @Override
    public Exceptional<World> getWorld(UUID uuid) {
        if (ROOT_WORLD.getWorldUniqueId().equals(uuid)) return Exceptional.of(ROOT_WORLD);
        else if (SECOND_WORLD.getWorldUniqueId().equals(uuid)) return Exceptional.of(SECOND_WORLD);
        else return Exceptional.empty();
    }

    @Override
    public boolean hasWorld(String name) {
        return this.getWorld(name).present();
    }

    @Override
    public boolean hasWorld(UUID uuid) {
        return this.getWorld(uuid).present();
    }

    @Override
    public UUID getRootWorldId() {
        return ROOT_WORLD.getWorldUniqueId();
    }
}
