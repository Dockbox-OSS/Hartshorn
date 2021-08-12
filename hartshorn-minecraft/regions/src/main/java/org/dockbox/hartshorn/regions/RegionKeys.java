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

package org.dockbox.hartshorn.regions;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.keys.Key;
import org.dockbox.hartshorn.api.keys.Keys;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.players.Player;

public final class RegionKeys {

    public static final Key<Location, Region> REGION = Keys.builder(Location.class, Region.class)
            .withGetterSafe(loc -> Hartshorn.context().get(RegionService.class).first(loc, Region.class))
            .build();

    public static final Key<Player, Region> CURRENT_REGION = Keys.builder(Player.class, Region.class)
            .withGetterSafe(player -> Hartshorn.context().get(RegionService.class).first(player, Region.class))
            .build();

    private RegionKeys() {
    }
}
