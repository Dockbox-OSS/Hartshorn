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

package org.dockbox.hartshorn.events.handle;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoader;
import org.dockbox.hartshorn.events.EventWrapper;
import org.dockbox.hartshorn.events.parents.Event;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;

/**
 * Wrapper type for future invocation of a {@link Method} listening for {@link Event} posting. This
 * type is responsible for filtering and invoking a {@link Method} when a supported {@link Event} is
 * fired.
 */
public final class EventWrapperImpl<T> implements Comparable<EventWrapperImpl<T>>, EventWrapper {

    public static final Comparator<EventWrapperImpl<?>> COMPARATOR = (o1, o2) -> {
        if (fastEqual(o1, o2)) return 0;

        int c;
        if (0 != (c = Integer.compare(o1.priority, o2.priority))) return c;
        if (0 != (c = o1.method.qualifiedName().compareTo(o2.method.qualifiedName()))) return c;
        if (0 != (c = o1.eventType.qualifiedName().compareTo(o2.eventType.qualifiedName()))) return c;
        if (0 != (c = Integer.compare(o1.listenerType.hashCode(), o2.listenerType.hashCode()))) return c;
        if (0 != (c = Integer.compare(o1.hashCode(), o2.hashCode()))) return c;

        throw new AssertionError(); // ensures the comparator will never return 0 if the two wrapper
        // aren't equal
    };
    @Getter private final ParameterLoader<EventParameterLoaderContext> parameterLoader;
    @Getter private final TypeContext<? extends Event> eventType;
    @Getter private final List<TypeContext<?>> eventParameters;
    @Getter private final Key<T> listenerType;
    @Getter private final ApplicationContext context;
    @Getter private final MethodContext<?, T> method;
    @Getter private final int priority;
    @Getter private T listener;

    private EventWrapperImpl(
            final ParameterLoader<EventParameterLoaderContext> parameterLoader,
            final Key<T> key,
            final TypeContext<? extends Event> eventType,
            final MethodContext<?, T> method,
            final int priority,
            final ApplicationContext context
    ) {
        this.context = context;
        this.listener = null; // Lazy loaded value
        this.parameterLoader = parameterLoader;
        this.listenerType = key;
        this.eventType = eventType;
        this.method = method;
        this.priority = priority;
        this.eventParameters = method.parameters().get(0).typeParameters();
    }

    /**
     * Creates one or more {@link EventWrapperImpl}s (depending on how many event parameters are
     * present) for a given method and instance.
     *
     * @param key The type of the instance which is used when invoking the method.
     * @param method The method to store for invocation.
     * @param priority The priority at which the event is fired.
     *
     * @return The list of {@link EventWrapperImpl}s
     */
    public static <T> List<EventWrapperImpl<T>> create(final ApplicationContext context, final Key<T> key, final MethodContext<?, T> method, final int priority) {
        final List<EventWrapperImpl<T>> invokeWrappers = new CopyOnWriteArrayList<>();
        final ParameterLoader<EventParameterLoaderContext> parameterLoader = context.get(Key.of(ParameterLoader.class, "event_loader"));
        for (final TypeContext<?> param : method.parameterTypes()) {
            if (param.childOf(Event.class)) {
                invokeWrappers.add(new EventWrapperImpl<>(parameterLoader, key, (TypeContext<? extends Event>) param, method, priority, context));
            }
        }
        return invokeWrappers;
    }

    @Override
    public void invoke(final Event event) throws SecurityException {
        if (this.filtersMatch(event)) {
            final String eventName = TypeContext.of(event).name();
            event.applicationContext().log().debug("Invoking event " + eventName + " to method context of " + this.method.qualifiedName());
            // Lazy initialisation to allow processors to register first
            if (this.listener == null) this.listener = event.applicationContext().get(this.listenerType);
            final EventParameterLoaderContext loaderContext = new EventParameterLoaderContext(this.method, this.listenerType.type(), this.listener, this.context, event);
            final List<Object> arguments = this.parameterLoader().loadArguments(loaderContext);
            final Exceptional<?> result = this.method.invoke(this.listener, arguments);
            if (result.caught()) {
                this.context().handle("Could not finish event runner for " + eventName, result.error());
            }
        }
    }

    private boolean filtersMatch(final Event event) {
        if (!this.eventParameters.isEmpty()) {
            final List<TypeContext<?>> typeParameters = TypeContext.of(event).typeParameters();
            if (typeParameters.size() != this.eventParameters.size()) return false;

            for (int i = 0; i < this.eventParameters.size(); i++) {
                final TypeContext<?> eventParameter = this.eventParameters.get(i);
                final TypeContext<?> actualTypeArgument = typeParameters.get(i);
                if (!actualTypeArgument.childOf(eventParameter)) return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(@NonNull final EventWrapperImpl o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public int hashCode() {
        int n = 1;
        n = 31 * n + this.listenerType.hashCode();
        n = 31 * n + this.eventType.hashCode();
        n = 31 * n + this.method.hashCode();
        return n;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof EventWrapperImpl)) return false;
        return fastEqual(this, (EventWrapperImpl<?>) o);
    }

    private static boolean fastEqual(final EventWrapperImpl<?> o1, final EventWrapperImpl<?> o2) {
        return Objects.equals(o1.listenerType, o2.listenerType)
                && Objects.equals(o1.eventType, o2.eventType)
                && Objects.equals(o1.method, o2.method);
    }

    @Override
    public String toString() {
        return String.format(
                "InvokeWrapper{key=%s, eventType=%s, method=%s(%s), priority=%d}",
                this.listenerType,
                this.eventType.name(),
                this.method.name(),
                this.eventType.name(),
                this.priority);
    }
}
