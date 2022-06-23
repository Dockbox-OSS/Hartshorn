package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class BinaryExpression extends Expression {

    private final Expression leftExp;
    private final Token operator;
    private final Expression rightExp;

    public BinaryExpression(final Expression leftExp, final Token operator, final Expression rightExp) {
        this.leftExp = leftExp;
        this.operator = operator;
        this.rightExp = rightExp;
    }

    public Expression leftExpression() {
        return this.leftExp;
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
