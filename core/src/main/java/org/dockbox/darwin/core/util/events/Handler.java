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

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Handler {
    private final Class<? extends Event> eventType;

    private final Set<Handler> supertypeHandlers = new HashSet<>();

    private final SortedSet<InvokeWrapper> invokers = new TreeSet<>(InvokeWrapper.COMPARATOR);

    private transient volatile InvokeWrapper[] computedInvokerCache = null;

    Handler(Class<? extends Event> eventType) {
        this.eventType = eventType;
    }

    public List<Method> getMethods() {
        return this.invokers.stream().map(InvokeWrapper::getMethod).collect(Collectors.toList());
    }

    public boolean subscribe(InvokeWrapper invoker) {
        return invalidateCache(invokers.add(invoker));
    }

    public boolean unsubscribe(InvokeWrapper invoker) {
        return invalidateCache(invokers.remove(invoker));
    }

    public void post(Event event) {
        InvokeWrapper[] cache = this.computedInvokerCache;
        if (cache == null) {
            synchronized (this) {
                if ((cache = this.computedInvokerCache) == null) {
                    cache = this.computedInvokerCache = computeInvokerCache();
                }
            }
        }

        for (InvokeWrapper invoker : cache) {
            invoker.invoke(event);
        }
    }

    synchronized InvokeWrapper[] computeInvokerCache() {
        SortedSet<InvokeWrapper> set;
        if (hasSupertypeHandler()) {
            set = new TreeSet<>(this.invokers);
            for (Handler supertypeHandler : this.supertypeHandlers)
                set.addAll(supertypeHandler.invokers);
        } else {
            set = this.invokers;
        }
        return set.toArray(new InvokeWrapper[0]);
    }

    boolean invalidateCache(boolean modified) {
        if (modified) this.computedInvokerCache = null;
        return modified;
    }

    public Class<? extends Event> eventType() {
        return eventType;
    }

    public boolean isSubtypeOf(Class<?> cls) {
        Class<? extends Event> type = eventType();
        return type != cls && cls.isAssignableFrom(type);
    }

    public boolean isSubtypeOf(Handler handler) {
        return isSubtypeOf(handler.eventType());
    }

    public boolean hasSupertypeHandler() {
        return !supertypeHandlers.isEmpty();
    }

    public Set<Handler> getSupertypeHandlers() {
        return Collections.unmodifiableSet(supertypeHandlers);
    }

    boolean addSupertypeHandler(Handler handler) {
        if (handler == this) return false;
        return invalidateCache(supertypeHandlers.add(handler));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Handler)) return false;
        return Objects.equals(eventType, ((Handler) o).eventType);
    }

    @Override
    public int hashCode() {
        return eventType.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Handler{%s}", eventType.getName());
    }
}
