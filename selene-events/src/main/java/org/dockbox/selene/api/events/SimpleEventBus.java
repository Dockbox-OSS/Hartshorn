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
import org.dockbox.selene.api.events.annotations.Listener;
import org.dockbox.selene.api.events.handle.EventHandlerRegistry;
import org.dockbox.selene.api.events.handle.SimpleEventWrapper;
import org.dockbox.selene.api.events.parents.Event;
import org.dockbox.selene.di.annotations.Binds;
import org.dockbox.selene.util.Reflect;
import org.dockbox.selene.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A simple default implementation of {@link EventBus}, used for internal event posting and
 * handling.
 */
@SuppressWarnings({ "EqualsWithItself", "unchecked" })
@Binds(EventBus.class)
public class SimpleEventBus implements EventBus {

    protected static final Set<Function<Method, Exceptional<Boolean>>> validators = SeleneUtils.emptySet();

    /** A map of all listening objects with their associated {@link EventWrapper}s. */
    protected static final Map<Object, Set<EventWrapper>> listenerToInvokers = SeleneUtils.emptyMap();

    /** The internal registry of handlers for each event. */
    protected static final EventHandlerRegistry handlerRegistry = new EventHandlerRegistry();

    public SimpleEventBus() {
        // Event listeners need a @Listener annotation
        this.addValidationRule(method -> {
            if (!Reflect.hasAnnotation(method, Listener.class)) {
                return Exceptional.of(false, new IllegalArgumentException("Needs @Listener annotation: " + method.toGenericString()));
            }
            return Exceptional.of(true);
        });
        // Event listeners cannot be abstract
        this.addValidationRule(method -> {
            int modifiers = method.getModifiers();
            if (Modifier.isAbstract(modifiers)) {
                return Exceptional.of(false, new IllegalArgumentException("Method cannot be abstract: " + method.toGenericString()));
            }
            return Exceptional.of(true);
        });
        // Event listeners must have one and only parameter which is a subclass of Event
        this.addValidationRule(method -> {
            if (1 != method.getParameterCount() || !Reflect.assignableFrom(Event.class, method.getParameterTypes()[0])) {
                return Exceptional.of(false, new IllegalArgumentException("Must have one (and only one) parameter, which is a subclass of Event: " + method.toGenericString()));
            }
            return Exceptional.of(true);
        });
    }

    /**
     * Subscribes all event listeners in a object instance. Typically event listeners are methods
     * annotated with {@link Listener}.
     *
     * @param object
     *         The instance of the listener
     */
    @Override
    public void subscribe(Object object) {
        if (!object.equals(object)) return;
        if (listenerToInvokers.containsKey(object)) {
            return; // Already registered
        }

        Set<EventWrapper> invokers = getInvokers(object);
        if (invokers.isEmpty()) {
            return; // Doesn't contain any listener methods
        }
        listenerToInvokers.put(object, invokers);
        for (EventWrapper invoker : invokers) {
            handlerRegistry.getHandler(invoker.getEventType()).subscribe(invoker);
        }
    }

    /**
     * Unsubscribes all event listeners in a object instance.
     *
     * @param object
     *         The instance of the listener
     */
    @Override
    public void unsubscribe(Object object) {
        if (!object.equals(object)) return;
        Set<EventWrapper> invokers = listenerToInvokers.remove(object);
        if (null == invokers || invokers.isEmpty()) {
            return; // Not registered
        }

        for (EventWrapper invoker : invokers) {
            handlerRegistry.getHandler(invoker.getEventType()).unsubscribe(invoker);
        }
    }

    @Override
    public void post(Event event, Class<?> target) {
        handlerRegistry.getHandler(event.getClass()).post(event, target);
    }

    @Override
    public void post(Event event) {
        this.post(event, null);
    }

    @NotNull
    @Override
    public Map<Object, Set<EventWrapper>> getListenersToInvokers() {
        return listenerToInvokers;
    }

    @Override
    public void addValidationRule(Function<Method, Exceptional<Boolean>> validator) {
        validators.add(validator);
    }

    /**
     * Gets all {@link EventWrapper} instances for a given listener instance.
     *
     * @param object
     *         The listener instance
     *
     * @return The invokers
     */
    protected static Set<EventWrapper> getInvokers(Object object) {
        Set<EventWrapper> result = SeleneUtils.emptySet();
        for (Method method : Reflect.methods(object.getClass())) {
            Listener annotation = Reflect.annotation(method, Listener.class);
            if (null != annotation) {
                checkListenerMethod(method);
                result.addAll(SimpleEventWrapper.create(object, method, annotation.value().getPriority()));
            }
        }
        return result;
    }

    /**
     * Checks if a method is a valid listener method. A method is only valid if it:
     *
     * <ul>
     *   <li>Is annotated with {@link Listener}
     *   <li>Is not static
     *   <li>Is not abstract
     *   <li>Has at least one parameter which is a subcless of {@link Event}
     * </ul>
     *
     * @param method
     *         the method
     *
     * @throws IllegalArgumentException
     *         the illegal argument exception
     */
    protected static void checkListenerMethod(Method method) throws IllegalArgumentException {
        for (Function<Method, Exceptional<Boolean>> validator : validators) {
            boolean result = validator.apply(method).rethrow().get();
            if (!result) throw new IllegalArgumentException("Unspecified validation error while validating: " + method.toGenericString());
        }
    }
}
