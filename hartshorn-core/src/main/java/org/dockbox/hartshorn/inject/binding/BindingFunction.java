package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.function.Supplier;

public interface BindingFunction<T> {

    /**
     * Binds to the given type, this will create a new instance of the given type
     * every time it is requested.
     *
     * @param type The type to bind to
     * @return The binder
     */
    Binder to(Class<? extends T> type);

    /**
     * Binds to the given type, this will create a new instance of the given type
     * every time it is requested.
     *
     * @param type The type to bind to
     * @return The binder
     */
    Binder to(TypeContext<? extends T> type);

    /**
     * Binds to the given supplier, this will call the supplier every time it is
     * requested.
     *
     * @param supplier The supplier to bind to
     * @return The binder
     */
    Binder to(Supplier<T> supplier);

    /**
     * Binds to the given instance, this will always return the same instance
     * every time it is requested. This may not enhance the instance before it
     * is returned.
     *
     * @param t The instance to bind to
     * @return The binder
     */
    Binder singleton(T t);

    /**
     * Binds to a supplier that will provide a lazy instance of the given type
     * every time it is requested. This will create the instance the first time
     * it is requested and then return the same instance every time it is
     * requested.
     *
     * @param type The type to bind to
     * @return The binder
     */
    Binder lazySingleton(Class<T> type);

    /**
     * Binds to a supplier that will provide a lazy instance of the given type
     * every time it is requested. This will create the instance the first time
     * it is requested and then return the same instance every time it is
     * requested.
     *
     * @param type The type to bind to
     * @return The binder
     */
    Binder lazySingleton(TypeContext<T> type);

    /**
     * Binds to a supplier that will provide a lazy instance of the given type
     * every time it is requested. This will request the given supplier the first
     * time it is requested and then return the same instance every time it is
     * requested.
     *
     * @param supplier The supplier to bind to
     * @return The binder
     */
    Binder lazySingleton(Supplier<T> supplier);
}
