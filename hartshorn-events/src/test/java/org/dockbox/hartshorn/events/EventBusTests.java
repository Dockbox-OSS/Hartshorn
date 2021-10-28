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

import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.events.annotations.Listener.Priority;
import org.dockbox.hartshorn.events.listeners.BasicEventListener;
import org.dockbox.hartshorn.events.listeners.GenericEventListener;
import org.dockbox.hartshorn.events.listeners.PriorityEventListener;
import org.dockbox.hartshorn.events.listeners.StaticEventListener;
import org.dockbox.hartshorn.events.parents.Event;
import org.dockbox.hartshorn.testsuite.ApplicationAwareTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class EventBusTests extends ApplicationAwareTest {

    @Test
    public void testTypesCanSubscribe() {
        final EventBus bus = this.bus();
        bus.subscribe(TypeContext.of(BasicEventListener.class));
        Assertions.assertTrue(bus.invokers().containsKey(TypeContext.of(BasicEventListener.class)));
    }

    private EventBus bus() {
        return this.context().get(EventBusImpl.class);
    }

    @Test
    public void testNonStaticMethodsCanListen() {
        final EventBus bus = this.bus();
        bus.subscribe(TypeContext.of(BasicEventListener.class));
        bus.post(new SampleEvent());
        Assertions.assertTrue(BasicEventListener.fired);
    }

    @Test
    public void testStaticMethodsCanListen() {
        final EventBus bus = this.bus();
        bus.subscribe(TypeContext.of(StaticEventListener.class));
        bus.post(new SampleEvent());
        Assertions.assertTrue(StaticEventListener.fired);
    }

    @Test
    public void testEventsArePostedInCorrectPriorityOrder() {
        final EventBus bus = this.bus();
        bus.subscribe(TypeContext.of(PriorityEventListener.class));
        bus.post(new SampleEvent());
        Assertions.assertEquals(Priority.LAST, PriorityEventListener.last());
    }

    @Test
    void testGenericEventsAreFiltered() {
        final EventBus bus = this.bus();
        bus.subscribe(TypeContext.of(GenericEventListener.class));
        final Event event = new GenericEvent<>("String") {
        };
        Assertions.assertDoesNotThrow(() -> bus.post(event));
    }

    @Test
    void testGenericWildcardsArePosted() {
        final EventBus bus = this.bus();
        // Ensure the values have not been affected by previous tests
        GenericEventListener.objects().clear();
        bus.subscribe(TypeContext.of(GenericEventListener.class));
        final Event stringEvent = new GenericEvent<>("String") {
        };
        final Event integerEvent = new GenericEvent<>(1) {
        };
        bus.post(stringEvent);
        bus.post(integerEvent);
        final List<Object> objects = GenericEventListener.objects();
        Assertions.assertEquals(2, objects.size());
        Assertions.assertEquals("String", objects.get(0));
        Assertions.assertEquals(1, objects.get(1));
    }

    @AfterEach
    public void reset() {
        BasicEventListener.fired = false;
        StaticEventListener.fired = false;
    }

}
