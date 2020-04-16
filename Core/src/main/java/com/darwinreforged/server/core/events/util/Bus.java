package com.darwinreforged.server.core.events.util;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

/**
 * Basic event bus.
 *
 * @author Matt, Andy Li
 */
public final class Bus {
    private static final EventBus EVENT_BUS = new EventBus();

    /**
     * Registers all listener methods on {@code object} for receiving events.
     *
     * @param object object whose listener methods should be registered
     * @param lookup the {@linkplain MethodHandles.Lookup Lookup object} used in {@link MethodHandle} creation
     * @throws IllegalArgumentException if there's an invalid listener method on the {@code object},
     *                                  or the {@code object} doesn't have any listener methods
     * @throws SecurityException        if a security manager denied access to the declared methods
     *                                  of the class of the {@code object}, or the provided
     *                                  {@linkplain MethodHandles.Lookup lookup object}
     *                                  cannot access one of the listener method found in the class
     * @see EventBus#subscribe(Object, MethodHandles.Lookup)
     * @see MethodHandles.Lookup
     * @since 1.3
     */
    public static void subscribe(Object object, MethodHandles.Lookup lookup) throws IllegalArgumentException, SecurityException {
        EVENT_BUS.subscribe(object, lookup);
    }

    /**
     * Registers all listener methods on {@code object} for receiving events.
     *
     * @param object object whose listener methods should be registered
     * @throws IllegalArgumentException if there's an invalid listener method on the {@code object},
     *                                  or the {@code object} doesn't have any listener methods
     * @throws SecurityException        if a security manager denied access to the declared methods
     *                                  of the class of the {@code object}, or the default
     *                                  {@linkplain MethodHandles.Lookup lookup object} cannot access
     *                                  one of the listener method found in the class
     * @see EventBus#subscribe(Object)
     */
    public static void subscribe(Object object) throws IllegalArgumentException, SecurityException {
        EVENT_BUS.subscribe(object);
    }

    /**
     * Unregisters all listener methods on the {@code object}.
     *
     * @param object object whose listener methods should be unregistered
     * @see EventBus#unsubscribe(Object)
     */
    public static void unsubscribe(Object object) {
        EVENT_BUS.unsubscribe(object);
    }

    /**
     * Posts an event to all registered listeners.
     *
     * @param event event to post
     */
    public static void post(Event event) {
        EVENT_BUS.post(event);
    }

    private Bus() {}
}
