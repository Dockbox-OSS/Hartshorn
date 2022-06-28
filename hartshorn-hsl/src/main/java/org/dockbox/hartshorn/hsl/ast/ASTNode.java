package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;

public abstract class ASTNode {

    private final int line;

    protected ASTNode(final int line) {
        this.line = line;
    }

    protected ASTNode(final Token at) {
        this.line = at.line();
    }

    public int line() {
        return this.line;
    }
}
