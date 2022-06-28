package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public abstract class Expression extends ASTNode {

    protected Expression(final int line) {
        super(line);
    }

    protected Expression(final Token at) {
        super(at);
    }

    public abstract <R> R accept(ExpressionVisitor<R> visitor);
}
