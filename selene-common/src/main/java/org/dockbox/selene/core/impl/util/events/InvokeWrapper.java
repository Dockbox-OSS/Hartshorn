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

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.util.eventbus.EventHandler.Priority;

import org.dockbox.selene.core.annotations.AsyncEvent;
import org.dockbox.selene.core.annotations.Filter;
import org.dockbox.selene.core.annotations.Filters;
import org.dockbox.selene.core.annotations.Getter;
import org.dockbox.selene.core.annotations.IsCancelled;
import org.dockbox.selene.core.annotations.Provided;
import org.dockbox.selene.core.annotations.SkipIf;
import org.dockbox.selene.core.annotations.WrapSafe;
import org.dockbox.selene.core.exceptions.SkipEventException;
import org.dockbox.selene.core.objects.events.Cancellable;
import org.dockbox.selene.core.objects.events.Event;
import org.dockbox.selene.core.objects.events.Filterable;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.core.util.events.IWrapper;
import org.dockbox.selene.core.util.extension.Extension;
import org.dockbox.selene.core.util.threads.ThreadUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class InvokeWrapper implements Comparable<InvokeWrapper>, IWrapper {
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
            if (Event.class.isAssignableFrom(param)) {
                Class<? extends Event> eventType = (Class<? extends Event>) param;
                invokeWrappers.add(new InvokeWrapper(instance, eventType, method, priority));
            } else if (com.sk89q.worldedit.event.Event.class.isAssignableFrom(param)) {
                WorldEdit.getInstance().getEventBus().subscribe(param,
                        new MethodEventHandler(
                                Priority.EARLY,
                                instance,
                                method
                        ));
            }
        }
        return invokeWrappers;
    }

    private final Object listener;

    private final Class<? extends Event> eventType;

    private final Method method;

    private final int priority;

    InvokeWrapper(Object listener, Class<? extends Event> eventType, Method method, int priority) {
        this.listener = listener;
        this.eventType = eventType;
        this.method = method;
        this.priority = priority;
    }

    @Override
    public void invoke(Event event) throws RuntimeException {
        try {
            Collection<Object> args = this.getEventArgs(event);

            if (!this.method.isAccessible()) {
                this.method.setAccessible(true);
            }

            if (this.filtersMatch(event) && this.acceptsState(event)) {
                Runnable eventRunner = () -> {
                    try {
                        this.method.invoke(this.listener, args.toArray());
                    } catch (Throwable e) {
                        Selene.getServer().except("Could not finish event runner", e);
                    }
                };

                ThreadUtils tu = Selene.getInstance(ThreadUtils.class);
                if (this.method.isAnnotationPresent(AsyncEvent.class)) {
                    tu.performAsync(eventRunner);
                } else {
                    tu.performSync(eventRunner).get();
                }
            }
        } catch (SkipEventException ignored) {
        } catch (InterruptedException | ExecutionException e) {
            Selene.getServer().except("Sync event execution interrupted", e);
        }
    }

    private boolean acceptsState(Event event) {
        if (event instanceof Cancellable) {
            Cancellable cancellable = (Cancellable) event;
            if (this.method.isAnnotationPresent(IsCancelled.class)) {
                switch (this.method.getAnnotation(IsCancelled.class).value()) {
                    case TRUE:
                        return cancellable.isCancelled();
                    case FALSE: // Default behavior
                        return !cancellable.isCancelled();
                    case UNDEFINED: // Either is accepted
                        return true;
                }
            } else return !cancellable.isCancelled();
        }
        return true;
    }

    @NotNull
    private Collection<Object> getEventArgs(Event event) throws SkipEventException {
        Collection<Object> args = new ArrayList<>();
        for (Class<?> type : this.method.getParameterTypes()) {
            boolean wrapSafe = type.isAnnotationPresent(WrapSafe.class);

            if (type.isAssignableFrom(event.getClass())) {
                if (wrapSafe) Selene.log().warn("Event parameter cannot be wrapped");
                args.add(event);

            } else if (type.isAnnotationPresent(Getter.class)) {
                Object finalArg = this.getterArgument(event, type);
                if (wrapSafe) args.add(Exceptional.ofNullable(finalArg));
                else args.add(finalArg);

            } else if (type.isAnnotationPresent(Provided.class)) {
                Object instance = this.providedArgument(type);
                if (wrapSafe) args.add(Exceptional.ofNullable(instance));
                else args.add(instance);

            } else {
                if (wrapSafe) args.add(Exceptional.empty());
                else args.add(null);
            }
        }
        return args;
    }

    private Object getterArgument(Event event, Class<?> type) throws SkipEventException {
        Getter getter = type.getAnnotation(Getter.class);
        AtomicReference<Object> arg = new AtomicReference<>(null);
        SeleneUtils.getMethodValue(event, getter.value(), type)
                .ifPresent(arg::set);

        Object finalArg = arg.get();

        this.verifySkip(type, finalArg);
        return finalArg;
    }

    private void verifySkip(Class<?> type, Object finalArg) throws SkipEventException {
        if (type.isAnnotationPresent(SkipIf.class)) {
            SkipIf skip = type.getAnnotation(SkipIf.class);
            switch (skip.value()) {
                case NULL:
                    if (null == finalArg) throw new SkipEventException();
                    break;
                case EMPTY:
                    if (SeleneUtils.isEmpty(finalArg)) throw new SkipEventException();
                    break;
                case ZERO:
                    if (finalArg instanceof Number && 0 == ((Number) finalArg).floatValue())
                        throw new SkipEventException();
                    break;
            }
        }
    }

    private Object providedArgument(Class<?> type) {
        Provided provided = type.getAnnotation(Provided.class);

        Class<?> extensionClass = type;
        if (Void.class != provided.value() && provided.value().isAnnotationPresent(Extension.class)) {
            extensionClass = provided.value();
        } else if (this.listener.getClass().isAnnotationPresent(Extension.class)) {
            extensionClass = this.listener.getClass();
        }
        return Selene.getInstance(type, extensionClass);
    }


    private boolean filtersMatch(Event event) {
        if (event instanceof Filterable) {
            if (this.method.isAnnotationPresent(Filter.class)) {
                Filter filter = this.method.getAnnotation(Filter.class);
                return this.testFilter(filter, (Filterable) event);
            } else if (this.method.isAnnotationPresent(Filters.class)) {
                Filters filters = this.method.getAnnotation(Filters.class);
                for (Filter filter : filters.value()) {
                    if (!this.testFilter(filter, (Filterable) event)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean testFilter(Filter filter, Filterable event) {
        if (event.acceptedParams().contains(filter.param()) && event.acceptedFilters().contains(filter.type())) {
            return event.isApplicable(filter);
        }
        return false;
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

    @Override
    public Object getListener() {
        return this.listener;
    }

    @Override
    public Class<? extends Event> getEventType() {
        return this.eventType;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

}
