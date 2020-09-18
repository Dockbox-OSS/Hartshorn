/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.darwin.core.util.events;

import org.dockbox.darwin.core.objects.events.Event;
import org.dockbox.darwin.core.server.Server;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class InvokeWrapper implements Comparable<InvokeWrapper> {
    public static final Comparator<InvokeWrapper> COMPARATOR = (o1, o2) -> {
        if (fastEqual(o1, o2)) return 0;

        // @formatter:off
        int c;
        if ((c = Integer.compare(o1.priority, o2.priority)) != 0) return c;
        if ((c = o1.method.getName().compareTo(o2.method.getName())) != 0) return c;
        if ((c = o1.eventType.getName().compareTo(o2.eventType.getName())) != 0) return c;
        if ((c = Integer.compare(o1.listener.hashCode(), o2.listener.hashCode())) != 0) return c;
        if ((c = Integer.compare(o1.hashCode(), o2.hashCode())) != 0) return c;
        // @formatter:on
        throw new AssertionError();  // ensures the comparator will never return 0 if the two wrapper aren't equal
    };

    public static List<InvokeWrapper> create(Object instance, Method method, int priority, Lookup lookup)
            throws SecurityException {
        List<InvokeWrapper> invokeWrappers = new CopyOnWriteArrayList<>();
        for (Class<?> param : method.getParameterTypes()) {
            Class<? extends Event> eventType = (Class<? extends Event>) param;
            MethodHandle methodHandle = AccessHelper.unreflectMethodHandle(lookup, method);
            invokeWrappers.add(new InvokeWrapper(instance, eventType, method, priority, methodHandle));
        }
        return invokeWrappers;
    }

    private final Object listener;

    private final Class<? extends Event> eventType;

    private final Method method;

    private final int priority;

    private final MethodHandle methodHandle;

    InvokeWrapper(Object listener, Class<? extends Event> eventType, Method method, int priority, MethodHandle methodHandle) {
        this.listener = listener;
        this.eventType = eventType;
        this.method = method;
        this.priority = priority;
        this.methodHandle = methodHandle;
    }

    public void invoke(Event event) throws RuntimeException {
        try {
            List<Event> args = new ArrayList<>();
            // As event listeners support having multiple event parameters, it may be there are event parameters which
            // we do not have here. If the parameter type is equal to, or a super class of our event we will add it to
            // the argument list. If it is neither, null will be injected.
            for (Class<?> type : this.method.getParameterTypes()) {
                if (type.isAssignableFrom(event.getClass())) {
                    args.add(event);
                } else args.add(null);
            }
            this.methodHandle.invoke(this.listener, args.toArray(new Event[0]));
        } catch (Throwable e) {
            Server.getServer().except("Failed to invoke method", e);
        }
    }

    @Override
    public int compareTo(InvokeWrapper o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvokeWrapper)) return false;
        return fastEqual(this, (InvokeWrapper) o);
    }

    @Override
    public int hashCode() {
        int n = 1;
        n = 31 * n + this.listener.hashCode();
        n = 31 * n + this.eventType.hashCode();
        n = 31 * n + this.method.hashCode();
        return n;
    }

    private static boolean fastEqual(InvokeWrapper o1, InvokeWrapper o2) {
        return Objects.equals(o1.listener, o2.listener) &&
                Objects.equals(o1.eventType, o2.eventType) &&
                Objects.equals(o1.method, o2.method);
    }

    @Override
    public String toString() {
        return String.format("InvokeWrapper{listener=%s, eventType=%s, method=%s(%s), priority=%d}",
                this.listener, this.eventType.getName(), this.method.getName(), this.eventType.getSimpleName(), this.priority);
    }

    public Object getListener() {
        return this.listener;
    }

    public Class<? extends Event> getEventType() {
        return this.eventType;
    }

    public Method getMethod() {
        return this.method;
    }

    public int getPriority() {
        return this.priority;
    }

    public MethodHandle getMethodHandle() {
        return this.methodHandle;
    }
}
