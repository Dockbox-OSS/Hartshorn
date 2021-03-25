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

package org.dockbox.selene.test.services;

import org.dockbox.selene.api.Worlds;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.location.dimensions.World;
import org.dockbox.selene.api.objects.player.Gamemode;
import org.dockbox.selene.api.objects.tuple.Vector3N;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.test.objects.JUnitWorld;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JUnitWorlds implements Worlds {

    // Seed is equal to 'selene' represented by a long
    public static World ROOT_WORLD = new JUnitWorld(UUID.randomUUID(), "world",
            true, Vector3N.empty(), -906021310L, Gamemode.SURVIVAL
    );

    // Seed is equal to 'junit' represented by a long
    public static World SECOND_WORLD = new JUnitWorld(UUID.randomUUID(), "second",
            false,Vector3N.empty(), 101487854L, Gamemode.CREATIVE);

    @Override
    public List<World> getLoadedWorlds() {
        return SeleneUtils.asList(World::isLoaded, ROOT_WORLD, SECOND_WORLD);
    }

    @Override
    public List<UUID> getAllWorldUUIDs() {
        return SeleneUtils.asList(ROOT_WORLD, SECOND_WORLD).stream()
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
        return this.getWorld(name).isPresent();
    }

    @Override
    public boolean hasWorld(UUID uuid) {
        return this.getWorld(uuid).isPresent();
    }

    @Override
    public UUID getRootWorldId() {
        return ROOT_WORLD.getWorldUniqueId();
    }
}
