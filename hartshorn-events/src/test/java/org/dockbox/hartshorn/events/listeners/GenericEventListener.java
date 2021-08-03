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

import org.dockbox.hartshorn.events.GenericEvent;
import org.dockbox.hartshorn.events.annotations.Listener;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.List;

import lombok.Getter;

public class GenericEventListener {

    @Getter
    private static final List<Object> objects = HartshornUtils.emptyList();

    @Listener
    public void onString(GenericEvent<String> event) {
        if (event.value() instanceof String) return;
        throw new IllegalArgumentException("Expected type to be String, but got " + event.value().getClass());
    }

    @Listener
    public void onInteger(GenericEvent<Integer> event) {
        if (event.value() instanceof Integer) return;
        throw new IllegalArgumentException("Expected type to be Integer, but got " + event.value().getClass());
    }

    @Listener
    public void on(GenericEvent<?> event) {
        objects.add(event.value());
    }

}
