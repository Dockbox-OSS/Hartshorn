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

package org.dockbox.selene.api.events.player;

import org.dockbox.selene.api.objects.location.Warp;
import org.dockbox.selene.api.objects.targets.Target;

/** The event fired when a player is teleported using a {@link Warp}. */
public class PlayerWarpEvent extends PlayerMoveEvent {
    private final Warp warp;

    public PlayerWarpEvent(Target target, Warp warp) {
        super(target);
        this.warp = warp;
    }

    public Warp getWarp() {
        return this.warp;
    }
}
