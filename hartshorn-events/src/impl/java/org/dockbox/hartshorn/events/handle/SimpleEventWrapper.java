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

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.events.EventWrapper;
import org.dockbox.hartshorn.events.parents.Event;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
    @Getter private final Class<?> listenerType;
    @Getter private final Class<? extends Event> eventType;
    @Getter private final Type[] eventParameters;
    @Getter private final Method method;
    @Getter private final int priority;
    private final BiConsumer<Object, ? super Event> operator;
    @Getter private Object listener;

    private SimpleEventWrapper(Class<?> type, Class<? extends Event> eventType, Method method, int priority) {
        this.listener = null;
        this.listenerType = type;
        this.eventType = eventType;
        this.method = method;
        this.priority = priority;

        final Type genericType = method.getGenericParameterTypes()[0];
        if (genericType instanceof ParameterizedType parameterizedType) {
            this.eventParameters = parameterizedType.getActualTypeArguments();
        }
        else {
            this.eventParameters = new Type[0];
        }

        // Listener methods may be private or protected, before invoking it we need to ensure it is
        // accessible.
        this.method.setAccessible(true);

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
        }
        catch (Throwable e) {
            Hartshorn.log().warn("Could not prepare meta factory for method '" + this.method.getName() + "' in " + this.listenerType.getSimpleName() + ", behavior will default to unoptimized reflective operations.");
            return null;
        }
    }

    /**
     * Creates one or more {@link SimpleEventWrapper}s (depending on how many event parameters are
     * present) for a given method and instance.
     *
     * @param type
     *         The type of the instance which is used when invoking the method.
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
            if (Reflect.assigns(Event.class, param)) {
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
        if (this.eventParameters.length > 0) {
            final Type[] actualTypeArguments = (((ParameterizedType) event.getClass().getGenericSuperclass()).getActualTypeArguments());
            if (this.eventParameters.length != actualTypeArguments.length) return false;

            for (int i = 0; i < this.eventParameters.length; i++) {
                final Type eventParameter = this.eventParameters[i];
                final Type actualTypeArgument = actualTypeArguments[i];

                if (eventParameter instanceof Class && actualTypeArgument instanceof Class) {
                    if (!Reflect.assigns((Class<?>) eventParameter, (Class<?>) actualTypeArgument)) {
                        return false;
                    }
                }
            }
        }

        return true;
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
