package org.dockbox.hartshorn.hsl.callable;

import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;

public interface ErrorReporter {
    void error(int line, String message);

    void error(String message);

    void error(Token token, String message);

    void runtimeError(RuntimeError error);
}
