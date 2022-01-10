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

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.events.EventWrapper;
import org.dockbox.hartshorn.events.parents.Event;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class EventHandler {

    private final TypeContext<? extends Event> eventType;
    private final Set<EventHandler> superTypeHandlers = HartshornUtils.emptyConcurrentSet();
    private final SortedSet<EventWrapperImpl<?>> invokers = new TreeSet<>(EventWrapperImpl.COMPARATOR);
    private transient EventWrapperImpl<?>[] computedInvokerCache;

    EventHandler(final TypeContext<? extends Event> eventType) {
        this.eventType = eventType;
    }

    public List<MethodContext<?, ?>> methods() {
        return this.invokers.stream().map(EventWrapperImpl::method).collect(Collectors.toList());
    }

    public void subscribe(final EventWrapper invoker) {
        if (invoker instanceof EventWrapperImpl)
            this.invalidateCache(this.invokers.add((EventWrapperImpl<?>) invoker));
    }

    private boolean invalidateCache(final boolean modified) {
        if (modified) this.computedInvokerCache = null;
        return modified;
    }

    public void unsubscribe(final EventWrapper invoker) {
        if (invoker instanceof EventWrapperImpl) this.invalidateCache(this.invokers.remove(invoker));
    }

    public void post(final Event event, final Key<?> target) {
        EventWrapperImpl<?>[] cache = this.computedInvokerCache;
        if (null == cache) {
            synchronized (this) {
                if (null == (cache = this.computedInvokerCache)) {
                    cache = this.computedInvokerCache = this.computeInvokerCache();
                }
            }
        }

        for (final EventWrapperImpl<?> invoker : cache) {
            // Target is null if no specific target should be checked
            // If the target is present we only want to invoke when the listener matches our target
            if (null == target || invoker.listenerType().equals(target)) invoker.invoke(event);
        }
    }

    private synchronized EventWrapperImpl<?>[] computeInvokerCache() {
        final SortedSet<EventWrapperImpl<?>> set;
        if (this.hasSupertypeHandler()) {
            set = new TreeSet<>(this.invokers);
            for (final EventHandler supertypeHandler : this.superTypeHandlers)
                set.addAll(supertypeHandler.invokers);
        }
        else {
            set = this.invokers;
        }
        return set.toArray(new EventWrapperImpl[0]);
    }

    private boolean hasSupertypeHandler() {
        return !this.superTypeHandlers.isEmpty();
    }

    public boolean subtypeOf(final EventHandler handler) {
        if (handler != null) return this.eventType().childOf(handler.eventType());
        return false;
    }

    private TypeContext<? extends Event> eventType() {
        return this.eventType;
    }

    public boolean addSuperTypeHandler(final EventHandler handler) {
        if (handler == null) return false;
        if (handler == this) return false;
        return this.invalidateCache(this.superTypeHandlers.add(handler));
    }

    @Override
    public int hashCode() {
        return this.eventType.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof EventHandler)) return false;
        return Objects.equals(this.eventType, ((EventHandler) o).eventType);
    }

    @Override
    public String toString() {
        return String.format("Handler{%s}", this.eventType.name());
    }
}
