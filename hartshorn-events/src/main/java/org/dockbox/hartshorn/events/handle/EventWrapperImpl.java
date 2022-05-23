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

package org.dockbox.hartshorn.events.handle;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.parameter.ParameterLoader;
import org.dockbox.hartshorn.events.EventWrapper;
import org.dockbox.hartshorn.events.parents.Event;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private final ParameterLoader<EventParameterLoaderContext> parameterLoader;
    private final TypeContext<? extends Event> eventType;
    private final List<TypeContext<?>> eventParameters;
    private final Key<T> listenerType;
    private final ApplicationContext context;
    private final MethodContext<?, T> method;
    private final int priority;
    private T listener;

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

    public ParameterLoader<EventParameterLoaderContext> parameterLoader() {
        return this.parameterLoader;
    }

    public TypeContext<? extends Event> eventType() {
        return this.eventType;
    }

    public List<TypeContext<?>> eventParameters() {
        return this.eventParameters;
    }

    public Key<T> listenerType() {
        return this.listenerType;
    }

    public ApplicationContext context() {
        return this.context;
    }

    public MethodContext<?, T> method() {
        return this.method;
    }

    public int priority() {
        return this.priority;
    }

    public T listener() {
        return this.listener;
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
            final Result<?> result = this.method.invoke(this.listener, arguments);
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
