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

package org.dockbox.selene.core.events.player;

import org.dockbox.selene.core.events.AbstractTargetEvent;
import org.dockbox.selene.core.objects.targets.Target;

import java.net.InetSocketAddress;

/**
 * The abstract type which can be used to listen to all player movement related events.
 */
public abstract class PlayerConnectionEvent extends AbstractTargetEvent {

    protected PlayerConnectionEvent(Target target) {
        super(target);
    }

    @Override
    public void setTarget(Target target) {
        throw new UnsupportedOperationException("Cannot change target of connection event");
    }

    /**
     * The event fired when a player connected to the server.
     */
    public static class PlayerJoinEvent extends PlayerConnectionEvent {
        public PlayerJoinEvent(Target target) {
            super(target);
        }
    }

    /**
     * The event fired when a player disconnected from the server.
     */
    public static class PlayerLeaveEvent extends PlayerConnectionEvent {
        public PlayerLeaveEvent(Target target) {
            super(target);
        }
    }

    /**
     The event fired when a remote location is attempting to authenticate to the server.
     */
    public static class PlayerAuthEvent extends PlayerConnectionEvent {
        private final InetSocketAddress address;
        private final InetSocketAddress host;

        public PlayerAuthEvent(InetSocketAddress address, InetSocketAddress host) {
            super(null);
            this.address = address;
            this.host = host;
        }

        public InetSocketAddress getAddress() {
            return this.address;
        }

        public InetSocketAddress getHost() {
            return this.host;
        }

        @Override
        public Target getTarget() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Cannot get target while authenticating");
        }
    }

}
