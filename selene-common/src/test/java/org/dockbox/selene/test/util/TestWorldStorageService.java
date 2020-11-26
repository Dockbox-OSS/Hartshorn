/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.test.util;

import org.dockbox.selene.core.objects.location.World;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.core.util.world.WorldStorageService;
import org.dockbox.selene.test.object.TestWorld;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TestWorldStorageService extends WorldStorageService {

    private final Collection<World> worlds = SeleneUtils.singletonList(new TestWorld(UUID.randomUUID(), "MockWorld"));

    @NotNull
    @Override
    public List<World> getLoadedWorlds() {
        return this.worlds.stream().filter(World::isLoaded).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public List<UUID> getAllWorldUUIDs() {
        return this.worlds.stream().map(World::getWorldUniqueId).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Exceptional<World> getWorld(@NotNull String name) {
        return Exceptional.empty();
    }

    @NotNull
    @Override
    public Exceptional<World> getWorld(@NotNull UUID uuid) {
        return Exceptional.empty();
    }

}
