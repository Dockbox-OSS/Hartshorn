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

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.events.parents.Event;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public interface EventBus {

    void subscribe(Class<?> object);

    void unsubscribe(Class<?> object);

    void post(Event event, Class<?> target);

    void post(Event event);

    Map<Class<?>, Set<EventWrapper>> getListenersToInvokers();

    void addValidationRule(Function<Method, Exceptional<Boolean>> validator);
}
