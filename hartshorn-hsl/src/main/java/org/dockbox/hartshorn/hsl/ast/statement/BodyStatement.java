package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.token.Token;

public abstract class BodyStatement extends Statement {

    private final BlockStatement body;

    protected BodyStatement(final int line, final BlockStatement body) {
        super(line);
        this.body = body;
    }

    protected BodyStatement(final Token at, final BlockStatement body) {
        super(at);
        this.body = body;
    }

    public BlockStatement body() {
        return this.body;
    }
}
