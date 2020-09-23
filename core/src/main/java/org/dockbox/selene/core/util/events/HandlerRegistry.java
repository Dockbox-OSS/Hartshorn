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

package org.dockbox.selene.core.util.events;

import org.dockbox.selene.core.objects.events.Event;

import java.util.HashMap;
import java.util.Map;

public final class HandlerRegistry {
    private final Map<Class<? extends Event>, Handler> handlers = new HashMap<>();

    public Handler getHandler(Class<? extends Event> type) {
        Handler handler = handlers.get(type);
        if (handler == null) {
            computeHierarchy(handler = new Handler(type));
            handlers.put(type, handler);
        }
        return handler;
    }

    boolean computeHierarchy(Handler subject) {
        boolean associationFound = false;
        for (Handler handler : handlers.values()) {
            if (subject == handler) continue;
            if (subject.isSubtypeOf(handler)) {
                associationFound |= subject.addSupertypeHandler(handler);
            } else if (handler.isSubtypeOf(subject)) {
                associationFound |= handler.addSupertypeHandler(subject);
            }
        }
        return associationFound;
    }

    public Map<Class<? extends Event>, Handler> getHandlers() {
        return this.handlers;
    }
}
