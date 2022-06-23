package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class UnaryExpression extends Expression {

    private final Token operator;
    private final Expression rightExp;

    public UnaryExpression(final Token operator, final Expression rightExp) {
        this.operator = operator;
        this.rightExp = rightExp;
    }

    public Token operator() {
        return this.operator;
    }

    public Expression rightExpression() {
        return this.rightExp;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
