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

package org.dockbox.selene.server.minecraft.events.player;

import org.dockbox.selene.api.domain.Target;
import org.dockbox.selene.minecraft.dimension.position.Location;
import org.dockbox.selene.minecraft.dimension.world.World;

/**
 * The event fired when a player switches to another world. Typically this is fired after {@link
 * PlayerTeleportEvent}.
 */
public class PlayerSwitchWorldEvent extends PlayerTeleportEvent {
    private final World oldWorld;
    private final World newWorld;

    public PlayerSwitchWorldEvent(Target target, Location oldLocation, Location newLocation) {
        super(target, oldLocation, newLocation);
        this.oldWorld = oldLocation.getWorld();
        this.newWorld = newLocation.getWorld();
    }

    public PlayerSwitchWorldEvent(Target target, World oldWorld, World newWorld) {
        super(target, Location.of(oldWorld), Location.of(newWorld));
        this.oldWorld = oldWorld;
        this.newWorld = newWorld;
    }

    public World getOldWorld() {
        return this.oldWorld;
    }

    public World getNewWorld() {
        return this.newWorld;
    }
}
