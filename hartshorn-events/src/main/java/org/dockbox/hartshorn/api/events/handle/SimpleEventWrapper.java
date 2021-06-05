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

package org.dockbox.hartshorn.api.events.handle;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.events.EventWrapper;
import org.dockbox.hartshorn.api.events.annotations.filter.Filter;
import org.dockbox.hartshorn.api.events.annotations.filter.Filters;
import org.dockbox.hartshorn.api.events.parents.Event;
import org.dockbox.hartshorn.api.events.parents.Filterable;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.util.Reflect;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

import lombok.Getter;

/**
 * Wrapper type for future invokation of a {@link Method} listening for {@link Event} posting. This
 * type is responsible for filtering and invoking a {@link Method} when a supported {@link Event} is
 * fired.
 */
public final class SimpleEventWrapper implements Comparable<SimpleEventWrapper>, EventWrapper {

    public static final Comparator<SimpleEventWrapper> COMPARATOR = (o1, o2) -> {
        if (fastEqual(o1, o2)) return 0;

        int c;
        if (0 != (c = Integer.compare(o1.priority, o2.priority))) return c;
        if (0 != (c = o1.method.getName().compareTo(o2.method.getName()))) return c;
        if (0 != (c = o1.eventType.getName().compareTo(o2.eventType.getName()))) return c;
        if (0 != (c = Integer.compare(o1.listenerType.hashCode(), o2.listenerType.hashCode()))) return c;
        if (0 != (c = Integer.compare(o1.hashCode(), o2.hashCode()))) return c;

        throw new AssertionError(); // ensures the comparator will never return 0 if the two wrapper
        // aren't equal
    };

    @Getter
    private Object listener;
    @Getter
    private final Class<?> listenerType;
    @Getter
    private final Class<? extends Event> eventType;
    @Getter
    private final Method method;
    @Getter
    private final int priority;
    private final BiConsumer<Object, ? super Event> operator;

    private SimpleEventWrapper(Class<?> type, Class<? extends Event> eventType, Method method, int priority) {
        this.listener = null;
        this.listenerType = type;
        this.eventType = eventType;
        this.method = method;
        this.priority = priority;

        // Listener methods may be private or protected, before invoking it we need to ensure it is
        // accessible.
        if (!this.method.isAccessible()) {
            this.method.setAccessible(true);
        }

        this.operator = this.createLambda();
    }

    @SuppressWarnings("unchecked")
    private <T> BiConsumer<T, ? super Event> createLambda() {
        try {
            MethodHandles.Lookup caller = MethodHandles.lookup();
            CallSite site = LambdaMetafactory.metafactory(caller,
                    "accept",
                    MethodType.methodType(BiConsumer.class),
                    MethodType.methodType(void.class, Object.class, Object.class),
                    caller.findVirtual(this.listenerType, this.method.getName(),
                            MethodType.methodType(void.class, this.method.getParameterTypes()[0])),
                    MethodType.methodType(void.class, this.listenerType, this.method.getParameterTypes()[0]));
            MethodHandle factory = site.getTarget();
            return (BiConsumer<T, ? super Event>) factory.invoke();
        } catch (Throwable e) {
            Hartshorn.log().warn("Could not prepare meta factory for method '" + this.method.getName() + "' in " + this.listenerType.getSimpleName() + ", behavior will default to unoptimized reflective operations.");
            return null;
        }
    }

    /**
     * Creates one or more {@link SimpleEventWrapper}s (depending on how many event parameters are
     * present) for a given method and instance.
     *
     * @param instance
     *         The instance which is used when invoking the method.
     * @param method
     *         The method to store for invocation.
     * @param priority
     *         The priority at which the event is fired.
     *
     * @return The list of {@link SimpleEventWrapper}s
     */
    public static List<SimpleEventWrapper> create(Class<?> type, Method method, int priority) {
        List<SimpleEventWrapper> invokeWrappers = HartshornUtils.emptyConcurrentList();
        for (Class<?> param : method.getParameterTypes()) {
            if (Reflect.assignableFrom(Event.class, param)) {
                @SuppressWarnings("unchecked")
                Class<? extends Event> eventType = (Class<? extends Event>) param;
                invokeWrappers.add(new SimpleEventWrapper(type, eventType, method, priority));
            }
        }
        return invokeWrappers;
    }

    @Override
    public void invoke(Event event) throws SecurityException {
        if (this.filtersMatch(event)) {
            // Lazy initialisation to allow processors to register first
            if (this.listener == null) this.listener = Hartshorn.context().get(this.listenerType);

            Runnable eventRunner = () -> {
                try {
                    if (this.operator != null)
                        this.operator.accept(this.listener, event);
                    else
                        this.method.invoke(this.listener, event);
                }
                catch (Throwable e) {
                        /*
                        Typically this is caused by a exception thrown inside the event itself. It is possible that
                        the arguments provided to Method#invoke are incorrect, depending on external annotation
                        processors.
                        */
                    Except.handle("Could not finish event runner", e);
                }
            };

            eventRunner.run();
        }
    }

    private boolean filtersMatch(Event event) {
        /*
        If a event is Filterable and has one or more Filter annotations, we test for these filters to decide whether
        or not we can invoke this method. These filters act on the given filter and event, and unlike paramater
        annotation processors do not have access to the InvokeWrapper, Method or listener objects.
        */
        if (event instanceof Filterable) {
            if (this.method.isAnnotationPresent(Filter.class)) {
                Filter filter = this.method.getAnnotation(Filter.class);
                return SimpleEventWrapper.testFilter(filter, (Filterable) event);

            }
            else if (this.method.isAnnotationPresent(Filters.class)) {
                Filters filters = this.method.getAnnotation(Filters.class);
                for (Filter filter : filters.value()) {
                    if (!SimpleEventWrapper.testFilter(filter, (Filterable) event)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean testFilter(Filter filter, Filterable event) {
        if (event.acceptedParams().contains(filter.param())
                && event.acceptedFilters().contains(filter.type())) {
            return event.isApplicable(filter);
        }
        return false;
    }

    @Override
    public int compareTo(@NotNull SimpleEventWrapper o) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleEventWrapper)) return false;
        return fastEqual(this, (SimpleEventWrapper) o);
    }

    private static boolean fastEqual(SimpleEventWrapper o1, SimpleEventWrapper o2) {
        return Objects.equals(o1.listenerType, o2.listenerType)
                && Objects.equals(o1.eventType, o2.eventType)
                && Objects.equals(o1.method, o2.method);
    }

    @Override
    public String toString() {
        return String.format(
                "InvokeWrapper{type=%s, eventType=%s, method=%s(%s), priority=%d}",
                this.listenerType.getSimpleName(),
                this.eventType.getName(),
                this.method.getName(),
                this.eventType.getSimpleName(),
                this.priority);
    }
}
