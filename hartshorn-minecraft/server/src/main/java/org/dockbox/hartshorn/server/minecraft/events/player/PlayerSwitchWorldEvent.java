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

package org.dockbox.hartshorn.server.minecraft.events.player;

import org.dockbox.hartshorn.api.domain.Subject;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;

/**
 * The event fired when a player switches to another world. Typically this is fired after {@link
 * PlayerTeleportEvent}.
 */
public class PlayerSwitchWorldEvent extends PlayerMoveEvent {
    private final World origin;
    private final World destination;

    public PlayerSwitchWorldEvent(Subject subject, Location origin, Location destination) {
        super(subject);
        this.origin = origin.world();
        this.destination = destination.world();
    }

    public PlayerSwitchWorldEvent(Subject subject, World origin, World destination) {
        super(subject);
        this.origin = origin;
        this.destination = destination;
    }

    public World origin() {
        return this.origin;
    }

    public World destination() {
        return this.destination;
    }
}
