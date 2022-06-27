package org.dockbox.hartshorn.hsl.callable;

import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;

public interface ErrorReporter {
    void error(Phase phase, int line, String message);

    void error(Phase phase, Token token, String message);
}
