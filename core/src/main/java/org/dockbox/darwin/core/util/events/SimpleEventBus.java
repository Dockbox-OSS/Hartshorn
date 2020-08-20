package org.dockbox.darwin.core.util.events;

import org.dockbox.darwin.core.annotations.Listener;
import org.dockbox.darwin.core.objects.events.Event;
import org.dockbox.darwin.core.server.Server;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@SuppressWarnings({"unchecked", "EqualsWithItself", "VolatileArrayField"})
public class SimpleEventBus implements EventBus {
    protected static final Map<Object, Set<InvokeWrapper>> listenerToInvokers = new HashMap<>();

    protected static final HandlerRegistry handlerRegistry = new HandlerRegistry();

    protected static Lookup defaultLookup = AccessHelper.defaultLookup();

    public void subscribe(Object object, @NotNull Lookup lookup) throws IllegalArgumentException, SecurityException {
        if (!object.equals(object)) return;
        if (listenerToInvokers.containsKey(object)) {
            return;  // Already registered
        }

        Set<InvokeWrapper> invokers = getInvokers(object, lookup);
        if(invokers.isEmpty()) {
            return; // Doesn't contain any listener methods
        }
        Server.log().info("Registered {} as event listener", object.getClass().toGenericString());
        listenerToInvokers.put(object, invokers);
        for (InvokeWrapper invoker : invokers) {
            handlerRegistry.getHandler(invoker.eventType).subscribe(invoker);
        }
    }

    public void subscribe(Object object) throws IllegalArgumentException, SecurityException {
        subscribe(object, defaultLookup);
    }

    public void unsubscribe(Object object) {
        if (!object.equals(object)) return;
        Set<InvokeWrapper> invokers = listenerToInvokers.remove(object);
        if (invokers == null || invokers.isEmpty()) {
            return; // Not registered
        }

        for (InvokeWrapper invoker : invokers) {
            handlerRegistry.getHandler(invoker.eventType).unsubscribe(invoker);
        }
    }

    public void post(Event event) {
        handlerRegistry.getHandler(event.getClass()).post(event);
    }

    public void setDefaultLookup(Lookup lookup) {
        this.defaultLookup = Objects.requireNonNull(lookup);
    }

    protected static Set<InvokeWrapper> getInvokers(Object object, Lookup lookup)
            throws IllegalArgumentException, SecurityException {
        Set<InvokeWrapper> result = new LinkedHashSet<>();
        for (Method method : AccessHelper.getMethodsRecursively(object.getClass())) {
            Listener annotation = AccessHelper.getAnnotationRecursively(method, Listener.class);
            if (annotation != null) {
                checkListenerMethod(method, false);
                result.add(InvokeWrapper.create(object, method, annotation.value().getPriority(), lookup));
            }
        }
        return result;
    }

    protected static void checkListenerMethod(Method method, boolean checkAnnotation) throws IllegalArgumentException {
        if (checkAnnotation && !AccessHelper.isAnnotationPresentRecursively(method, Listener.class)) {
            throw new IllegalArgumentException("Needs @Listener annotation: " + method.toGenericString());
        }

        int modifiers = method.getModifiers();
        if (Modifier.isStatic(modifiers)) {
            throw new IllegalArgumentException("Method cannot be static: " + method.toGenericString());
        }
        if (Modifier.isAbstract(modifiers)) {
            throw new IllegalArgumentException("Method cannot be abstract: " + method.toGenericString());
        }

        if (method.getParameterCount() != 1) {
            throw new IllegalArgumentException("Must have exactly one parameter: " + method.toGenericString());
        }
        if (!Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
            throw new IllegalArgumentException("Parameter must be a subclass of the Event class: " + method.toGenericString());
        }
    }

    public static boolean isListenerMethod(Method method) {
        if (AccessHelper.isAnnotationPresentRecursively(method, Listener.class) &&
                method.getParameterCount() == 1 &&
                Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
            int modifiers = method.getModifiers();
            return !Modifier.isStatic(modifiers) && !Modifier.isAbstract(modifiers);
        }
        return false;
    }

    static final class HandlerRegistry {
        private final Map<Class<? extends Event>, Handler> handlers = new HashMap<>();

        public Handler getHandler(Class<? extends Event> type) {
            Handler handler = handlers.get(type);
            if (handler == null) {
                computeHierarchy(handler = new Handler(type));
                handlers.put(type, handler);
            }
            return handler;
        }

        boolean computeHierarchy(Handler subject) {
            boolean associationFound = false;
            for (Handler handler : handlers.values()) {
                if (subject == handler) continue;
                if (subject.isSubtypeOf(handler)) {
                    associationFound |= subject.addSupertypeHandler(handler);
                } else if (handler.isSubtypeOf(subject)) {
                    associationFound |= handler.addSupertypeHandler(subject);
                }
            }
            return associationFound;
        }
    }

    static class Handler {
        private final Class<? extends Event> eventType;

        private final Set<Handler> supertypeHandlers = new HashSet<>();

        private final SortedSet<InvokeWrapper> invokers = new TreeSet<>(InvokeWrapper.COMPARATOR);

        private transient volatile InvokeWrapper[] computedInvokerCache = null;

        Handler(Class<? extends Event> eventType) { this.eventType = eventType; }

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

    static class InvokeWrapper implements Comparable<InvokeWrapper> {
        public static final Comparator<InvokeWrapper> COMPARATOR = (o1, o2) -> {
            if (fastEqual(o1, o2)) return 0;

            // @formatter:off
            int c;
            if ((c = Integer.compare(o1.priority, o2.priority))                         != 0) return c;
            if ((c = o1.method.getName().compareTo(o2.method.getName()))                != 0) return c;
            if ((c = o1.eventType.getName().compareTo(o2.eventType.getName()))          != 0) return c;
            if ((c = Integer.compare(o1.listener.hashCode(), o2.listener.hashCode()))   != 0) return c;
            if ((c = Integer.compare(o1.hashCode(), o2.hashCode()))                     != 0) return c;
            // @formatter:on
            throw new AssertionError();  // ensures the comparator will never return 0 if the two wrapper aren't equal
        };

        public static InvokeWrapper create(Object instance, Method method, Lookup lookup) throws SecurityException {
            int priority = AccessHelper.getAnnotationRecursively(method, Listener.class).value().getPriority();
            return create(instance, method, priority, lookup);
        }

        public static InvokeWrapper create(Object instance, Method method, int priority, Lookup lookup)
                throws SecurityException {
            Class<? extends Event> eventType = (Class<? extends Event>) method.getParameterTypes()[0];
            MethodHandle methodHandle = AccessHelper.unreflectMethodHandle(lookup, method);
            return new InvokeWrapper(instance, eventType, method, priority, methodHandle);
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
                methodHandle.invoke(listener, event);
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
            n = 31 * n + listener.hashCode();
            n = 31 * n + eventType.hashCode();
            n = 31 * n + method.hashCode();
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
                    listener, eventType.getName(), method.getName(), eventType.getSimpleName(), priority);
        }
    }
}
