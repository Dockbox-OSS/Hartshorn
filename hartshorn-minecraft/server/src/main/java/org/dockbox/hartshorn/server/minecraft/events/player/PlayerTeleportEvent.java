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

import lombok.Getter;
import lombok.Setter;

/** The event fired when a player is teleported to another location */
@Getter
public class PlayerTeleportEvent extends PlayerMoveEvent {

    @Setter private Location destination;
    private final Location origin;

    public PlayerTeleportEvent(Subject subject, Location origin, Location destination) {
        super(subject);
        this.origin = origin;
        this.destination = destination;
    }
}
