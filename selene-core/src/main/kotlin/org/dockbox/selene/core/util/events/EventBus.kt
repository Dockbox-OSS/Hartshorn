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

package org.dockbox.selene.core.util.events

import java.lang.invoke.MethodHandles
import org.dockbox.selene.core.objects.events.Event

interface EventBus {

    fun subscribe(`object`: Any)
    fun subscribe(`object`: Any, lookup: MethodHandles.Lookup)
    fun unsubscribe(`object`: Any)
    fun post(event: Event, target: Class<*>)
    fun post(event: Event)
    fun getHandlerRegistry(): IHandlerRegistry
    fun getListenerToInvokers(): Map<Any, Set<IWrapper>>

}
