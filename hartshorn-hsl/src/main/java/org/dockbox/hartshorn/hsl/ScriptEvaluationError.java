package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;

public class ScriptEvaluationError extends RuntimeException {

    private final Phase phase;
    private final int line;
    private final int column;
    private final Token at;

    public ScriptEvaluationError(final String message, final Phase phase, final int line, final int column) {
        this(null, message, phase, null, line, column);
    }

    public ScriptEvaluationError(final String message, final Phase phase, final Token at) {
        this(null, message, phase, at, at.line(), at.column());
    }

    public ScriptEvaluationError(final Throwable cause, final Phase phase, final Token at) {
        this(cause, cause.getMessage(), phase, at, at.line(), at.column());
    }

    public ScriptEvaluationError(final Throwable cause, final String message, final Phase phase, final Token at, final int line, final int column) {
        super(message, cause);
        this.phase = phase;
        this.at = at;
        this.line = line;
        this.column = column;
    }

    public Token at() {
        return at;
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }

    public Phase phase() {
        return phase;
    }
}
