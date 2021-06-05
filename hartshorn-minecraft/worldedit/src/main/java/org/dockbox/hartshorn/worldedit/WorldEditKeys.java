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

package org.dockbox.hartshorn.worldedit;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.keys.Key;
import org.dockbox.hartshorn.api.keys.Keys;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.worldedit.region.Clipboard;
import org.dockbox.hartshorn.worldedit.region.Region;

public final class WorldEditKeys {

    public static final Key<Player, Region> SELECTION = Keys.of(
            (player, region) -> Hartshorn.context().get(WorldEditService.class).setPlayerSelection(player, region),
            player -> Hartshorn.context().get(WorldEditService.class).getPlayerSelection(player)
    );

    public static final Key<Player, Clipboard> CLIPBOARD = Keys.of(
            (player, clipboard) -> Hartshorn.context().get(WorldEditService.class).setPlayerClipboard(player, clipboard),
            player -> Hartshorn.context().get(WorldEditService.class).getPlayerClipboard(player));

    private WorldEditKeys() {}
}
