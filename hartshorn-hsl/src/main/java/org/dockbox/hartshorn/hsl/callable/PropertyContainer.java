package org.dockbox.hartshorn.hsl.callable;

import org.dockbox.hartshorn.hsl.token.Token;

public interface PropertyContainer {

    void set(Token name, Object value);

    Object get(Token name);
}
