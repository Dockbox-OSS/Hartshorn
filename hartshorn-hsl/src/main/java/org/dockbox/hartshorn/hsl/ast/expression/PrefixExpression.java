package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class PrefixExpression extends Expression {

    private final Token prefixFunName;
    private final Expression rightExpression;

    public PrefixExpression(final Token prefixFunName, final Expression rightExpression) {
        super(prefixFunName);
        this.prefixFunName = prefixFunName;
        this.rightExpression = rightExpression;
    }

    public Token prefixOperatorName() {
        return this.prefixFunName;
    }

    public Expression rightExpression() {
        return this.rightExpression;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
