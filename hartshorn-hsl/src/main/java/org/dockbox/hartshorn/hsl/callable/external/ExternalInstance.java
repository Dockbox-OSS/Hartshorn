package org.dockbox.hartshorn.hsl.callable.external;

import org.dockbox.hartshorn.hsl.callable.PropertyContainer;
import org.dockbox.hartshorn.hsl.runtime.StandardRuntime;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.Map;

/**
 * Represents a single nullable {@link Object} instance that can be accessed from an HSL
 * runtime. This instance can be used to access properties of the instance. The instance
 * needs to be made available to the runtime through {@link StandardRuntime#global(String, Object)}
 * or {@link StandardRuntime#global(Map)}, where the instance is made available globally
 * to the runtime.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class ExternalInstance implements PropertyContainer {

    private final Object instance;

    public ExternalInstance(final Object instance) {
        this.instance = instance;
    }

    /**
     * Returns the {@link Object} instance represented by this instance.
     * @return The {@link Object} instance represented by this instance.
     */
    public Object instance() {
        return this.instance;
    }

    @Override
    public void set(final Token name, final Object value) {
        throw new UnsupportedOperationException("Cannot modify fields of external instances.");
    }

    @Override
    public Object get(final Token name) {
        return new ExternalFunction(this.instance, name.lexeme());
    }

    @Override
    public String toString() {
        return String.valueOf(this.instance);
    }
}
