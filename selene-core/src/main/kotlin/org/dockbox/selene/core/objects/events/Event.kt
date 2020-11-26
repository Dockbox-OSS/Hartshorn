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

package org.dockbox.selene.core.objects.events

import com.google.common.eventbus.EventBus
import org.dockbox.selene.core.server.Selene


/**
 * A low level type which is used when subscribing to, posting, or modifying events.
 */
interface Event {
    /**
     * Posts the event directly to the implementation of [EventBus], obtained through [Selene.getInstance]
     *
     * @return Itself
     */
    fun post(): Event {
        Selene.getInstance(EventBus::class.java).post(this)
        return this
    }
}
