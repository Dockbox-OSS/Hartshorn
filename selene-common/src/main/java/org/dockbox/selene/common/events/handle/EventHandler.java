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

package org.dockbox.selene.common.events.handle;

import org.dockbox.selene.api.events.EventWrapper;
import org.dockbox.selene.api.events.parents.Event;
import org.dockbox.selene.api.util.Reflect;
import org.dockbox.selene.api.util.SeleneUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class EventHandler {

    private final Class<? extends Event> eventType;

    private final Set<EventHandler> supertypeHandlers = SeleneUtils.emptySet();

    private final SortedSet<SimpleEventWrapper> invokers = new TreeSet<>(SimpleEventWrapper.COMPARATOR);

    private transient volatile SimpleEventWrapper @Nullable [] computedInvokerCache;

    EventHandler(Class<? extends Event> eventType) {
        this.eventType = eventType;
    }

    public List<Method> getMethods() {
        return this.invokers.stream().map(SimpleEventWrapper::getMethod).collect(Collectors.toList());
    }

    public void subscribe(EventWrapper invoker) {
        if (invoker instanceof SimpleEventWrapper)
            this.invalidateCache(this.invokers.add((SimpleEventWrapper) invoker));
    }

    private boolean invalidateCache(boolean modified) {
        if (modified) this.computedInvokerCache = null;
        return modified;
    }

    public void unsubscribe(EventWrapper invoker) {
        if (invoker instanceof SimpleEventWrapper) this.invalidateCache(this.invokers.remove(invoker));
    }

    public void post(Event event, Class<?> target) {
        SimpleEventWrapper[] cache = this.computedInvokerCache;
        if (null == cache) {
            synchronized (this) {
                if (null == (cache = this.computedInvokerCache)) {
                    cache = this.computedInvokerCache = this.computeInvokerCache();
                }
            }
        }

        for (SimpleEventWrapper invoker : cache) {
            // Target is null if no specific target should be checked
            // If the target is present we only want to invoke when the listener matches our target
            if (null == target || invoker.getListener().getClass().equals(target)) invoker.invoke(event);
        }
    }

    private synchronized SimpleEventWrapper[] computeInvokerCache() {
        SortedSet<SimpleEventWrapper> set;
        if (this.hasSupertypeHandler()) {
            set = new TreeSet<>(this.invokers);
            for (EventHandler supertypeHandler : this.supertypeHandlers)
                set.addAll(supertypeHandler.invokers);
        }
        else {
            set = this.invokers;
        }
        return set.toArray(new SimpleEventWrapper[0]);
    }

    private boolean hasSupertypeHandler() {
        return !this.supertypeHandlers.isEmpty();
    }

    public boolean isSubtypeOf(EventHandler handler) {
        if (handler != null) return this.isSubtypeOf(handler.eventType());
        return false;
    }

    private boolean isSubtypeOf(Class<?> cls) {
        Class<? extends Event> type = this.eventType();
        return type != cls && Reflect.isAssignableFrom(cls, type);
    }

    private Class<? extends Event> eventType() {
        return this.eventType;
    }

    public Set<EventHandler> getSupertypeHandlers() {
        return Collections.unmodifiableSet(this.supertypeHandlers);
    }

    public boolean addSupertypeHandler(EventHandler handler) {
        if (handler == null) return false;
        if (handler == this) return false;
        return this.invalidateCache(this.supertypeHandlers.add(handler));
    }

    @Override
    public int hashCode() {
        return this.eventType.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventHandler)) return false;
        return Objects.equals(this.eventType, ((EventHandler) o).eventType);
    }

    @Override
    public String toString() {
        return String.format("Handler{%s}", this.eventType.getName());
    }
}
