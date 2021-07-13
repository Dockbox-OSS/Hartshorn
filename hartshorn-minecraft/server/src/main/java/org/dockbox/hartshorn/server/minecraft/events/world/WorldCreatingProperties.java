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

package org.dockbox.hartshorn.server.minecraft.events.world;

import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.server.minecraft.dimension.world.WorldProperties;
import org.dockbox.hartshorn.server.minecraft.players.Gamemode;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Map;
import java.util.UUID;

import lombok.Getter;

/** The available properties used when a world is being created or generated. */
@Getter
public class WorldCreatingProperties extends WorldProperties {

    private final String name;
    private final UUID uniqueId;

    private final Map<String, String> gamerules = HartshornUtils.emptyConcurrentMap();

    public WorldCreatingProperties(
            String name,
            UUID uniqueId,
            boolean loadOnStartup,
            Vector3N spawnPosition,
            long seed,
            Gamemode defaultGamemode,
            Map<String, String> gamerules
    ) {
        super(loadOnStartup, spawnPosition, seed, defaultGamemode);
        this.name = name;
        this.uniqueId = uniqueId;
        gamerules.forEach(this::gamerule);
    }

    @Override
    public void gamerule(String key, String value) {
        this.gamerules.put(key, value);
    }
}
