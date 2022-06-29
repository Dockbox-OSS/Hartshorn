package org.dockbox.hartshorn.hsl.callable.virtual;

import org.dockbox.hartshorn.hsl.callable.PropertyContainer;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an instance of a {@link VirtualClass} inside a script. The instance is
 * identified by its {@link VirtualClass type}. The instance can carry a variety of
 * properties, which are not bound to a specific contract.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class VirtualInstance implements PropertyContainer {

    private final VirtualClass virtualClass;
    private final Map<String, Object> fields = new HashMap<>();

    public VirtualInstance(final VirtualClass virtualClass) {
        this.virtualClass = virtualClass;
    }

    /**
     * Gets the {@link VirtualClass} type of the instance.
     * @return The {@link VirtualClass} type of the instance.
     */
    public VirtualClass virtualClass() {
        return this.virtualClass;
    }

    @Override
    public void set(final Token name, final Object value) {
        this.fields.put(name.lexeme(), value);
    }

    @Override
    public Object get(final Token name) {
        if (this.fields.containsKey(name.lexeme())) {
            return this.fields.get(name.lexeme());
        }
        final VirtualFunction method = this.virtualClass.findMethod(name.lexeme());
        if (method != null) return method.bind(this);
        throw new RuntimeError(name, "Undefined property '" + name.lexeme() + "'.");
    }

    @Override
    public String toString() {
        return this.virtualClass.name() + " instance";
    }
}
