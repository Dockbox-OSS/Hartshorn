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

package org.dockbox.hartshorn.events.handle;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.events.EventExecutionFilterContext;
import org.dockbox.hartshorn.events.EventWrapper;
import org.dockbox.hartshorn.events.parents.Event;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EventHandler {

    private final TypeView<? extends Event> eventType;
    private final Set<EventHandler> superTypeHandlers = ConcurrentHashMap.newKeySet();
    private final Set<EventExecutionFilter> executionFilters = ConcurrentHashMap.newKeySet();
    private final SortedSet<EventWrapperImpl<?>> invokers = new TreeSet<>(EventWrapperImpl.COMPARATOR);
    private transient EventWrapperImpl<?>[] computedInvokerCache;

    EventHandler(final TypeView<? extends Event> eventType) {
        this.eventType = eventType;
    }

    public List<MethodView<?, ?>> methods() {
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

    public void post(final Event event, final ComponentKey<?> target) {
        EventWrapperImpl<?>[] cache = this.computedInvokerCache;
        if (null == cache) {
            synchronized (this) {
                if (null == this.computedInvokerCache) {
                    cache = this.computeInvokerCache();
                    this.computedInvokerCache = cache;
                }
            }
        }

        final EventExecutionFilterContext filterContext = event.applicationContext().first(EventExecutionFilterContext.class).get();

        for (final EventWrapperImpl<?> invoker : cache) {
            // Target is null if no specific target should be checked
            // If the target is present we only want to invoke when the listener matches our target
            if (null == target || invoker.listenerType().equals(target)) {
                boolean invoke = true;
                for (final EventExecutionFilter executionFilter : filterContext.filters()) {
                    if (!executionFilter.accept(event, invoker, target)) {
                        invoke = false;
                        break;
                    }
                }
                if (invoke) invoker.invoke(event);
            }
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
        if (handler != null) return this.eventType().isChildOf(handler.eventType().type());
        return false;
    }

    private TypeView<? extends Event> eventType() {
        return this.eventType;
    }

    public boolean addSuperTypeHandler(final EventHandler handler) {
        if (handler == null) return false;
        if (handler == this) return false;
        return this.invalidateCache(this.superTypeHandlers.add(handler));
    }

    public boolean addFilter(final EventExecutionFilter eventExecutionFilter) {
        return this.executionFilters.add(eventExecutionFilter);
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
