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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.events.annotations.Listener;
import org.dockbox.hartshorn.events.handle.EventHandlerRegistry;
import org.dockbox.hartshorn.events.handle.EventWrapperImpl;
import org.dockbox.hartshorn.events.parents.Event;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * A simple default implementation of {@link EventBus}, used for internal event posting and
 * handling.
 */
@SuppressWarnings("EqualsWithItself")
@Binds(EventBus.class)
@Singleton
public class EventBusImpl implements EventBus {

    protected final Set<Function<MethodContext<?, ?>, Exceptional<Boolean>>> validators = HartshornUtils.emptySet();

    /** A map of all listening objects with their associated {@link EventWrapper}s. */
    protected final Map<TypeContext<?>, Set<EventWrapper>> listenerToInvokers = HartshornUtils.emptyMap();

    /** The internal registry of handlers for each event. */
    protected final EventHandlerRegistry handlerRegistry = new EventHandlerRegistry();

    @Inject
    private ApplicationContext context;

    public EventBusImpl() {
        // Event listeners need a @Listener annotation
        this.addValidationRule(method -> {
            if (method.annotation(Listener.class).absent()) {
                return Exceptional.of(false, new IllegalArgumentException("Needs @Listener annotation: " + method.qualifiedName()));
            }
            return Exceptional.of(true);
        });
        // Event listeners cannot be abstract
        this.addValidationRule(method -> {
            if (method.isAbstract()) {
                return Exceptional.of(false, new IllegalArgumentException("Method cannot be abstract: " + method.qualifiedName()));
            }
            return Exceptional.of(true);
        });
        // Event listeners must have one and only parameter which is a subclass of Event
        this.addValidationRule(method -> {
            if (1 != method.parameterCount() || !method.parameterTypes().get(0).childOf(Event.class)) {
                return Exceptional.of(false, new IllegalArgumentException("Must have one (and only one) parameter, which is a subclass of Event: " + method.qualifiedName()));
            }
            return Exceptional.of(true);
        });
    }

    /**
     * Subscribes all event listeners in a object instance. Typically event listeners are methods
     * decorated with {@link Listener}.
     *
     * @param type
     *         The instance of the listener
     */
    @Override
    public void subscribe(final TypeContext<?> type) {
        if (!type.equals(type)) return;
        if (this.listenerToInvokers.containsKey(type)) {
            return; // Already registered
        }

        final Set<EventWrapper> invokers = this.invokers(type);
        if (invokers.isEmpty()) {
            return; // Doesn't contain any listener methods
        }
        this.listenerToInvokers.put(type, invokers);
        for (final EventWrapper invoker : invokers) {
            this.handlerRegistry.handler(invoker.eventType()).subscribe(invoker);
        }
    }

    /**
     * Unsubscribes all event listeners in a object instance.
     *
     * @param type
     *         The instance of the listener
     */
    @Override
    public void unsubscribe(final TypeContext<?> type) {
        if (!type.equals(type)) return;
        final Set<EventWrapper> invokers = this.listenerToInvokers.remove(type);
        if (null == invokers || invokers.isEmpty()) {
            return; // Not registered
        }

        for (final EventWrapper invoker : invokers) {
            this.handlerRegistry.handler(invoker.eventType()).unsubscribe(invoker);
        }
    }

    @Override
    public void post(final Event event, final TypeContext<?> target) {
        if (event.first(ApplicationContext.class).absent()) event.add(this.context);
        this.handlerRegistry.handler(TypeContext.of(event)).post(event, target);
    }

    @Override
    public void post(final Event event) {
        this.post(event, null);
    }

    @NotNull
    @Override
    public Map<TypeContext<?>, Set<EventWrapper>> invokers() {
        return this.listenerToInvokers;
    }

    @Override
    public void addValidationRule(final Function<MethodContext<?, ?>, Exceptional<Boolean>> validator) {
        this.validators.add(validator);
    }

    /**
     * Gets all {@link EventWrapper} instances for a given listener instance.
     *
     * @param type
     *         The listener type
     *
     * @return The invokers
     */
    protected <T> Set<EventWrapper> invokers(final TypeContext<T> type) {
        final Set<EventWrapper> result = HartshornUtils.emptySet();
        for (final MethodContext<?, T> method : type.flatMethods()) {
            final Exceptional<Listener> annotation = method.annotation(Listener.class);
            if (annotation.present()) {
                this.checkListenerMethod(method);
                result.addAll(EventWrapperImpl.create(type, method, annotation.get().value().priority()));
            }
        }
        return result;
    }

    /**
     * Checks if a method is a valid listener method. A method is only valid if it:
     *
     * <ul>
     *   <li>Is decorated with {@link Listener}
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
    protected void checkListenerMethod(final MethodContext<?, ?> method) throws IllegalArgumentException {
        for (final Function<MethodContext<?, ?>, Exceptional<Boolean>> validator : this.validators) {
            final boolean result = validator.apply(method).rethrow().get();
            if (!result) throw new IllegalArgumentException("Unspecified validation error while validating: " + method.qualifiedName());
        }
    }
}
