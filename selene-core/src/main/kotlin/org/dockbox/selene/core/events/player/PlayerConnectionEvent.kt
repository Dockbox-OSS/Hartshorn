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

package org.dockbox.selene.core.events.player

import java.net.InetSocketAddress
import org.dockbox.selene.core.events.parents.Event
import org.dockbox.selene.core.events.parents.Targetable
import org.dockbox.selene.core.objects.targets.Target

/**
 * The abstract type which can be used to listen to all player movement related events.
 *
 * @param target The target of the event
 */
abstract class PlayerConnectionEvent(private val target: Target?) : Targetable, Event {

    override fun getTarget(): Target {
        return this.target!!
    }

    override fun setTarget(target: Target) {
        throw UnsupportedOperationException("Cannot change target of connection event")
    }

    /**
     * The event fired when a player connected to the server.
     *
     * @param target The player targeted by the event
     */
    class PlayerJoinEvent(target: Target) : PlayerConnectionEvent(target)

    /**
     * The event fired when a player disconnected from the server.
     *
     * @constructor
     *
     * @param target The player targeted by the event
     */
    class PlayerLeaveEvent(target: Target) : PlayerConnectionEvent(target)

    /**
     * The event fired when a remote location is attempting to authenticate to the server.
     *
     * @constructor
     *
     * @param address The network address which is being authenticated to
     * @param host The network address of the host attempting to authenticate
     */
    class PlayerAuthEvent(address: InetSocketAddress, host: InetSocketAddress) : PlayerConnectionEvent(null) {

        override fun getTarget(): Target {
            throw UnsupportedOperationException("Cannot get target while authenticating")
        }

    }

}
