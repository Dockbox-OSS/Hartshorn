/*
 * Copyright 2019-2023 the original author or authors.
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

package test.org.dockbox.hartshorn.events;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.events.annotations.Listener.Priority;
import org.dockbox.hartshorn.events.annotations.UseEvents;
import org.dockbox.hartshorn.events.parents.Event;
import org.dockbox.hartshorn.hsl.UseExpressionValidation;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import jakarta.inject.Inject;
import test.org.dockbox.hartshorn.events.listeners.BasicEventListener;
import test.org.dockbox.hartshorn.events.listeners.ConditionalEventListener;
import test.org.dockbox.hartshorn.events.listeners.GenericEventListener;
import test.org.dockbox.hartshorn.events.listeners.PriorityEventListener;
import test.org.dockbox.hartshorn.events.listeners.StaticEventListener;

@HartshornTest(includeBasePackages = false)
@UseEvents
@UseExpressionValidation
@TestComponents(TestEventBus.class)
public class EventBusTests {

    @Inject
    private TestEventBus bus;
    
    @Test
    @TestComponents(BasicEventListener.class)
    public void testTypesCanSubscribe() {
        this.bus.subscribe(ComponentKey.of(BasicEventListener.class));
        Assertions.assertTrue(this.bus.invokers().containsKey(ComponentKey.of(BasicEventListener.class)));
    }

    @Test
    @TestComponents(BasicEventListener.class)
    public void testNonStaticMethodsCanListen() {
        this.bus.subscribe(ComponentKey.of(BasicEventListener.class));
        this.bus.post(new SampleEvent());
        Assertions.assertTrue(BasicEventListener.fired);
    }

    @Test
    @TestComponents(StaticEventListener.class)
    public void testStaticMethodsCanListen() {
        this.bus.subscribe(ComponentKey.of(StaticEventListener.class));
        this.bus.post(new SampleEvent());
        Assertions.assertTrue(StaticEventListener.fired);
    }

    @Test
    @TestComponents(PriorityEventListener.class)
    public void testEventsArePostedInCorrectPriorityOrder() {
        this.bus.subscribe(ComponentKey.of(PriorityEventListener.class));
        this.bus.post(new SampleEvent());
        Assertions.assertEquals(Priority.LAST, PriorityEventListener.last());
    }

    @Test
    @TestComponents(GenericEventListener.class)
    void testGenericEventsAreFiltered() {
        this.bus.subscribe(ComponentKey.of(GenericEventListener.class));
        final Event event = new GenericEvent<>("String") {
        };
        Assertions.assertDoesNotThrow(() -> this.bus.post(event));
    }

    @Test
    @TestComponents(GenericEventListener.class)
    void testGenericWildcardsArePosted() {
        // Ensure the values have not been affected by previous tests
        GenericEventListener.objects.clear();
        this.bus.subscribe(ComponentKey.of(GenericEventListener.class));
        final Event stringEvent = new GenericEvent<>("String") {
        };
        final Event integerEvent = new GenericEvent<>(1) {
        };
        this.bus.post(stringEvent);
        this.bus.post(integerEvent);
        final List<Object> objects = List.copyOf(GenericEventListener.objects);
        Assertions.assertEquals(2, objects.size());
        Assertions.assertEquals("String", objects.get(0));
        Assertions.assertEquals(1, objects.get(1));
    }

    @Test
    @TestComponents(ConditionalEventListener.class)
    void testConditionEventIsNotFiredIfMismatch() {
        this.bus.subscribe(ComponentKey.of(ConditionalEventListener.class));

        final Event nullNameEvent = new SampleNamedEvent(null);
        this.bus.post(nullNameEvent);
        Assertions.assertFalse(ConditionalEventListener.fired);
    }

    @Test
    @TestComponents(ConditionalEventListener.class)
    void testConditionEventIsFiredIfMatch() {
        this.bus.subscribe(ComponentKey.of(ConditionalEventListener.class));

        final Event nameEvent = new SampleNamedEvent("name");
        this.bus.post(nameEvent);
        Assertions.assertTrue(ConditionalEventListener.fired);
    }

    @AfterEach
    public void reset() {
        BasicEventListener.fired = false;
        StaticEventListener.fired = false;
        ConditionalEventListener.fired = false;
    }
}
