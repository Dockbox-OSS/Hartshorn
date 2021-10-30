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

package org.dockbox.hartshorn.events;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ServiceProcessor;
import org.dockbox.hartshorn.events.annotations.Listener;
import org.dockbox.hartshorn.events.annotations.UseEvents;

public class EventServiceProcessor implements ServiceProcessor<UseEvents> {
    @Override
    public boolean preconditions(final ApplicationContext context, final TypeContext<?> type) {
        return !type.methods(Listener.class).isEmpty();
    }

    @Override
    public <T> void process(final ApplicationContext context, final TypeContext<T> type) {
        context.get(EventBus.class).subscribe(type);
    }

    @Override
    public Class<UseEvents> activator() {
        return UseEvents.class;
    }
}
