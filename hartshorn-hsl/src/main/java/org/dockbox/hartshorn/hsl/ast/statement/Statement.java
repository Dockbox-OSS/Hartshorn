package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public abstract class Statement extends ASTNode {

    protected Statement(final int line) {
        super(line);
    }

    protected Statement(final Token at) {
        super(at);
    }

    public abstract <R> R accept(StatementVisitor<R> visitor);
}
