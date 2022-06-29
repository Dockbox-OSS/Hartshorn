package org.dockbox.hartshorn.hsl.callable;

import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;

/**
 * Represents a class instance which is capable of carrying properties. This instance can
 * be virtual or native, depending on its implementation.
 *
 * @author Guus Lieben
 * @since 22.4
 * @see org.dockbox.hartshorn.hsl.callable.virtual.VirtualInstance
 * @see org.dockbox.hartshorn.hsl.callable.external.ExternalInstance
 */
public interface PropertyContainer {

    /**
     * Sets a property on the instance. If the property is not supported or accessible,
     * an {@link RuntimeError} is thrown.
     *
     * @param name The name of the property.
     * @param value The value of the property.
     * @throws RuntimeError If the property is not supported or not accessible.
     */
    void set(Token name, Object value);

    /**
     * Gets a property from the instance. This may either return a {@link CallableNode}
     * if the property is a function, or any other {@link Object} if the property is
     * a field. If the property is not supported or accessible, a {@link RuntimeError}
     * is thrown.
     *
     * @param name The name of the property.
     * @return The value of the property.
     * @throws RuntimeError If the property is not supported or accessible.
     */
    Object get(Token name);
}
