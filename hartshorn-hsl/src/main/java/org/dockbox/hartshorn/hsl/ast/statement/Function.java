package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.token.Token;

public abstract class Function extends Statement {

    protected Function(final int line) {
        super(line);
    }

    protected Function(final Token at) {
        super(at);
    }
}
