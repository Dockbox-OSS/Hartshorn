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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.events.annotations.Listener;
import org.dockbox.hartshorn.events.handle.EventHandlerRegistry;
import org.dockbox.hartshorn.events.handle.EventWrapperImpl;
import org.dockbox.hartshorn.events.parents.Event;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import jakarta.inject.Inject;

/**
 * A simple default implementation of {@link EventBus}, used for internal event posting and
 * handling.
 */
@Component(singleton = true)
public class EventBusImpl implements EventBus {

    protected final Set<Function<MethodContext<?, ?>, Result<Boolean>>> validators = ConcurrentHashMap.newKeySet();

    /** A map of all listening objects with their associated {@link EventWrapper}s. */
    protected final Map<Key<?>, Set<EventWrapper>> listenerToInvokers = new ConcurrentHashMap<>();

    /** The internal registry of handlers for each event. */
    protected final EventHandlerRegistry handlerRegistry = new EventHandlerRegistry();

    @Inject
    private ApplicationContext context;

    public EventBusImpl() {
        // Event listeners need a @Listener annotation
        this.addValidationRule(method -> {
            if (method.annotation(Listener.class).absent()) {
                return Result.of(false, new IllegalArgumentException("Needs @Listener annotation: " + method.qualifiedName()));
            }
            return Result.of(true);
        });
        // Event listeners cannot be abstract
        this.addValidationRule(method -> {
            if (method.isAbstract()) {
                return Result.of(false, new IllegalArgumentException("Method cannot be abstract: " + method.qualifiedName()));
            }
            return Result.of(true);
        });
        // Event listeners must have one and only parameter which is a subclass of Event
        this.addValidationRule(method -> {
            if (1 != method.parameterCount() || !method.parameterTypes().get(0).childOf(Event.class)) {
                return Result.of(false, new IllegalArgumentException("Must have one (and only one) parameter, which is a subclass of Event: " + method.qualifiedName()));
            }
            return Result.of(true);
        });
    }

    /**
     * Subscribes all event listeners in an object instance. Typically, event listeners are methods
     * decorated with {@link Listener}.
     *
     * @param key The key of the listener
     */
    @Override
    public void subscribe(final Key<?> key) {
        if (this.listenerToInvokers.containsKey(key)) {
            this.context.log().debug(key + " is already subscribed, skipping duplicate registration");
            return; // Already subscribed
        }

        final Set<EventWrapper> invokers = this.invokers(key);
        if (invokers.isEmpty()) {
            this.context.log().debug(key + " has no event invokers, skipping registration");
            return; // Doesn't contain any listener methods
        }
        this.listenerToInvokers.put(key, invokers);
        for (final EventWrapper invoker : invokers) {
            this.handlerRegistry.handler(invoker.eventType()).subscribe(invoker);
        }
    }

    /**
     * Unsubscribes all event listeners in an object instance.
     *
     * @param key The instance of the listener
     */
    @Override
    public void unsubscribe(final Key<?> key) {
        final Set<EventWrapper> invokers = this.listenerToInvokers.remove(key);
        if (null == invokers || invokers.isEmpty()) {
            return; // Not registered
        }

        for (final EventWrapper invoker : invokers) {
            this.handlerRegistry.handler(invoker.eventType()).unsubscribe(invoker);
        }
    }

    @Override
    public void post(final Event event, final Key<?> target) {
        if (event.first(this.context, ApplicationContext.class).absent()) {
            this.context.log().debug("Event " + TypeContext.of(event).name() + " was not enhanced with the active application context, adding it before handling event");
            event.add(this.context);
        }
        this.handlerRegistry.handler(TypeContext.of(event)).post(event, target);
    }

    @Override
    public void post(final Event event) {
        this.post(event, null);
    }

    @NonNull
    @Override
    public Map<Key<?>, Set<EventWrapper>> invokers() {
        return this.listenerToInvokers;
    }

    @Override
    public void addValidationRule(final Function<MethodContext<?, ?>, Result<Boolean>> validator) {
        this.validators.add(validator);
    }

    /**
     * Gets all {@link EventWrapper} instances for a given listener instance.
     *
     * @param key The listener type
     *
     * @return The invokers
     */
    protected <T> Set<EventWrapper> invokers(final Key<T> key) {
        final Set<EventWrapper> result = new HashSet<>();
        for (final MethodContext<?, T> method : key.type().methods()) {
            final Result<Listener> annotation = method.annotation(Listener.class);
            if (annotation.present()) {
                this.checkListenerMethod(method);
                result.addAll(EventWrapperImpl.create(this.context, key, method, annotation.get().value().priority()));
            }
        }
        return Set.copyOf(result);
    }

    /**
     * Checks if a method is a valid listener method. A method is only valid if it:
     *
     * <ul>
     *   <li>Is decorated with {@link Listener}
     *   <li>Is not static
     *   <li>Is not abstract
     *   <li>Has at least one parameter which is a subclass of {@link Event}
     * </ul>
     *
     * @param method the method
     *
     * @throws IllegalArgumentException the illegal argument exception
     */
    protected void checkListenerMethod(final MethodContext<?, ?> method) throws IllegalArgumentException {
        for (final Function<MethodContext<?, ?>, Result<Boolean>> validator : this.validators) {
            final boolean result = validator.apply(method).rethrowUnchecked().get();
            if (!result) throw new IllegalArgumentException("Unspecified validation error while validating: " + method.qualifiedName());
        }
    }
}
