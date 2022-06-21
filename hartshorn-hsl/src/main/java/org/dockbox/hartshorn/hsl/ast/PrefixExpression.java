package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class PrefixExpression extends Expression {

    private final Token prefixFunName;
    private final Expression rightExpression;

    public PrefixExpression(final Token prefixFunName, final Expression rightExpression) {
        this.prefixFunName = prefixFunName;
        this.rightExpression = rightExpression;
    }

    public Token getPrefixOperatorName() {
        return this.prefixFunName;
    }

    public Expression getRightExpression() {
        return this.rightExpression;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
