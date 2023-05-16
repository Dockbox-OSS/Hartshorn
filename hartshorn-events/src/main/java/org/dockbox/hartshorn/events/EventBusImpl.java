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

package org.dockbox.hartshorn.events;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.events.annotations.Listener;
import org.dockbox.hartshorn.events.handle.EventHandlerRegistry;
import org.dockbox.hartshorn.events.handle.EventWrapperImpl;
import org.dockbox.hartshorn.events.parents.Event;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;

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

    protected final Set<Function<MethodView<?, ?>, Attempt<Boolean, InvalidEventListenerException>>> validators = ConcurrentHashMap.newKeySet();

    /** A map of all listening objects with their associated {@link EventWrapper}s. */
    protected final Map<ComponentKey<?>, Set<EventWrapper>> listenerToInvokers = new ConcurrentHashMap<>();

    /** The internal registry of handlers for each event. */
    protected final EventHandlerRegistry handlerRegistry = new EventHandlerRegistry();

    @Inject
    private ApplicationContext context;

    public EventBusImpl() {
        // Event listeners need a @Listener annotation
        this.addValidationRule(method -> {
            if (!method.annotations().has(Listener.class)) {
                return Attempt.of(false, new InvalidEventListenerException("Expected @Listener annotation on " + method.qualifiedName()));
            }
            return Attempt.of(true);
        });
        // Event listeners cannot be abstract
        this.addValidationRule(method -> {
            if (method.modifiers().isAbstract()) {
                return Attempt.of(false, new InvalidEventListenerException("Method cannot be abstract: " + method.qualifiedName()));
            }
            return Attempt.of(true);
        });
        // Event listeners must have one and only parameter which is a subclass of Event
        this.addValidationRule(method -> {
            if (1 != method.parameters().count() || !method.parameters().at(0).get().type().isChildOf(Event.class)) {
                return Attempt.of(false, new InvalidEventListenerException("Must have one (and only one) parameter, which is a subclass of " + Event.class.getCanonicalName() + ": " + method.qualifiedName()));
            }
            return Attempt.of(true);
        });
    }

    /**
     * Subscribes all event listeners in an object instance. Typically, event listeners are methods
     * decorated with {@link Listener}.
     *
     * @param key The key of the listener
     */
    @Override
    public void subscribe(final ComponentKey<?> key) {
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
    public void unsubscribe(final ComponentKey<?> key) {
        final Set<EventWrapper> invokers = this.listenerToInvokers.remove(key);
        if (null == invokers || invokers.isEmpty()) {
            return; // Not registered
        }

        for (final EventWrapper invoker : invokers) {
            this.handlerRegistry.handler(invoker.eventType()).unsubscribe(invoker);
        }
    }

    @Override
    public void post(final Event event, final ComponentKey<?> target) {
        if (event.first(ApplicationContext.class).absent()) {
            this.context.log().debug("Event " + event.getClass().getSimpleName() + " was not enhanced with the active application context, adding it before handling event");
            event.add(this.context);
        }
        final TypeView<Event> typeView = this.context.environment().introspect(event);
        this.handlerRegistry.handler(typeView).post(event, target);
    }

    @Override
    public void post(final Event event) {
        this.post(event, null);
    }

    @Override
    public void addValidationRule(final Function<MethodView<?, ?>, Attempt<Boolean, InvalidEventListenerException>> validator) {
        this.validators.add(validator);
    }

    /**
     * Gets all {@link EventWrapper} instances for a given listener instance.
     *
     * @param key The listener type
     *
     * @return The invokers
     */
    protected <T> Set<EventWrapper> invokers(final ComponentKey<T> key) {
        final Set<EventWrapper> result = new HashSet<>();
        final TypeView<T> typeView = this.context.environment().introspect(key.type());
        for (final MethodView<T, ?> method : typeView.methods().annotatedWith(Listener.class)) {
            final Listener annotation = method.annotations().get(Listener.class).get();
            this.checkListenerMethod(method);
            result.addAll(EventWrapperImpl.create(this.context, key, method, annotation.value().priority()));
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
    protected void checkListenerMethod(final MethodView<?, ?> method) throws IllegalArgumentException {
        for (final Function<MethodView<?, ?>, Attempt<Boolean, InvalidEventListenerException>> validator : this.validators) {
            final Attempt<Boolean, InvalidEventListenerException> validation = validator.apply(method);
            if (validation.errorPresent()) validation.rethrow();

            final boolean result = validation.get();
            if (!result) throw new EventValidationException("Unspecified validation error while validating: " + method.qualifiedName());
        }
    }
}
