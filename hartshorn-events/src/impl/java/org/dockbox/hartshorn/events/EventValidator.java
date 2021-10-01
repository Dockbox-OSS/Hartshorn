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

import org.dockbox.hartshorn.boot.Hartshorn;
import org.dockbox.hartshorn.boot.ServerState.Started;
import org.dockbox.hartshorn.boot.annotations.PostBootstrap;
import org.dockbox.hartshorn.boot.annotations.UseBootstrap;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.events.annotations.Posting;
import org.dockbox.hartshorn.events.annotations.UseEvents;
import org.dockbox.hartshorn.events.parents.Event;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service(activators = { UseBootstrap.class, UseEvents.class })
public class EventValidator {

    @PostBootstrap
    public void validate(final ApplicationContext context) {
        final List<TypeContext<? extends Event>> allEvents = context.environment().children(Event.class)
                .stream()
                .filter(type -> !type.isAbstract())
                // Anonymous classes indicate the event carries type parameters when posted (e.g. EngineChangedState<State>)
                // These are only created when the event is posted, so they can be ignored here, as they are not explicit
                // definitions.
                .filter(type -> !type.isAnonymous())
                .toList();
        final List<TypeContext<? extends Event>> postedEvents = HartshornUtils.emptyList();

        for (final TypeContext<?> bridge : context.environment().types(Posting.class)) {
            final Posting posting = bridge.annotation(Posting.class).get();
            postedEvents.addAll(Arrays.stream(posting.value()).map(TypeContext::of).collect(Collectors.toList()));
        }

        final Set<TypeContext<? extends Event>> difference = HartshornUtils.difference(allEvents, postedEvents);

        if (!difference.isEmpty()) {
            final StringBuilder message = new StringBuilder(difference.size() + " events are not handled by any event bridge!");

            if (!context.environment().isCI()) {
                for (final TypeContext<? extends Event> event : difference) {
                    message.append("\n\t- ").append(event.name());
                }
            }
            Hartshorn.log().warn(message.toString());
        }

        new EngineChangedState<Started>() {}.with(context).post();
    }

}
