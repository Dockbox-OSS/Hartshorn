package org.dockbox.hartshorn.hsl.callable.external;

import org.dockbox.hartshorn.hsl.callable.PropertyContainer;
import org.dockbox.hartshorn.hsl.token.Token;

public class ExternalInstance implements PropertyContainer {

    private final Object instance;

    public ExternalInstance(final Object instance) {
        this.instance = instance;
    }

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
