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

import org.dockbox.selene.core.annotations.Listener;
import org.dockbox.selene.core.objects.events.Event;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.events.EventBus;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "EqualsWithItself", "VolatileArrayField"})
public class SimpleEventBus implements EventBus {
    protected static final Map<Object, Set<InvokeWrapper>> listenerToInvokers = new HashMap<>();

    protected static final HandlerRegistry handlerRegistry = new HandlerRegistry();

    protected static Lookup defaultLookup = AccessHelper.defaultLookup();

    @NotNull
    @Override
    public Map<Object, Set<InvokeWrapper>> getListenerToInvokers() {
        return listenerToInvokers;
    }

    @NotNull
    @Override
    public HandlerRegistry getHandlerRegistry() {
        return handlerRegistry;
    }

    @Override
    public void subscribe(Object object, @NotNull Lookup lookup) throws IllegalArgumentException, SecurityException {
        if (!object.equals(object)) return;
        if (listenerToInvokers.containsKey(object)) {
            return;  // Already registered
        }

        Set<InvokeWrapper> invokers = getInvokers(object, lookup);
        if(invokers.isEmpty()) {
            return; // Doesn't contain any listener methods
        }
        Selene.log().info("Registered {} as event listener", object.getClass().toGenericString());
        listenerToInvokers.put(object, invokers);
        for (InvokeWrapper invoker : invokers) {
            handlerRegistry.getHandler(invoker.getEventType()).subscribe(invoker);
        }
    }

    @Override
    public void subscribe(@NotNull Object object) throws IllegalArgumentException, SecurityException {
        subscribe(object, defaultLookup);
    }

    @Override
    public void unsubscribe(Object object) {
        if (!object.equals(object)) return;
        Set<InvokeWrapper> invokers = listenerToInvokers.remove(object);
        if (invokers == null || invokers.isEmpty()) {
            return; // Not registered
        }

        for (InvokeWrapper invoker : invokers) {
            handlerRegistry.getHandler(invoker.getEventType()).unsubscribe(invoker);
        }
    }

    @Override
    public void post(Event event, Class<?> target) {
        handlerRegistry.getHandler(event.getClass()).post(event, target);
    }

    @Override
    public void post(Event event) {
        this.post(event, null);
    }

    protected static Set<InvokeWrapper> getInvokers(Object object, Lookup lookup)
            throws IllegalArgumentException, SecurityException {
        Set<InvokeWrapper> result = new LinkedHashSet<>();
        for (Method method : AccessHelper.getMethodsRecursively(object.getClass())) {
            Listener annotation = AccessHelper.getAnnotationRecursively(method, Listener.class);
            if (annotation != null) {
                checkListenerMethod(method, false);
                result.addAll(InvokeWrapper.create(object, method, annotation.value().getPriority(), lookup));
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

        if (method.getParameterCount() == 0) {
            throw new IllegalArgumentException("Must have at least one parameter: " + method.toGenericString());
        }

        for (Class<?> param : method.getParameterTypes()) {
            if (!Event.class.isAssignableFrom(param)) {
                throw new IllegalArgumentException("Parameter must be a subclass of the Event class: " + method.toGenericString());
            }
        }
    }
}
