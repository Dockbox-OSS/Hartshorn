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

package org.dockbox.hartshorn.api.events.processing;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.annotations.PostBootstrap;
import org.dockbox.hartshorn.api.annotations.UseBootstrap;
import org.dockbox.hartshorn.api.events.annotations.Posting;
import org.dockbox.hartshorn.api.events.parents.Event;
import org.dockbox.hartshorn.di.annotations.Service;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service(activator = UseBootstrap.class)
public class EventValidator {

    @PostBootstrap
    public void validate() {
        final List<Class<? extends Event>> allEvents = Reflect.children(Hartshorn.PACKAGE_PREFIX, Event.class)
                .stream()
                .filter(type -> !Reflect.isAbstract(type))
                .toList();
        List<Class<? extends Event>> postedEvents = HartshornUtils.emptyList();

        for (Class<?> bridge : Reflect.types(Hartshorn.PACKAGE_PREFIX, Posting.class)) {
            final Posting posting = bridge.getAnnotation(Posting.class);
            postedEvents.addAll(Arrays.asList(posting.value()));
        }

        Set<Class<? extends Event>> difference = HartshornUtils.difference(allEvents, postedEvents);

        if (!difference.isEmpty()) {
            StringBuilder message = new StringBuilder(difference.size() + " events are not handled by any event bridge!");
            for (Class<? extends Event> event : difference) {
                message.append("\n\t- ").append(event.getSimpleName());
            }
            Hartshorn.log().warn(message.toString());
        }
    }

}
