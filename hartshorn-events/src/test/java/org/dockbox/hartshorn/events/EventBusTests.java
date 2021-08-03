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

import org.dockbox.hartshorn.events.annotations.Listener.Priority;
import org.dockbox.hartshorn.events.listeners.BasicEventListener;
import org.dockbox.hartshorn.events.listeners.GenericEventListener;
import org.dockbox.hartshorn.events.listeners.PriorityEventListener;
import org.dockbox.hartshorn.events.listeners.StaticEventListener;
import org.dockbox.hartshorn.test.HartshornRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@ExtendWith(HartshornRunner.class)
public class EventBusTests {

    @Test
    public void testTypesCanSubscribe() {
        EventBus bus = this.bus();
        bus.subscribe(BasicEventListener.class);
        Assertions.assertTrue(bus.invokers().containsKey(BasicEventListener.class));
    }

    @Test
    public void testNonStaticMethodsCanListen() {
        EventBus bus = this.bus();
        bus.subscribe(BasicEventListener.class);
        bus.post(new SampleEvent());
        Assertions.assertTrue(BasicEventListener.fired);
    }

    @Test
    public void testStaticMethodsCanListen() {
        EventBus bus = this.bus();
        bus.subscribe(StaticEventListener.class);
        bus.post(new SampleEvent());
        Assertions.assertTrue(StaticEventListener.fired);
    }

    @Test
    public void testEventsArePostedInCorrectPriorityOrder() {
        EventBus bus = this.bus();
        bus.subscribe(PriorityEventListener.class);
        bus.post(new SampleEvent());
        Assertions.assertEquals(Priority.LAST, PriorityEventListener.last());
    }

    @Test
    void testGenericEventsAreFiltered() {
        EventBus bus = this.bus();
        bus.subscribe(GenericEventListener.class);
        final GenericEvent<String> event = new GenericEvent<>("String") {};
        Assertions.assertDoesNotThrow(() -> bus.post(event));
    }

    @Test
    void testGenericWildcardsArePosted() {
        EventBus bus = this.bus();
        // Ensure the values have not been affected by previous tests
        GenericEventListener.objects().clear();
        bus.subscribe(GenericEventListener.class);
        final GenericEvent<String> stringEvent = new GenericEvent<>("String") {};
        final GenericEvent<Integer> integerEvent = new GenericEvent<>(1) {};
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

    private EventBus bus() {
        return new SimpleEventBus();
    }

}
