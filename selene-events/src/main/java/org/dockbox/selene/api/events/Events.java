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

package org.dockbox.selene.api.events;

import org.dockbox.selene.api.events.parents.Event;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Events {

    /**
     * Returns a {@link List} of non-null events based on the provided {@link Event events}. This
     * should typically be used for event listeners with multiple event parameters.
     *
     * @param events
     *         The events
     *
     * @return The fired (non-null) events
     */
    public static List<Event> getFiredEvents(Event... events) {
        return Arrays.stream(events).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Returns the first fired event based on the provided {@link Event events}.
     *
     * @param events
     *         The events
     *
     * @return The first fired (non-null) event
     */
    @Contract(pure = true)
    @Nullable
    public static Event getFirstFiredEvent(Event... events) {
        for (Event event : events) {
            if (null != event) {
                return event;
            }
        }
        return null;
    }

}
