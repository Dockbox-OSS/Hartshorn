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

package org.dockbox.selene.common.events;

import com.google.inject.Singleton;

import org.dockbox.selene.api.annotations.event.Listener;
import org.dockbox.selene.api.annotations.event.filter.Packet;
import org.dockbox.selene.api.events.EventBus;
import org.dockbox.selene.api.events.EventStage;
import org.dockbox.selene.api.events.EventWrapper;
import org.dockbox.selene.api.events.packet.PacketEvent;
import org.dockbox.selene.api.events.parents.Event;
import org.dockbox.selene.api.events.processing.AbstractEventParamProcessor;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.util.Reflect;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.common.events.handle.EventHandlerRegistry;
import org.dockbox.selene.common.events.handle.SimpleEventWrapper;
import org.dockbox.selene.common.events.processors.DefaultParamProcessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

/**
 * A simple default implementation of {@link EventBus}, used for internal event posting and
 * handling.
 */
@Singleton
@SuppressWarnings({"unchecked", "EqualsWithItself", "VolatileArrayField"})
public class SimpleEventBus implements EventBus {

  /** A map of all listening objects with their associated {@link EventWrapper}s. */
  protected static final Map<Object, Set<EventWrapper>> listenerToInvokers = SeleneUtils.emptyMap();

  /** The internal registry of handlers for each event. */
  protected static final EventHandlerRegistry handlerRegistry = new EventHandlerRegistry();

  /** The internal map of {@link AbstractEventParamProcessor}s per annotation per stage. */
  protected static final Map<
          Class<? extends Annotation>, Map<EventStage, AbstractEventParamProcessor<?>>>
      parameterProcessors = SeleneUtils.emptyMap();

  /**
   * Subscribes all event listeners in a object instance. Typically event listeners are methods
   * annotated with {@link Listener}.
   *
   * @param object The instance of the listener
   */
  @Override
  public void subscribe(Object object) {
    if (!object.equals(object)) return;
    if (listenerToInvokers.containsKey(object)) {
      return; // Already registered
    }

    Set<EventWrapper> invokers = getInvokers(object);
    if (invokers.isEmpty()) {
      return; // Doesn't contain any listener methods
    }
    Selene.log().info("Registered {} as event listener", object.getClass().toGenericString());
    listenerToInvokers.put(object, invokers);
    for (EventWrapper invoker : invokers) {
      handlerRegistry.getHandler(invoker.getEventType()).subscribe(invoker);
    }
  }

  /**
   * Unsubscribes all event listeners in a object instance.
   *
   * @param object The instance of the listener
   */
  @Override
  public void unsubscribe(Object object) {
    if (!object.equals(object)) return;
    Set<EventWrapper> invokers = listenerToInvokers.remove(object);
    if (null == invokers || invokers.isEmpty()) {
      return; // Not registered
    }

    for (EventWrapper invoker : invokers) {
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

  @NotNull
  @Override
  public Map<Object, Set<EventWrapper>> getListenersToInvokers() {
    return listenerToInvokers;
  }

  @Override
  public void registerProcessors(@NotNull AbstractEventParamProcessor<?> @NotNull ... processors) {
    for (AbstractEventParamProcessor<?> processor : processors) {
      parameterProcessors.putIfAbsent(processor.getAnnotationClass(), SeleneUtils.emptyMap());
      parameterProcessors
          .get(processor.getAnnotationClass())
          .put(processor.targetStage(), processor);
    }
  }

  @Nullable
  @Override
  public <T extends Annotation> AbstractEventParamProcessor<T> getParamProcessor(
      @NotNull Class<T> annotation, EventStage stage) {
    if (SimpleEventBus.parameterProcessors.isEmpty()) {
      for (DefaultParamProcessors processor : DefaultParamProcessors.values()) {
        this.registerProcessors(processor.getProcessor());
      }
    }

    if (parameterProcessors.containsKey(annotation)) {
      return (AbstractEventParamProcessor<T>) parameterProcessors.get(annotation).get(stage);
    }
    return null;
  }

  /**
   * Gets all {@link EventWrapper} instances for a given listener instance.
   *
   * @param object The listener instance
   * @return The invokers
   */
  protected static Set<EventWrapper> getInvokers(Object object) {
    Set<EventWrapper> result = SeleneUtils.emptySet();
    for (Method method : Reflect.getMethodsRecursively(object.getClass())) {
      Listener annotation = Reflect.getAnnotationRecursively(method, Listener.class);
      if (null != annotation) {
        checkListenerMethod(method);
        result.addAll(SimpleEventWrapper.create(object, method, annotation.value().getPriority()));
      }
    }
    return result;
  }

  /**
   * Checks if a method is a valid listener method. A method is only valid if it:
   *
   * <ul>
   *   <li>Is annotated with {@link Listener}
   *   <li>Is not static
   *   <li>Is not abstract
   *   <li>Has at least one parameter which is a subcless of {@link Event}
   * </ul>
   *
   * @param method the method
   * @throws IllegalArgumentException the illegal argument exception
   */
  protected static void checkListenerMethod(Method method) throws IllegalArgumentException {
    if (!Reflect.isAnnotationPresentRecursively(method, Listener.class)) {
      throw new IllegalArgumentException("Needs @Listener annotation: " + method.toGenericString());
    }

    int modifiers = method.getModifiers();
    if (Modifier.isAbstract(modifiers)) {
      throw new IllegalArgumentException("Method cannot be abstract: " + method.toGenericString());
    }

    if (0 == method.getParameterCount()) {
      throw new IllegalArgumentException(
          "Must have at least one parameter: " + method.toGenericString());
    }

    for (Class<?> param : method.getParameterTypes()) {
      if (Reflect.isAssignableFrom(Event.class, param)) {
        if (Reflect.isAssignableFrom(PacketEvent.class, param)
            && !method.isAnnotationPresent(Packet.class)) {
          throw new IllegalArgumentException(
              "Needs @Packet annotation: " + method.toGenericString());
        }
        return;
      }
    }

    throw new IllegalArgumentException(
        "Parameter must be a subclass of the Event class: " + method.toGenericString());
  }
}
