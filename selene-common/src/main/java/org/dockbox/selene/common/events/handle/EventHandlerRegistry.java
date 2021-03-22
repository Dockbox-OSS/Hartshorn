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

package org.dockbox.selene.common.events.handle;

import org.dockbox.selene.api.events.parents.Event;
import org.dockbox.selene.api.util.SeleneUtils;

import java.util.Map;

public final class EventHandlerRegistry {

    private final Map<Class<? extends Event>, EventHandler> handlers = SeleneUtils.emptyMap();

    public EventHandler getHandler(Class<? extends Event> type) {
        EventHandler handler = this.handlers.get(type);
        if (null == handler) {
            this.computeHierarchy(handler = new EventHandler(type));
            this.handlers.put(type, handler);
        }
        return handler;
    }

    public void computeHierarchy(EventHandler subject) {
        boolean associationFound = false;
        for (EventHandler handler : this.handlers.values()) {
            if (subject == handler) continue;
            if (subject.isSubtypeOf(handler)) {
                associationFound |= subject.addSupertypeHandler(handler);
            }
            else if (handler.isSubtypeOf(subject)) {
                associationFound |= handler.addSupertypeHandler(subject);
            }
        }
    }

    public Map<Class<? extends Event>, EventHandler> getHandlers() {
        return this.handlers;
    }
}
