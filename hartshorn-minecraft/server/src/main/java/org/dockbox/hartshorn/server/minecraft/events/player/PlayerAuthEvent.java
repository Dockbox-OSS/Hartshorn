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

import org.dockbox.hartshorn.server.minecraft.players.Player;

import java.net.InetSocketAddress;

import lombok.Getter;

/** The event fired when a remote location is attempting to authenticate to the server. */
@Getter
public class PlayerAuthEvent extends PlayerConnectionEvent {
    private final InetSocketAddress address;
    private final InetSocketAddress host;

    public PlayerAuthEvent(InetSocketAddress address, InetSocketAddress host) {
        super(null);
        this.address = address;
        this.host = host;
    }

    @Override
    public Player getTarget() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot get target while authenticating");
    }
}
