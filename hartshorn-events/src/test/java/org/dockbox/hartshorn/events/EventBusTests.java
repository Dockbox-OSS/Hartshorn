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

package org.dockbox.hartshorn.events;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.events.annotations.Listener.Priority;
import org.dockbox.hartshorn.events.listeners.BasicEventListener;
import org.dockbox.hartshorn.events.listeners.GenericEventListener;
import org.dockbox.hartshorn.events.listeners.PriorityEventListener;
import org.dockbox.hartshorn.events.listeners.StaticEventListener;
import org.dockbox.hartshorn.events.parents.Event;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import javax.inject.Inject;

import lombok.Getter;

@HartshornTest
public class EventBusTests {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    @Test
    public void testTypesCanSubscribe() {
        final EventBus bus = this.bus();
        bus.subscribe(Key.of(BasicEventListener.class));
        Assertions.assertTrue(bus.invokers().containsKey(Key.of(BasicEventListener.class)));
    }

    private EventBus bus() {
        return this.applicationContext().get(EventBusImpl.class);
    }

    @Test
    public void testNonStaticMethodsCanListen() {
        final EventBus bus = this.bus();
        bus.subscribe(Key.of(BasicEventListener.class));
        bus.post(new SampleEvent());
        Assertions.assertTrue(BasicEventListener.fired);
    }

    @Test
    public void testStaticMethodsCanListen() {
        final EventBus bus = this.bus();
        bus.subscribe(Key.of(StaticEventListener.class));
        bus.post(new SampleEvent());
        Assertions.assertTrue(StaticEventListener.fired);
    }

    @Test
    public void testEventsArePostedInCorrectPriorityOrder() {
        final EventBus bus = this.bus();
        bus.subscribe(Key.of(PriorityEventListener.class));
        bus.post(new SampleEvent());
        Assertions.assertEquals(Priority.LAST, PriorityEventListener.last());
    }

    @Test
    void testGenericEventsAreFiltered() {
        final EventBus bus = this.bus();
        bus.subscribe(Key.of(GenericEventListener.class));
        final Event event = new GenericEvent<>("String") {
        };
        Assertions.assertDoesNotThrow(() -> bus.post(event));
    }

    @Test
    void testGenericWildcardsArePosted() {
        final EventBus bus = this.bus();
        // Ensure the values have not been affected by previous tests
        GenericEventListener.objects().clear();
        bus.subscribe(Key.of(GenericEventListener.class));
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
