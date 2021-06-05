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

package org.dockbox.hartshorn.api.events;

import org.dockbox.hartshorn.api.events.annotations.Listener.Priority;
import org.dockbox.hartshorn.api.events.annotations.filter.Filter;
import org.dockbox.hartshorn.api.events.listeners.BasicEventListener;
import org.dockbox.hartshorn.api.events.listeners.FilteredEventListener;
import org.dockbox.hartshorn.api.events.listeners.PriorityEventListener;
import org.dockbox.hartshorn.api.events.listeners.StaticEventListener;
import org.dockbox.hartshorn.api.events.processing.FilterTypes;
import org.dockbox.hartshorn.test.HartshornRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Annotation;

@ExtendWith(HartshornRunner.class)
public class EventBusTests {

    @Test
    public void testTypesCanSubscribe() {
        EventBus bus = this.bus();
        bus.subscribe(BasicEventListener.class);
        Assertions.assertTrue(bus.getListenersToInvokers().containsKey(BasicEventListener.class));
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
        Assertions.assertEquals(Priority.LAST, PriorityEventListener.getLast());
    }

    @Test
    public void testFilteredListeners() {
        EventBus bus = this.bus();
        bus.subscribe(FilteredEventListener.class);
        SampleFilterableEvent event = new SampleFilterableEvent("Hartshorn");

        Assertions.assertEquals(1, event.acceptedFilters().size());
        Assertions.assertEquals(FilterTypes.EQUALS, event.acceptedFilters().get(0));

        Assertions.assertTrue(event.isApplicable(this.filter("name", "Hartshorn", FilterTypes.EQUALS)));

        bus.post(event);
        Assertions.assertTrue(FilteredEventListener.fired);
    }

    @Test
    public void testIncorrectFilterDoesNoetInvokeFilteredListeners() {
        EventBus bus = this.bus();
        bus.subscribe(FilteredEventListener.class);
        SampleFilterableEvent event = new SampleFilterableEvent("NotHartshorn");

        Assertions.assertEquals(1, event.acceptedFilters().size());
        Assertions.assertEquals(FilterTypes.EQUALS, event.acceptedFilters().get(0));

        Assertions.assertFalse(event.isApplicable(this.filter("name", "Hartshorn", FilterTypes.EQUALS)));

        bus.post(event);
        Assertions.assertFalse(FilteredEventListener.fired);
    }

    @SuppressWarnings("SameParameterValue")
    private Filter filter(String param, String value, FilterTypes type) {
        //noinspection OverlyComplexAnonymousInnerClass
        return new Filter() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Filter.class;
            }

            @Override
            public String param() {
                return param;
            }

            @Override
            public String value() {
                return value;
            }

            @Override
            public FilterTypes type() {
                return type;
            }
        };
    }

    @AfterEach
    public void reset() {
        BasicEventListener.fired = false;
        StaticEventListener.fired = false;
        FilteredEventListener.fired = false;
    }

    private EventBus bus() {
        return new SimpleEventBus();
    }

}
