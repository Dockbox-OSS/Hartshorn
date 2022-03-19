/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.events.handle;

import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.events.parents.Event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class EventHandlerRegistry {

    private final Map<TypeContext<? extends Event>, EventHandler> handlers = new ConcurrentHashMap<>();

    public Map<TypeContext<? extends Event>, EventHandler> handlers() {
        return this.handlers;
    }

    public EventHandler handler(final TypeContext<? extends Event> type) {
        EventHandler handler = this.handlers.get(type);
        if (null == handler) {
            this.computeHierarchy(handler = new EventHandler(type));
            this.handlers.put(type, handler);
        }
        return handler;
    }

    public void computeHierarchy(final EventHandler subject) {
        for (final EventHandler handler : this.handlers.values()) {
            if (subject == handler) continue;
            if (subject.subtypeOf(handler)) subject.addSuperTypeHandler(handler);
            else if (handler.subtypeOf(subject)) handler.addSuperTypeHandler(subject);
        }
    }
}
