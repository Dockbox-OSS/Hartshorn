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

package org.dockbox.selene.core.impl.util.events;

import org.dockbox.selene.core.objects.events.Event;
import org.dockbox.selene.core.util.events.IHandler;
import org.dockbox.selene.core.util.events.IWrapper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Handler implements IHandler {
    private final Class<? extends Event> eventType;

    private final Set<Handler> supertypeHandlers = new HashSet<>();

    private final SortedSet<InvokeWrapper> invokers = new TreeSet<>(InvokeWrapper.COMPARATOR);

    private transient volatile InvokeWrapper @Nullable [] computedInvokerCache;

    Handler(Class<? extends Event> eventType) {
        this.eventType = eventType;
    }

    @Override
    public List<Method> getMethods() {
        return this.invokers.stream().map(InvokeWrapper::getMethod).collect(Collectors.toList());
    }

    @Override
    public void subscribe(IWrapper invoker) {
        if (invoker instanceof InvokeWrapper)
            this.invalidateCache(this.invokers.add((InvokeWrapper) invoker));
    }

    @Override
    public void unsubscribe(IWrapper invoker) {
        if (invoker instanceof InvokeWrapper)
            this.invalidateCache(this.invokers.remove(invoker));
    }

    @Override
    public void post(Event event, Class<?> target) {
        InvokeWrapper[] cache = this.computedInvokerCache;
        if (null == cache) {
            synchronized (this) {
                if (null == (cache = this.computedInvokerCache)) {
                    cache = this.computedInvokerCache = this.computeInvokerCache();
                }
            }
        }

        for (InvokeWrapper invoker : cache) {
            // Target is null if no specific target should be checked
            // If the target is present we only want to invoke when the listener matches our target
            if (null == target || invoker.getListener().getClass().equals(target))
                invoker.invoke(event);
        }
    }

    private synchronized InvokeWrapper[] computeInvokerCache() {
        SortedSet<InvokeWrapper> set;
        if (this.hasSupertypeHandler()) {
            set = new TreeSet<>(this.invokers);
            for (Handler supertypeHandler : this.supertypeHandlers)
                set.addAll(supertypeHandler.invokers);
        } else {
            set = this.invokers;
        }
        return set.toArray(new InvokeWrapper[0]);
    }

    private boolean invalidateCache(boolean modified) {
        if (modified) this.computedInvokerCache = null;
        return modified;
    }

    private Class<? extends Event> eventType() {
        return this.eventType;
    }

    private boolean isSubtypeOf(Class<?> cls) {
        Class<? extends Event> type = this.eventType();
        return type != cls && cls.isAssignableFrom(type);
    }

    @Override
    public boolean isSubtypeOf(IHandler handler) {
        if (handler instanceof Handler)
            return this.isSubtypeOf(((Handler) handler).eventType());
        return false;
    }

    private boolean hasSupertypeHandler() {
        return !this.supertypeHandlers.isEmpty();
    }

    @Override
    public Set<IHandler> getSupertypeHandlers() {
        return Collections.unmodifiableSet(this.supertypeHandlers);
    }

    @Override
    public boolean addSupertypeHandler(IHandler handler) {
        if (!(handler instanceof Handler)) return false;
        if (handler == this) return false;
        return this.invalidateCache(this.supertypeHandlers.add((Handler) handler));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Handler)) return false;
        return Objects.equals(this.eventType, ((Handler) o).eventType);
    }

    @Override
    public int hashCode() {
        return this.eventType.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Handler{%s}", this.eventType.getName());
    }
}
