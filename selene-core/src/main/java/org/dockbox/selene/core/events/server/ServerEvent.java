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

package org.dockbox.selene.core.events.server;

import org.dockbox.selene.core.events.parents.Event;

/**
 * The abstract type which can be used to listen to all server related events.
 */
public abstract class ServerEvent implements Event {

    /**
     * The event fired when the server is initiating/initializing. Typically this is the first event to be fired.
     */
    public static class ServerInitEvent extends ServerEvent {
    }

    /**
     * The event fired when the server is done initiating/initializing. Typically this is fired after
     * {@link ServerInitEvent} and before {@link ServerStartingEvent}.
     */
    public static class ServerPostInitEvent extends ServerEvent {
    }

    /**
     * The event fired when a server-wide reload is performed.
     */
    public static class ServerReloadEvent extends ServerEvent {
    }

    /**
     * The event fired when the server is starting. Typically this is fired after {@link ServerPostInitEvent} and before
     * {@link ServerStartedEvent}.
     */
    public static class ServerStartingEvent extends ServerEvent {
    }

    /**
     * The event fired when the server is done starting. Typically this is the last event to be fired.
     */
    public static class ServerStartedEvent extends ServerEvent {
    }

    /**
     * The event fired when the server is stopping.
     */
    public static class ServerStoppingEvent extends ServerEvent {
    }

}
