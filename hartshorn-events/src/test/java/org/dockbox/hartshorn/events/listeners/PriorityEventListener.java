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

package org.dockbox.hartshorn.events.listeners;

import org.dockbox.hartshorn.events.SampleEvent;
import org.dockbox.hartshorn.events.annotations.Listener;
import org.dockbox.hartshorn.events.annotations.Listener.Priority;
import org.junit.jupiter.api.Assertions;

import lombok.Getter;

public class PriorityEventListener {

    @Getter
    private static Priority last = null;

    @Listener(Priority.FIRST)
    public void onFirst(SampleEvent event) {
        Assertions.assertNull(PriorityEventListener.last);
        PriorityEventListener.last = Priority.FIRST;
    }

    @Listener(Priority.EARLY)
    public void onEarly(SampleEvent event) {
        Assertions.assertEquals(PriorityEventListener.last, Priority.FIRST);
        PriorityEventListener.last = Priority.EARLY;
    }

    @Listener(Priority.NORMAL)
    public void onNormal(SampleEvent event) {
        Assertions.assertEquals(PriorityEventListener.last, Priority.EARLY);
        PriorityEventListener.last = Priority.NORMAL;
    }

    @Listener(Priority.LATE)
    public void onLate(SampleEvent event) {
        Assertions.assertEquals(PriorityEventListener.last, Priority.NORMAL);
        PriorityEventListener.last = Priority.LATE;
    }

    @Listener(Priority.LAST)
    public void onLast(SampleEvent event) {
        Assertions.assertEquals(PriorityEventListener.last, Priority.LATE);
        PriorityEventListener.last = Priority.LAST;
    }
}
