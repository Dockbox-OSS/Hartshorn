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

import org.dockbox.selene.api.events.annotations.Listener.Priority;
import org.dockbox.selene.api.events.annotations.filter.Filter;
import org.dockbox.selene.api.events.listeners.BasicEventListener;
import org.dockbox.selene.api.events.listeners.FilteredEventListener;
import org.dockbox.selene.api.events.listeners.PriorityEventListener;
import org.dockbox.selene.api.events.listeners.StaticEventListener;
import org.dockbox.selene.api.events.processing.FilterTypes;
import org.dockbox.selene.test.SeleneJUnit5Runner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Annotation;

@ExtendWith(SeleneJUnit5Runner.class)
@SuppressWarnings("InstantiationOfUtilityClass")
public class EventBusTests {

    @Test
    public void testTypesCanSubscribe() {
        EventBus bus = this.bus();
        BasicEventListener listener = new BasicEventListener();
        bus.subscribe(listener);
        Assertions.assertTrue(bus.getListenersToInvokers().containsKey(listener));
    }

    @Test
    public void testNonStaticMethodsCanListen() {
        EventBus bus = this.bus();
        BasicEventListener listener = new BasicEventListener();
        bus.subscribe(listener);
        bus.post(new SampleEvent());
        Assertions.assertTrue(BasicEventListener.fired);
    }

    @Test
    public void testStaticMethodsCanListen() {
        EventBus bus = this.bus();
        StaticEventListener listener = new StaticEventListener();
        bus.subscribe(listener);
        bus.post(new SampleEvent());
        Assertions.assertTrue(StaticEventListener.fired);
    }

    @Test
    public void testEventsArePostedInCorrectPriorityOrder() {
        EventBus bus = this.bus();
        PriorityEventListener listener = new PriorityEventListener();
        bus.subscribe(listener);
        bus.post(new SampleEvent());
        Assertions.assertEquals(Priority.LAST, listener.getLast());
    }

    @Test
    public void testFilteredListeners() {
        EventBus bus = this.bus();
        FilteredEventListener listener = new FilteredEventListener();
        bus.subscribe(listener);
        SampleFilterableEvent event = new SampleFilterableEvent("Selene");

        Assertions.assertEquals(1, event.acceptedFilters().size());
        Assertions.assertEquals(FilterTypes.EQUALS, event.acceptedFilters().get(0));

        Assertions.assertTrue(event.isApplicable(this.filter("name", "Selene", FilterTypes.EQUALS)));

        bus.post(event);
        Assertions.assertTrue(FilteredEventListener.fired);
    }

    @Test
    public void testIncorrectFilterDoesNoetInvokeFilteredListeners() {
        EventBus bus = this.bus();
        FilteredEventListener listener = new FilteredEventListener();
        bus.subscribe(listener);
        SampleFilterableEvent event = new SampleFilterableEvent("NotSelene");

        Assertions.assertEquals(1, event.acceptedFilters().size());
        Assertions.assertEquals(FilterTypes.EQUALS, event.acceptedFilters().get(0));

        Assertions.assertFalse(event.isApplicable(this.filter("name", "Selene", FilterTypes.EQUALS)));

        bus.post(event);
        Assertions.assertFalse(FilteredEventListener.fired);
    }

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
