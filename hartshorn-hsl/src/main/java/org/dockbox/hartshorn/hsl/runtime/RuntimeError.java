package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.hsl.token.Token;

public class RuntimeError extends RuntimeException {

    private final Token token;

    public RuntimeError(final Token token, final String message) {
        super(message);
        this.token = token;
    }

    public Token getToken() {
        return this.token;
    }
}
