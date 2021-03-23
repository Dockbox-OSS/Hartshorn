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

package org.dockbox.selene.api.events.player.interact;

import org.dockbox.selene.api.events.player.PlayerMoveEvent;
import org.dockbox.selene.api.objects.location.position.Location;
import org.dockbox.selene.api.objects.targets.Target;

/** The event fired when a player is teleported to the spawn location. */
// TODO: Implementation (Sponge-1.12)
public class PlayerSpawnEvent extends PlayerMoveEvent {
    private final Location spawnLocation;

    public PlayerSpawnEvent(Target target, Location spawnLocation) {
        super(target);
        this.spawnLocation = spawnLocation;
    }

    public Location getSpawnLocation() {
        return this.spawnLocation;
    }
}
